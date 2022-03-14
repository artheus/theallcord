package se.artheus.fabric.gas.api;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import se.artheus.fabric.gas.api.base.EmptyBucketStorage;
import se.artheus.fabric.gas.api.base.FullItemGasStorage;
import se.artheus.fabric.gas.impl.GasImpl;

@SuppressWarnings("UnstableApiUsage")
public final class GasStorage {

    public static final BlockApiLookup<Storage<GasVariant>, Direction> SIDED =
            BlockApiLookup.get(new ResourceLocation("fabric:sided_gas_storage"), Storage.asClass(), Direction.class);


    public static final ItemApiLookup<Storage<GasVariant>, ContainerItemContext> ITEM =
            ItemApiLookup.get(new ResourceLocation("fabric:gas_storage"), Storage.asClass(), ContainerItemContext.class);

    public static final Storage<GasVariant> EMPTY = GasImpl.EMPTY;

    /**
     * Allows multiple API providers to return {@code Storage<GasVariant>} implementations for some items.
     * {@link #combinedItemApiProvider} is per-item while this one is queried for all items, hence the "general" name.
     *
     * <p>Implementation note: This event is invoked both through an API Lookup fallback, and by the {@code combinedItemApiProvider} events.
     * This means that per-item combined providers registered through {@code combinedItemApiProvider} DO NOT prevent these general providers from running,
     * however regular providers registered through {@code ItemApiLookup#register...} that return a non-null API instance DO prevent it.
     */
    public static Event<GasStorage.CombinedItemApiProvider> GENERAL_COMBINED_PROVIDER = CombinedProvidersImpl.createEvent(false);

    static {
        // Ensure that the lookup is only queried on the server side.
        GasStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            Preconditions.checkArgument(!world.isClientSide(), "Sided gas storage may only be queried for a server world.");
            return null;
        });

        // Register combined fallback
        GasStorage.ITEM.registerFallback((stack, context) -> GENERAL_COMBINED_PROVIDER.invoker().find(context));

        // Register full bucket storage
        GENERAL_COMBINED_PROVIDER.register(context -> {
            if (context.getItemVariant().getItem() instanceof BucketItem bucketItem) {
                Fluid bucketFluid = ((BucketItemAccessor) bucketItem).fabric_getFluid();

                // Make sure the mapping is bidirectional.
                if (bucketFluid != null && bucketFluid.getBucket() == bucketItem) {
                    return new FullItemGasStorage(context, Items.BUCKET, GasVariant.of(bucketFluid), FluidConstants.BUCKET);
                }
            }

            return null;
        });
    }

    private GasStorage() {
    }

    /**
     * Get or create and register a {@link GasStorage.CombinedItemApiProvider} event for the passed item.
     * Allows multiple API providers to provide a {@code Storage<GasVariant>} implementation for the same item.
     *
     * <p>When the item is queried for an API through {@link #ITEM}, all the providers registered through the event will be invoked.
     * All non-null {@code Storage<GasVariant>} instances returned by the providers will be combined in a single storage,
     * that will be the final result of the query, or {@code null} if no storage is offered by the event handlers.
     *
     * <p>This is appropriate to use when multiple mods could wish to expose the Gas API for some items,
     * for example when dealing with items added by the base Minecraft game such as buckets or empty bottles.
     * A typical usage example is a mod adding support for filling empty bottles with a honey gas:
     * Fabric API already registers a storage for empty bottles to allow filling them with water through the event,
     * and a mod can register an event handler that will attach a second storage allowing empty bottles to be filled with its honey gas.
     *
     * @throws IllegalStateException If an incompatible provider is already registered for the item.
     */
    public static Event<GasStorage.CombinedItemApiProvider> combinedItemApiProvider(Item item) {
        return CombinedProvidersImpl.getOrCreateItemEvent(item);
    }

    @FunctionalInterface
    public interface CombinedItemApiProvider {
        /**
         * Return a {@code Storage<GasVariant>} if available in the given context, or {@code null} otherwise.
         * The current item variant can be {@linkplain ContainerItemContext#getItemVariant() retrieved from the context}.
         */
        @Nullable
        Storage<GasVariant> find(ContainerItemContext context);
    }


}
