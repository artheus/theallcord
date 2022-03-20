package se.artheus.minecraft.theallcord.networking.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.networking.AbstractOfferedType;

@SuppressWarnings("UnstableApiUsage")
public class OfferedFluidStorage extends AbstractOfferedType<Storage<FluidVariant>, Direction> {
    public OfferedFluidStorage(ServerLevel level, BlockPos blockPos, Direction direction) {
        super(level, blockPos, direction, FluidStorage.SIDED);
    }
}