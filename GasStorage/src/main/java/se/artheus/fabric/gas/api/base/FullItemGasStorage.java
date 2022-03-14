package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.Item;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.function.Function;

public final class FullItemGasStorage implements ExtractionOnlyStorage<GasVariant>, SingleSlotStorage<GasVariant> {
    private final ContainerItemContext context;
    private final Item fullItem;
    private final Function<ItemVariant, ItemVariant> fullToEmptyMapping;
    private final GasVariant containedGas;
    private final long containedAmount;

    /**
     * Create a new instance.
     *
     * @param context         The current context.
     * @param emptyItem       The new item after a successful extract operation.
     * @param containedGas    The contained gas variant.
     * @param containedAmount How much of {@code containedGas} is contained.
     */
    public FullItemGasStorage(ContainerItemContext context, Item emptyItem, GasVariant containedGas, long containedAmount) {
        this(context, fullVariant -> ItemVariant.of(emptyItem, fullVariant.getNbt()), containedGas, containedAmount);
    }

    /**
     * Create a new instance, with a custom mapping function.
     * The mapping function allows customizing how the NBT of the empty item depends on the NBT of the full item.
     * The default behavior with the other constructor is to just copy the full NBT.
     *
     * @param context            The current context.
     * @param fullToEmptyMapping A function mapping the full item variant, to the variant that should be used
     *                           for the empty item after a successful extract operation.
     * @param containedGas       The contained gas variant.
     * @param containedAmount    How much of {@code containedGas} is contained.
     */
    public FullItemGasStorage(ContainerItemContext context, Function<ItemVariant, ItemVariant> fullToEmptyMapping, GasVariant containedGas, long containedAmount) {
        StoragePreconditions.notBlankNotNegative(containedGas, containedAmount);

        this.context = context;
        this.fullItem = context.getItemVariant().getItem();
        this.fullToEmptyMapping = fullToEmptyMapping;
        this.containedGas = containedGas;
        this.containedAmount = containedAmount;
    }

    @Override
    public long extract(GasVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        // If the context's item is not fullItem anymore, can't extract!
        if (!context.getItemVariant().isOf(fullItem)) return 0;

        // Make sure that the gas and the amount match.
        if (resource.equals(containedGas) && maxAmount >= containedAmount) {
            // If that's ok, just convert one of the full item into the empty item, copying the nbt.
            ItemVariant newVariant = fullToEmptyMapping.apply(context.getItemVariant());

            if (context.exchange(newVariant, 1, transaction) == 1) {
                // Conversion ok!
                return containedAmount;
            }
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public GasVariant getResource() {
        // Only contains a resource if the item of the context is still this one.
        if (context.getItemVariant().isOf(fullItem)) {
            return containedGas;
        } else {
            return GasVariant.blank();
        }
    }

    @Override
    public long getAmount() {
        if (context.getItemVariant().isOf(fullItem)) {
            return containedAmount;
        } else {
            return 0;
        }
    }

    @Override
    public long getCapacity() {
        // Capacity is the same as the amount.
        return getAmount();
    }
}
