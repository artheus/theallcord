package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import se.artheus.fabric.gas.api.Gas;
import se.artheus.fabric.gas.api.GasVariant;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class EmptyBucketStorage implements InsertionOnlyStorage<GasVariant> {
    private final ContainerItemContext context;

    public EmptyBucketStorage(ContainerItemContext context) {
        this.context = context;
    }

    @Override
    public long insert(GasVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if (!context.getItemVariant().isOf(Items.BUCKET)) return 0;

        Item fullBucket = resource.getGas().getBucket();

        // Make sure the resource is a correct gas mapping: the gas <-> bucket mapping must be bidirectional.
        if (fullBucket instanceof BucketItemAccessor accessor && accessor.fabric_getFluid() instanceof Gas && resource.isOf((Gas) accessor.fabric_getFluid())) {
            if (maxAmount >= FluidConstants.BUCKET) {
                ItemVariant newVariant = ItemVariant.of(fullBucket, context.getItemVariant().getNbt());

                if (context.exchange(newVariant, 1, transaction) == 1) {
                    return FluidConstants.BUCKET;
                }
            }
        }

        return 0;
    }

    @Override
    public Iterator<StorageView<GasVariant>> iterator(TransactionContext transaction) {
        return SingleViewIterator.create(new BlankVariantView<>(GasVariant.blank(), FluidConstants.BUCKET), transaction);
    }
}