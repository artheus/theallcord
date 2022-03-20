package se.artheus.minecraft.theallcord.networking.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.networking.AbstractOfferedType;
import team.reborn.energy.api.EnergyStorage;

public class OfferedEnergyStorage extends AbstractOfferedType<EnergyStorage, Direction> {
    public OfferedEnergyStorage(ServerLevel level, BlockPos blockPos, Direction direction) {
        super(level, blockPos, direction, EnergyStorage.SIDED);
    }
}
