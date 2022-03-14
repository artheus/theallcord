package se.artheus.fabric.gas.api;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public abstract class Gas extends Fluid {
    public static final DefaultedRegistry<Gas> GAS_REGISTRY = new DefaultedRegistry<>("gas", ResourceKey.createRegistryKey(new ResourceLocation("fagric:gas")), Lifecycle.experimental());
}
