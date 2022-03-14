package se.artheus.fabric.gas.api;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import se.artheus.fabric.gas.impl.GasVariantImpl;

@SuppressWarnings("UnstableApiUsage")
public interface GasVariant extends TransferVariant<Gas> {
    /**
     * Retrieve a blank GasVariant.
     */
    static GasVariant blank() {
        return of(Gases.EMPTY);
    }

    /**
     * Retrieve a GasVariant with a gas, and a {@code null} tag.
     */
    static GasVariant of(Gas gas) {
        return of(gas, null);
    }

    static GasVariant of(Fluid fluid) {
        return of(((GasVariantCache) fluid).fabric_asGas(), null);
    }

    /**
     * Retrieve a GasVariant with a gas, and an optional tag.
     */
    static GasVariant of(Gas gas, @Nullable CompoundTag nbt) {
        return GasVariantImpl.of(gas, nbt);
    }

    /**
     * Return the gas of this variant.
     */
    default Gas getGas() {
        return getObject();
    }

    /**
     * Deserialize a variant from an NBT compound tag, assuming it was serialized using {@link #toNbt}.
     *
     * <p>If an error occurs during deserialization, it will be logged with the DEBUG level, and a blank variant will be returned.
     */
    static GasVariant fromNbt(CompoundTag nbt) {
        return GasVariantImpl.fromNbt(nbt);
    }

    /**
     * Read a variant from a packet byte buffer, assuming it was serialized using {@link #toPacket}.
     */
    static GasVariant fromPacket(FriendlyByteBuf buf) {
        return GasVariantImpl.fromPacket(buf);
    }
}