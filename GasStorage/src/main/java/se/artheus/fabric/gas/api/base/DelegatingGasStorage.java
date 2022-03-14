package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A gas storage that delegates to another gas storage,
 * with an optional boolean supplier to check that the storage is still valid.
 * This can be used for easier item gas storage implementation, or overridden for custom delegation logic.
 */
@SuppressWarnings({"UnstableApiUsage"})
public class DelegatingGasStorage implements Storage<GasVariant> {
    protected final Supplier<Storage<GasVariant>> backingStorage;
    protected final BooleanSupplier validPredicate;

    /**
     * Create a new instance.
     *
     * @param backingStorage Storage to delegate to.
     * @param validPredicate A function that can return false to prevent any operation, or true to call the delegate as usual.
     *                       {@code null} can be passed if no filtering is necessary.
     */
    public DelegatingGasStorage(Storage<GasVariant> backingStorage, @Nullable BooleanSupplier validPredicate) {
        this(() -> backingStorage, validPredicate);
        Objects.requireNonNull(backingStorage);
    }


    /**
     * More general constructor that allows the backing storage to change over time.
     */
    public DelegatingGasStorage(Supplier<Storage<GasVariant>> backingStorage, @Nullable BooleanSupplier validPredicate) {
        this.backingStorage = Objects.requireNonNull(backingStorage);
        this.validPredicate = validPredicate == null ? () -> true : validPredicate;
    }

    @Override
    public boolean supportsInsertion() {
        return validPredicate.getAsBoolean() && backingStorage.get().supportsInsertion();
    }

    @Override
    public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        if (validPredicate.getAsBoolean()) {
            return backingStorage.get().insert(resource, maxAmount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public boolean supportsExtraction() {
        return validPredicate.getAsBoolean() && backingStorage.get().supportsExtraction();
    }

    @Override
    public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        if (validPredicate.getAsBoolean()) {
            return backingStorage.get().extract(resource, maxAmount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
        return null;
    }
}
