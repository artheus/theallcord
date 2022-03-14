package se.artheus.fabric.gas.impl;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import se.artheus.fabric.gas.api.Gas;
import se.artheus.fabric.gas.api.GasVariant;
import se.artheus.fabric.gas.api.GasVariantCache;
import se.artheus.fabric.gas.api.Gases;

import java.util.Objects;

import static se.artheus.fabric.gas.api.Gas.GAS_REGISTRY;

@SuppressWarnings("UnstableApiUsage")
public class GasVariantImpl implements GasVariant {

    private static final Logger LOGGER = LogManager.getLogger("fabric-transfer-api-v1/gas");
    private final Gas gas;
    private final @Nullable CompoundTag nbt;
    private final int hashCode;

    public GasVariantImpl(Gas gas, CompoundTag nbt) {
        this.gas = gas;
        this.nbt = nbt == null ? null : nbt.copy(); // defensive copy
        this.hashCode = Objects.hash(this.gas, nbt);
    }

    public static GasVariant of(Gas gas, @Nullable CompoundTag nbt) {
        Objects.requireNonNull(gas, "Gas may not be null.");

        if (!gas.isSource(gas.defaultFluidState()) && gas != Gases.EMPTY) {
            // Note: the empty gas is not still, that's why we check for it specifically.
            throw new IllegalArgumentException("Gas may not be flowing.");
        }

        if (nbt == null || gas == Gases.EMPTY) {
            // Use the cached variant inside the gas
            return ((GasVariantCache) gas).fabric_getCachedGasVariant();
        } else {
            // TODO explore caching gas variants for non null tags.
            return new GasVariantImpl(gas, nbt);
        }
    }

    @Override
    public boolean isBlank() {
        return gas == Gases.EMPTY;
    }

    @Override
    public Gas getObject() {
        return gas;
    }

    @Override
    public @Nullable CompoundTag getNbt() {
        return nbt;
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag result = new CompoundTag();
        result.putString("gas", Registry.FLUID.getKey(gas).toString());

        if (nbt != null) {
            result.put("tag", nbt.copy());
        }

        return result;
    }

    public static GasVariant fromNbt(CompoundTag compound) {
        try {
            Gas gas = GAS_REGISTRY.get(new ResourceLocation(compound.getString("gas")));
            CompoundTag nbt = compound.contains("tag") ? compound.getCompound("tag") : null;
            return of(gas, nbt);
        } catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load an invalid GasVariant from NBT: {}", compound, runtimeException);
            return GasVariant.blank();
        }
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        if (isBlank()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeVarInt(Registry.FLUID.getId(gas));
            buf.writeNbt(nbt);
        }
    }

    public static GasVariant fromPacket(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return GasVariant.blank();
        } else {
            Gas gas = GAS_REGISTRY.byId(buf.readVarInt());
            CompoundTag nbt = buf.readNbt();
            return of(gas, nbt);
        }
    }

    @Override
    public String toString() {
        return "GasVariantImpl{gas=" + gas + ", tag=" + nbt + '}';
    }

    @Override
    public boolean equals(Object o) {
        // succeed fast with == check
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GasVariantImpl gasVariant = (GasVariantImpl) o;
        // fail fast with hash code
        return hashCode == gasVariant.hashCode && gas == gasVariant.gas && nbtMatches(gasVariant.nbt);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
