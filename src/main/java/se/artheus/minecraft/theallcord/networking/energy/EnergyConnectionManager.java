package se.artheus.minecraft.theallcord.networking.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.networking.AbstractTransferableStorageConnectionManager;
import se.artheus.minecraft.theallcord.tick.ITickingEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnergyConnectionManager extends AbstractTransferableStorageConnectionManager<EnergyStorage, Direction, OfferedEnergyStorage> implements EnergyStorage, ITickingEntity {

    public EnergyConnectionManager(@NotNull ServerLevel level, @NotNull AbstractNetworkCableEntity entity) {
        super(
            level,
            entity,
            entity.getEnergyTransferRate() * Direction.values().length
        );
    }

    @Override
    public void setBlocked(boolean blocked) {
        super.setBlocked(blocked);
    }

    @Override
    public BlockPos getBlockPos() {
        return getEntity().getBlockPos();
    }

    @Override
    public boolean isRemoved() {
        return getEntity().isRemoved();
    }

    @Override
    public void tick(ServerLevel level) {
        ITickingEntity.super.tick(level);

        connectedSides.clear();
        connectedSides.addAll(getTargets().stream()
            .filter(target -> Objects.nonNull(target.find()))
            .map(o -> o.context().getOpposite())
            .collect(Collectors.toSet())
        );

        PoweredEntityTickManager.handlePoweredEntityTick(level, this);
    }

    @Override
    public void initialize(ServerLevel level) {
        ITickingEntity.super.initialize(level);

        Arrays.stream(Direction.values())
            .forEach(dir -> this.addTarget(
                new OfferedEnergyStorage(level, entity.getBlockPos().relative(dir), dir.getOpposite())
            ));
    }


}
