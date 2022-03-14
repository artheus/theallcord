package se.artheus.fabric.gas.api.base;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import se.artheus.fabric.gas.api.GasVariant;
import se.artheus.fabric.gas.impl.SimpleItemGasStorageImpl;

public interface SimpleGasStorageItem {
    String GAS_KEY = "gas";

    /**
     * Return a base gas storage implementation for items, with fixed capacity, and per-operation insertion and extraction limits.
     * This is used internally for items that implement SimpleBatteryItem, but it may also be used outside of that.
     * The gas is stored in the {@code gas} tag of the stacks, the same as the constant {@link #GAS_KEY}.
     *
     * <p>Stackable gas containers are supported just fine, and they will distribute gas evenly.
     * For example, insertion of 3 units of gas into a stack of 2 items using this class will either insert 0 or 2 depending on the remaining capacity.
     */
    static Storage<GasVariant> createStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
        return SimpleItemGasStorageImpl.createSimpleStorage(ctx, capacity, maxInsert, maxExtract);
    }

    /**
     * @return The currently stored gas, ignoring the count and without checking the current item.
     */
    static long getStoredGasUnchecked(ItemStack stack) {
        return getStoredGasUnchecked(stack.getTag());
    }

    /**
     * @return The currently stored gas of this raw tag.
     */
    static long getStoredGasUnchecked(@Nullable CompoundTag nbt) {
        return nbt != null ? nbt.getLong(GAS_KEY) : 0;
    }

    /**
     * Set the gas, ignoring the count and without checking the current item.
     */
    static void setStoredGasUnchecked(ItemStack stack, long newAmount) {
        if (newAmount == 0) {
            // Make sure newly crafted gas containers stack with emptied ones.
            stack.removeTagKey(GAS_KEY);
        } else {
            stack.getOrCreateTag().putLong(GAS_KEY, newAmount);
        }
    }

    /**
     * @return The max gas that can be stored in this item.
     */
    long getGasCapacity();

    /**
     * @return The max amount of gas that can be inserted in this item in a single operation.
     */
    long getGasMaxInput();

    /**
     * @return The max amount of gas that can be extracted from this item in a single operation.
     */
    long getGasMaxOutput();

    /**
     * @return The gas stored in the stack. Count is ignored.
     */
    default long getStoredGas(ItemStack stack) {
        return getStoredGasUnchecked(stack);
    }

    /**
     * Set the gas stored in the stack. Count is ignored.
     */
    default void setStoredGas(ItemStack stack, long newAmount) {
        setStoredGasUnchecked(stack, newAmount);
    }

    /**
     * Try to use exactly {@code amount} gas if there is enough available and return true if successful,
     * otherwise do nothing and return false.
     *
     * @throws IllegalArgumentException If the count of the stack is not exactly 1!
     */
    default boolean tryUseGas(ItemStack stack, long amount) {
        if (stack.getCount() != 1) {
            throw new IllegalArgumentException("Invalid count: " + stack.getCount());
        }

        long newAmount = getStoredGas(stack) - amount;

        if (newAmount < 0) {
            return false;
        } else {
            setStoredGas(stack, newAmount);
            return true;
        }
    }

}
