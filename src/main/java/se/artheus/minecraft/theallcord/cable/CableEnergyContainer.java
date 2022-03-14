package se.artheus.minecraft.theallcord.cable;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

public class CableEnergyStorage extends SimpleSidedEnergyContainer {
    public final long capacity;
    public final long maxInsert, maxExtract;

    public CableEnergyStorage(boolean dense, long capacity) {
        super();

        capacity = dense ? capacity * 4 : capacity;

        this.capacity = capacity * Direction.values().length;
        this.maxInsert = capacity;
        this.maxExtract = capacity;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getMaxInsert(@Nullable Direction side) {
        return maxInsert;
    }

    @Override
    public long getMaxExtract(@Nullable Direction side) {
        return maxExtract;
    }
}
