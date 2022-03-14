package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.Iterator;

/**
 * An gas storage that can't accept gases, but will allow extracting any amount of gas.
 * Creative gas tanks are a possible use case.
 * {@link #INSTANCE} can be used instead of creating a new object every time.
 */
public class InfiniteGasStorage implements Storage<GasVariant> {
    public static final InfiniteGasStorage INSTANCE = new InfiniteGasStorage();

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
        return null;
    }
}