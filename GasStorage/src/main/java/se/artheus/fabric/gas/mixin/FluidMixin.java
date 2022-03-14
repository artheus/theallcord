package se.artheus.fabric.gas.mixin;

import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import se.artheus.fabric.gas.api.Gas;
import se.artheus.fabric.gas.api.GasVariant;
import se.artheus.fabric.gas.api.GasVariantCache;
import se.artheus.fabric.gas.impl.GasVariantImpl;

/**
 * Cache the FluidVariant with a null tag inside each Fluid directly.
 */
@Mixin(Fluid.class)
@SuppressWarnings("unused")
public class FluidMixin implements GasVariantCache {
    private GasVariant fabric_cachedGasVariant;

    @Nullable
    @Override
    public GasVariant fabric_getCachedGasVariant() {
        if (fabric_cachedGasVariant == null && this.getClass().isAssignableFrom(Gas.class)) {
            fabric_cachedGasVariant = new GasVariantImpl((Gas) (Object) this, null);
        }

        return fabric_cachedGasVariant;
    }

    @Nullable
    @Override
    public Gas fabric_asGas() {
        var variant = this.fabric_getCachedGasVariant();
        if (variant == null) return null;

        return variant.getGas();
    }
}