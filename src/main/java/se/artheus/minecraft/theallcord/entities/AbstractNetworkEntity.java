package se.artheus.minecraft.theallcord.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.Network;
import se.artheus.minecraft.theallcord.tick.ITickingBlockEntity;
import se.artheus.minecraft.theallcord.tick.TickHandler;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class AbstractNetworkEntity extends AbstractEntity implements ITickingBlockEntity {

    private Network network;

    public AbstractNetworkEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        this.network = new Network(this);
    }

    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void mergeNetworksWith(AbstractNetworkEntity entity) {
        this.network = Network.createMergedNetwork(this.network, entity.getNetwork());
        entity.setNetwork(this.network);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void clearRemoved() {
        super.clearRemoved();

        TickHandler.instance().addTickingEntity(this);
    }
}
