package se.artheus.fabric.gas.impl;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import se.artheus.fabric.gas.api.GasVariant;
import se.artheus.fabric.gas.api.base.DelegatingGasStorage;
import se.artheus.fabric.gas.api.base.SimpleGasStorageItem;

import java.util.Iterator;

/**
 * Note: instances of this class do not perform any context validation,
 * that is handled by the DelegatingGasStorage they are wrapped behind.
 */
@SuppressWarnings("UnstableApiUsage")
public record SimpleItemGasStorageImpl(ContainerItemContext ctx,
                                       long capacity, long maxInsert, long maxExtract) implements Storage<GasVariant> {
    public static Storage<GasVariant> createSimpleStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
        StoragePreconditions.notNegative(capacity);
        StoragePreconditions.notNegative(maxInsert);
        StoragePreconditions.notNegative(maxExtract);

        Item startingItem = ctx.getItemVariant().getItem();

        return new DelegatingGasStorage(
                new SimpleItemGasStorageImpl(ctx, capacity, maxInsert, maxExtract),
                () -> ctx.getItemVariant().isOf(startingItem) && ctx.getAmount() > 0
        );
    }

    /**
     * Try to set the gas of the stack to {@code gasAmountPerCount}, return true if success.
     */
    private boolean trySetGas(long gasAmountPerCount, long count, TransactionContext transaction) {
        ItemStack newStack = ctx.getItemVariant().toStack();
        SimpleGasStorageItem.setStoredGasUnchecked(newStack, gasAmountPerCount);
        ItemVariant newVariant = ItemVariant.of(newStack);

        // Try to convert exactly `count` items.
        try (Transaction nested = transaction.openNested()) {
            if (ctx.extract(ctx.getItemVariant(), count, nested) == count && ctx.insert(newVariant, count, nested) == count) {
                nested.commit();
                return true;
            }
        }

        return false;
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
        long count = ctx.getAmount();

        long maxAmountPerCount = maxAmount / count;
        long currentAmountPerCount = getAmount() / count;
        long insertedPerCount = Math.min(maxInsert, Math.min(maxAmountPerCount, capacity - currentAmountPerCount));

        if (insertedPerCount > 0) {
            if (trySetGas(currentAmountPerCount + insertedPerCount, count, transaction)) {
                return insertedPerCount * count;
            }
        }

        return 0;
    }

    @Override
    public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
        long count = ctx.getAmount();

        long maxAmountPerCount = maxAmount / count;
        long currentAmountPerCount = getAmount() / count;
        long extractedPerCount = Math.min(maxExtract, Math.min(maxAmountPerCount, currentAmountPerCount));

        if (extractedPerCount > 0) {
            if (trySetGas(currentAmountPerCount - extractedPerCount, count, transaction)) {
                return extractedPerCount * count;
            }
        }

        return 0;
    }

    @Override
    public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
        return null;
    }

    public long getAmount() {
        return ctx.getAmount() * SimpleGasStorageItem.getStoredGasUnchecked(ctx.getItemVariant().getNbt());
    }

    public long getCapacity() {
        return ctx.getAmount() * capacity;
    }
}
