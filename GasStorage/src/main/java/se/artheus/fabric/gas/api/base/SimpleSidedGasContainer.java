package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import se.artheus.fabric.gas.api.GasStorage;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.Iterator;

/**
 * A base gas storage implementation with a dynamic capacity, and per-side per-operation insertion and extraction limits.
 * {@link #getSideStorage} can be used to get an {@link GasStorage} implementation for a given side.
 * Make sure to override {@link #onFinalCommit} to call {@code markDirty} and similar functions.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class SimpleSidedGasContainer extends SnapshotParticipant<Long> {
    private final SideStorage[] sideStorages = new SideStorage[7];
    public long amount = 0;

    public SimpleSidedGasContainer() {
        for (int i = 0; i < 7; ++i) {
            sideStorages[i] = new SideStorage(i == 6 ? null : Direction.from3DDataValue(i));
        }
    }

    /**
     * @return The current capacity of this storage.
     */
    public abstract long getCapacity();

    /**
     * @return The maximum amount of gas that can be inserted in a single operation from the passed side.
     */
    public abstract long getMaxInsert(@Nullable Direction side);

    /**
     * @return The maximum amount of gas that can be extracted in a single operation from the passed side.
     */
    public abstract long getMaxExtract(@Nullable Direction side);

    /**
     * @return An {@link Storage<GasVariant>} implementation for the passed side.
     */
    public Storage<GasVariant> getSideStorage(@Nullable Direction side) {
        return sideStorages[side == null ? 6 : side.get3DDataValue()];
    }

    @Override
    protected Long createSnapshot() {
        return amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot;
    }

    private class SideStorage implements Storage<GasVariant> {
        private final Direction side;

        private SideStorage(Direction side) {
            this.side = side;
        }

        @Override
        public boolean supportsInsertion() {
            return getMaxInsert(side) > 0;
        }

        @Override
        public boolean supportsExtraction() {
            return getMaxExtract(side) > 0;
        }

        @Override
        public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);

            long inserted = Math.min(getMaxInsert(side), Math.min(maxAmount, getCapacity() - amount));

            if (inserted > 0) {
                updateSnapshots(transaction);
                amount += inserted;
                return inserted;
            }

            return 0;
        }

        @Override
        public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);

            long extracted = Math.min(getMaxExtract(side), Math.min(maxAmount, amount));

            if (extracted > 0) {
                updateSnapshots(transaction);
                amount -= extracted;
                return extracted;
            }

            return 0;
        }

        @Override
        public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
            return null;
        }
    }
}
