package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.Iterator;
import java.util.Objects;

/**
 * An gas storage that will apply additional per-insert and per-extract limits to another storage.
 */
@SuppressWarnings("UnstableApiUsage")
public class LimitingGasStorage implements Storage<GasVariant> {
    protected final Storage<GasVariant> backingStorage;
    protected final long maxInsert, maxExtract;

    /**
     * Create a new limiting storage.
     *
     * @param backingStorage Storage to delegate to.
     * @param maxInsert      The maximum amount of gas that can be inserted in one operation.
     * @param maxExtract     The maximum amount of gas that can be extracted in one operation.
     */
    public LimitingGasStorage(Storage<GasVariant> backingStorage, long maxInsert, long maxExtract) {
        Objects.requireNonNull(backingStorage);
        StoragePreconditions.notNegative(maxInsert);
        StoragePreconditions.notNegative(maxExtract);

        this.backingStorage = backingStorage;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    @Override
    public boolean supportsInsertion() {
        return maxInsert > 0 && backingStorage.supportsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0 && backingStorage.supportsExtraction();
    }

    @Override
    public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
        return backingStorage.insert(resource, Math.min(maxAmount, maxInsert), transaction);
    }

    @Override
    public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
        return backingStorage.extract(resource, Math.min(maxAmount, maxExtract), transaction);
    }

    @Override
    public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
        return null;
    }
}
