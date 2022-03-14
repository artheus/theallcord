package se.artheus.minecraft.theallcord.entities.indicators;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.entities.AbstractEntity;
import se.artheus.minecraft.theallcord.entities.AbstractGridNodeListener;
import se.artheus.minecraft.theallcord.tick.TickHandler;

import java.util.EnumSet;

import static se.artheus.minecraft.theallcord.blocks.indicators.AEChannelIndicator.ONLINE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CHANNEL_INDICATOR;
import static se.artheus.minecraft.theallcord.entities.BlockEntities.ENTITY_TYPE_CHANNEL_INDICATOR;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CHANNEL_INDICATOR;

public class AEChannelIndicatorEntity extends AbstractEntity implements IInWorldGridNodeHost {

    private final IManagedGridNode mainNode;
    private boolean initialized = false;

    public AEChannelIndicatorEntity(BlockPos blockPos, BlockState blockState) {
        super(ENTITY_TYPE_CHANNEL_INDICATOR, blockPos, blockState);

        mainNode = GridHelper.createManagedNode(this, new ChannelIndicatorGridListener());
        mainNode.setInWorldNode(true);
        mainNode.setFlags(GridFlags.REQUIRE_CHANNEL);
        mainNode.setExposedOnSides(EnumSet.allOf(Direction.class));
        mainNode.setTagName(ID_BLOCK_CHANNEL_INDICATOR.getPath());
    }

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(BLOCK_ITEM_CHANNEL_INDICATOR);
    }

    @Override
    public boolean isOnline() {
        return this.getBlockState().getValue(ONLINE);
    }

    public void initialize() {
        if (level == null) return;
        if (initialized) return;

        if (mainNode.getNode() == null) {
            mainNode.create(level, worldPosition);
        }

        initialized = true;
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        return mainNode.getNode();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        mainNode.destroy();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        TickHandler.instance().addInit(this);
    }

    private static class ChannelIndicatorGridListener extends AbstractGridNodeListener<AEChannelIndicatorEntity> {
        @Override
        public void onStateChanged(AEChannelIndicatorEntity nodeOwner, IGridNode node, State state) {
            if (nodeOwner.level == null || nodeOwner.worldPosition == null) return;

            if (nodeOwner.getBlockState().getValue(ONLINE) != node.isActive()) {
                nodeOwner.level.setBlockAndUpdate(
                        nodeOwner.worldPosition,
                        nodeOwner.getBlockState().setValue(ONLINE, node.isActive())
                );
            }
        }
    }
}
