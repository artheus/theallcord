package se.artheus.fabric.gas.impl;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import se.artheus.fabric.gas.api.GasStorage;
import se.artheus.fabric.gas.api.GasVariant;
import se.artheus.fabric.gas.api.base.SimpleGasStorageItem;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class GasImpl {
    public static final Storage<GasVariant> EMPTY = new Storage<>() {
        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
            return null;
        }
    };

    static {
        GasStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof SimpleGasStorageItem tank) {
                return SimpleGasStorageItem.createStorage(ctx, tank.getGasCapacity(), tank.getGasMaxInput(), tank.getGasMaxOutput());
            } else {
                return null;
            }
        });
    }
}
