package se.artheus.minecraft.theallcord.networking.energy;

import com.google.common.collect.Queues;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.networking.CableType;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This is pretty much a copy and paste of {@code techreborn.blockentity.cable.CableTickManager}
 * So all the credit should go to the TechReborn team, for this!
 * <p>
 * Some minor modifications are done, just for this class to actually work in project, and to not use deprecated stuff.
 */
@SuppressWarnings("UnstableApiUsage")
@ParametersAreNonnullByDefault
public class PoweredEntityTickManager {
    private static final Set<EnergyConnectionManager> connectionManagerList = Collections.synchronizedSet(new HashSet<>());
    private static final Set<OfferedEnergyStorage> targetStorages = Collections.synchronizedSet(new HashSet<>());
    private static final Deque<EnergyConnectionManager> bfsQueue = Queues.synchronizedDeque(new ArrayDeque<>());

    public static synchronized void handlePoweredEntityTick(ServerLevel level, EnergyConnectionManager startingEnergyConnectionManager) {
        try {
            gatherEntities(level, startingEnergyConnectionManager);
            if (connectionManagerList.size()==0) return;

            // Group all energy into the network.
            long networkCapacity = 0;
            long networkAmount = 0;

            for (EnergyConnectionManager connectionManager : connectionManagerList) {
                // Update entity connections.
                if (connectionManager.isRemoved()) {
                    connectionManager.getTargets().forEach(targetStorages::remove);
                    continue;
                } else {
                    networkAmount += connectionManager.getAmount();
                    networkCapacity += connectionManager.getCapacity();

                    targetStorages.addAll(connectionManager.getTargets());
                }

                // Block any entity I/O while we access the network amount directly.
                // Some things might try to access entities, for example a p2p tunnel pointing back at a entity.
                // If the entities and the network go out of sync, we risk duping or voiding energy.
                connectionManager.setBlocked(true);
            }

            // Just in case.
            if (networkAmount > networkCapacity) {
                networkAmount = networkCapacity;
            }

            // Pull energy from storages.
            networkAmount += dispatchTransfer(startingEnergyConnectionManager.getCableType(), startingEnergyConnectionManager.getEntity().isDense(), EnergyStorage::extract, networkCapacity - networkAmount);
            // Push energy into storages.
            networkAmount -= dispatchTransfer(startingEnergyConnectionManager.getCableType(), startingEnergyConnectionManager.getEntity().isDense(), EnergyStorage::insert, networkAmount);

            // Split energy evenly across entities.
            int entityCount = connectionManagerList.size();
            for (EnergyConnectionManager connectionManager : connectionManagerList) {
                connectionManager.setAmount(networkAmount / entityCount);
                networkAmount -= connectionManager.getAmount();
                entityCount--;
                connectionManager.getEntity().setChanged();
                connectionManager.setBlocked(false);
            }
        } finally {
            connectionManagerList.clear();
            targetStorages.clear();
            bfsQueue.clear();
        }
    }

    private static boolean shouldTickEntity(ServerLevel level, EnergyConnectionManager current) {
        return level.getChunk(current.getBlockPos())!=null;
    }

    /**
     * Perform a BFS to gather all connected ticking entities.
     */
    private static void gatherEntities(ServerLevel level, EnergyConnectionManager start) {
        if (!shouldTickEntity(level, start)) return;

        bfsQueue.add(start);
        connectionManagerList.add(start);

        while (!bfsQueue.isEmpty()) {
            queueAdjacentEntitiesOf(level, bfsQueue.removeFirst());
        }
    }

    /**
     * Add adjacent powered entities in all direction of current powered entity
     *
     * @param current The entity of which to look for adjacent powered entities
     */
    private static void queueAdjacentEntitiesOf(ServerLevel level, EnergyConnectionManager current) {
        for (Direction direction : Direction.values()) {
            var adjEntity = level.getBlockEntity(current.getBlockPos().relative(direction));

            if (adjEntity instanceof AbstractNetworkCableEntity adjPoweredEntity) {
                var adjEnergyConnectionManager = adjPoweredEntity.getEnergyConnectionManager();

                if (Objects.isNull(adjEnergyConnectionManager)) continue;

                if (!connectionManagerList.contains(adjEnergyConnectionManager)
                    && shouldTickEntity(level, adjEnergyConnectionManager)) {
                    bfsQueue.add(adjEnergyConnectionManager);
                    connectionManagerList.add(adjEnergyConnectionManager);
                }
            }
        }
    }

    /**
     * Perform a transfer operation across a list of targets.
     */
    private static long dispatchTransfer(CableType cableType, boolean isDense, TransferOperation operation, long maxAmount) {
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
                var energyStorage = target.storage.find();

                if (energyStorage==null) continue;

                int remainingTargets = sortedTargets.size() - i;
                long remainingAmount = maxAmount - transferredAmount;
                // Limit max amount to the entity transfer rate.
                long targetMaxAmount = Math.min(remainingAmount / remainingTargets, cableType.getEnergyTransferRate(isDense));

                long localTransferred = operation.transfer(energyStorage, targetMaxAmount, transaction);
                if (localTransferred > 0) {
                    transferredAmount += localTransferred;
                }
            }
            transaction.commit();
            return transferredAmount;
        }
    }

    @ParametersAreNonnullByDefault
    private interface TransferOperation {
        long transfer(EnergyStorage storage, long maxAmount, Transaction transaction);
    }

    @ParametersAreNonnullByDefault
    private static class SortableStorage {
        private final OfferedEnergyStorage storage;
        private final long simulationResult;

        SortableStorage(TransferOperation operation, OfferedEnergyStorage storage) {
            this.storage = storage;

            var energyStorage = storage.find();

            if (energyStorage!=null) {
                try (Transaction tx = Transaction.openOuter()) {
                    this.simulationResult = operation.transfer(energyStorage, Long.MAX_VALUE, tx);
                }
            } else {
                this.simulationResult = 0;
            }
        }
    }
}
