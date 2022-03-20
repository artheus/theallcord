package se.artheus.minecraft.theallcord.networking.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.networking.AbstractTransferableStorageConnectionManager;
import se.artheus.minecraft.theallcord.tick.ITickingEntity;

import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class FluidStorageConnectionManager extends AbstractTransferableStorageConnectionManager<Storage<FluidVariant>, Direction, OfferedFluidStorage> implements ITickingEntity {
    public FluidStorageConnectionManager(@NotNull ServerLevel level, @NotNull AbstractNetworkCableEntity entity) {
        super(
            level,
            entity,
            entity.getFluidTransferRate() * Direction.values().length
        );

        Arrays.stream(Direction.values())
            .forEach(dir -> this.addTarget(
                new OfferedFluidStorage(level, entity.getBlockPos().relative(dir), dir.getOpposite())
            ));
    }

    @Override
    public BlockPos getBlockPos() {
        return getEntity().getBlockPos();
    }

    @Override
    public boolean isRemoved() {
        return getEntity().isRemoved();
    }
}
