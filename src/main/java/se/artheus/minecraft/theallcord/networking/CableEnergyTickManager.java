package se.artheus.minecraft.theallcord.networking;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.entities.cables.AbstractCableEntity;
import se.artheus.minecraft.theallcord.tick.TickCounter;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CableEnergyTickManager {
    private static final List<AbstractCableEntity> cableList = new ArrayList<>();
    private static final List<OfferedEnergyStorage> targetStorages = new ArrayList<>();
    private static final Deque<AbstractCableEntity> bfsQueue = new ArrayDeque<>();

    public static void handleCableTick(AbstractCableEntity startingCable) {
        Preconditions.checkArgument(startingCable.getLevel() instanceof ServerLevel);

        try {
            gatherCables(startingCable);
            if (cableList.size() == 0) return;

            // Group all energy into the network.
            long networkCapacity = 0;
            long networkAmount = 0;

            for (AbstractCableEntity cable : cableList) {
                networkAmount += cable.getEnergyContainer().amount;
                networkCapacity += cable.getEnergyContainer().getCapacity();

                //cable.updateCableConnections();
            }

            // Just in case.
            if (networkAmount > networkCapacity) {
                networkAmount = networkCapacity;
            }

            // Pull energy from storages.
            networkAmount += dispatchTransfer(startingCable.getEnergyContainer(), EnergyStorage::extract, networkCapacity - networkAmount);
            // Push energy into storages.
            networkAmount -= dispatchTransfer(startingCable.getEnergyContainer(), EnergyStorage::insert, networkAmount);

            // Split energy evenly across cables.
            int cableCount = cableList.size();
            for (AbstractCableEntity cable : cableList) {
                cable.getEnergyContainer().amount = networkAmount / cableCount;
                networkAmount -= cable.getEnergyContainer().amount;
                cableCount--;
                cable.setChanged();
                //cable.ioBlocked = false;
            }
        } finally {
            cableList.clear();
            targetStorages.clear();
            bfsQueue.clear();
        }
    }

    private static boolean shouldTickCable(AbstractCableEntity current) {
        // Make sure we only gather and tick each cable once per tick.
        if (TickCounter.equals(current.getLastTick())) return false;
        // Make sure we ignore cables in non-ticking chunks.
        return current.getLevel() instanceof ServerLevel sw && sw.hasChunkAt(current.getBlockPos());
    }

    /**
     * Perform a BFS to gather all connected ticking cables.
     */
    private static void gatherCables(AbstractCableEntity start) {
        if (!shouldTickCable(start)) return;

        bfsQueue.add(start);
        start.updateTick(TickCounter.current());
        cableList.add(start);

        while (!bfsQueue.isEmpty()) {
            AbstractCableEntity current = bfsQueue.removeFirst();

            for (Direction direction : Direction.values()) {
                BlockPos adjPos = current.getBlockPos().relative(direction);

                if (current.getLevel().getBlockEntity(adjPos) instanceof AbstractCableEntity adjCable && current.getEnergyContainer().equals(adjCable.getEnergyContainer())) {
                    if (shouldTickCable(adjCable)) {
                        bfsQueue.add(adjCable);
                        adjCable.updateTick(TickCounter.current());
                        cableList.add(adjCable);
                    }
                }
            }
        }
    }

    /**
     * Perform a transfer operation across a list of targets.
     */
    private static long dispatchTransfer(CableEnergyContainer cableEnergyContainer, TransferOperation operation, long maxAmount) {
        // Build target list.
        List<SortableStorage> sortedTargets = new ArrayList<>();
        for (var storage : targetStorages) {
            sortedTargets.add(new SortableStorage(operation, storage));
        }
        // Shuffle for better average transfer.
        Collections.shuffle(sortedTargets);
        // Sort by lowest simulation target.
        sortedTargets.sort(Comparator.comparingLong(sortableStorage -> sortableStorage.simulationResult));
        // Actually perform the transfer.
        try (Transaction transaction = Transaction.openOuter()) {
            long transferredAmount = 0;
            for (int i = 0; i < sortedTargets.size(); ++i) {
                SortableStorage target = sortedTargets.get(i);
                int remainingTargets = sortedTargets.size() - i;
                long remainingAmount = maxAmount - transferredAmount;
                // Limit max amount to the cable transfer rate.
                long targetMaxAmount = Math.min(remainingAmount / remainingTargets, cableEnergyContainer.getTransferRate());

                long localTransferred = operation.transfer(target.storage.storage, targetMaxAmount, transaction);
                if (localTransferred > 0) {
                    transferredAmount += localTransferred;
                    // Block duplicate operations.
                    target.storage.afterTransfer();
                }
            }
            transaction.commit();
            return transferredAmount;
        }
    }

    private interface TransferOperation {
        long transfer(EnergyStorage storage, long maxAmount, Transaction transaction);
    }

    private static final class SortableStorage {
        private final OfferedEnergyStorage storage;
        private final long simulationResult;

        SortableStorage(CableEnergyTickManager.TransferOperation operation, OfferedEnergyStorage storage) {
            this.storage = storage;
            try (Transaction tx = Transaction.openOuter()) {
                this.simulationResult = operation.transfer(storage.storage, Long.MAX_VALUE, tx);
            }
        }
    }

    private record OfferedEnergyStorage(AbstractCableEntity sourceCable,
                                        Direction direction,
                                        EnergyStorage storage) {

        void afterTransfer() {
            // Block insertions from this side.
            //sourceCable.energyBlockedSides |= 1 << direction.ordinal();
        }
    }
}
