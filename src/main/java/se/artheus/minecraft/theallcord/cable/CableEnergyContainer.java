package se.artheus.minecraft.theallcord.cable;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

import java.util.Objects;

public class CableEnergyContainer extends SimpleSidedEnergyContainer {
    private final boolean isDense;
    private final long capacity;
    private final long transferRate;
    private final EnergyCableType cableType;

    public CableEnergyContainer(boolean isDense, @NotNull EnergyCableType cableType) {
        super();

        this.isDense = isDense;
        this.cableType = cableType;
        this.capacity = cableType.getTransferRate(isDense) * Direction.values().length;
        this.transferRate = cableType.getTransferRate(isDense);
    }

    public EnergyCableType getCableType() {
        return this.cableType;
    }

    public long getTransferRate() {
        return transferRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CableEnergyContainer that = (CableEnergyContainer) o;
        return isDense == that.isDense && cableType == that.cableType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDense, cableType);
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getMaxInsert(@Nullable Direction side) {
        return transferRate;
    }

    @Override
    public long getMaxExtract(@Nullable Direction side) {
        return transferRate;
    }
}
