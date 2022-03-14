package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.Iterator;

/**
 * A base gas storage implementation with fixed capacity, and per-operation insertion and extraction limits.
 * Make sure to override {@link #onFinalCommit} to call {@code markDirty} and similar functions.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class SimpleGasStorage extends SnapshotParticipant<Long> implements Storage<GasVariant> {
    public final long capacity;
    public final long maxInsert, maxExtract;
    public long amount = 0;

    public SimpleGasStorage(long capacity, long maxInsert, long maxExtract) {
        StoragePreconditions.notNegative(capacity);
        StoragePreconditions.notNegative(maxInsert);
        StoragePreconditions.notNegative(maxExtract);

        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    @Override
    protected Long createSnapshot() {
        return amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot;
    }

    @Override
    public boolean supportsInsertion() {
        return maxInsert > 0;
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0;
    }

    @Override
    public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(maxInsert, Math.min(maxAmount, capacity - amount));

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

        long extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

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
