package se.artheus.minecraft.theallcord.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.networking.Network;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractNetworkEntity extends AbstractEntity {

    protected final Set<Direction> connectedSides = EnumSet.noneOf(Direction.class);
    private Network network;

    public AbstractNetworkEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public abstract boolean isOnline();

    @Nullable
    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(@NotNull Network network) {
        this.network = network;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setRemoved() {
        super.setRemoved();

        //Network.removeNodeFor(this);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void clearRemoved() {
        super.clearRemoved();

        //Network.requestNetworkFor(this);
    }

    @Override
    public String toString() {
        return "NetworkEntity{class: %s, pos: %s}".formatted(this.getClass().getSimpleName(), this.getBlockPos().toString());
    }
}
