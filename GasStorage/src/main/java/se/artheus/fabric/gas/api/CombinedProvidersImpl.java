package se.artheus.fabric.gas.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CombinedProvidersImpl {
    public static Event<GasStorage.CombinedItemApiProvider> createEvent(boolean invokeFallback) {
        return EventFactory.createArrayBacked(GasStorage.CombinedItemApiProvider.class, listeners -> context -> {
            List<Storage<GasVariant>> storages = new ArrayList<>();

            for (GasStorage.CombinedItemApiProvider listener : listeners) {
                Storage<GasVariant> found = listener.find(context);

                if (found != null) {
                    storages.add(found);
                }
            }

            // Allow combining per-item combined providers with fallback combined providers.
            if (!storages.isEmpty() && invokeFallback) {
                // Only invoke the fallback if API Lookup doesn't invoke it right after,
                // that is only invoke the fallback if storages were offered,
                // otherwise we can wait for API Lookup to invoke the fallback provider itself.
                Storage<GasVariant> fallbackFound = GasStorage.GENERAL_COMBINED_PROVIDER.invoker().find(context);

                if (fallbackFound != null) {
                    storages.add(fallbackFound);
                }
            }

            return storages.isEmpty() ? null : new CombinedStorage<>(storages);
        });
    }

    public static Event<GasStorage.CombinedItemApiProvider> getOrCreateItemEvent(Item item) {
        // register here is thread-safe, so the query below will return a valid provider (possibly one registered before or from another thread).
        GasStorage.ITEM.registerForItems(new CombinedProvidersImpl.Provider(), item);
        ItemApiLookup.ItemApiProvider<Storage<GasVariant>, ContainerItemContext> existingProvider = GasStorage.ITEM.getProvider(item);

        if (existingProvider instanceof CombinedProvidersImpl.Provider registeredProvider) {
            return registeredProvider.event;
        } else {
            String errorMessage = String.format(
                    "An incompatible provider was already registered for item %s. Provider: %s.",
                    item,
                    existingProvider
            );
            throw new IllegalStateException(errorMessage);
        }
    }

    private static class Provider implements ItemApiLookup.ItemApiProvider<Storage<GasVariant>, ContainerItemContext> {
        private final Event<GasStorage.CombinedItemApiProvider> event = createEvent(true);

        @Override
        @Nullable
        public Storage<GasVariant> find(ItemStack itemStack, ContainerItemContext context) {
            if (!context.getItemVariant().matches(itemStack)) {
                String errorMessage = String.format(
                        "Query stack %s and ContainerItemContext variant %s don't match.",
                        itemStack,
                        context.getItemVariant()
                );
                throw new IllegalArgumentException(errorMessage);
            }

            return event.invoker().find(context);
        }
    }
}
