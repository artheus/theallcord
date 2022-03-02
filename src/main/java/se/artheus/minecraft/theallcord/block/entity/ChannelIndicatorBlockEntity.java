package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.networking.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import static se.artheus.minecraft.theallcord.block.Blocks.CHANNEL_INDICATOR_BLOCK_ITEM;
import static se.artheus.minecraft.theallcord.block.ChannelIndicatorBlock.HAS_CHANNEL;
import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.CHANNEL_INDICATOR_ENTITY;

public class ChannelIndicatorBlockEntity extends AbstractEntity implements IInWorldGridNodeHost {

    public static final String ID = "channel_indicator";

    private final IManagedGridNode mainNode;
    private boolean initialized = false;

    public ChannelIndicatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CHANNEL_INDICATOR_ENTITY, blockPos, blockState);

        mainNode = GridHelper.createManagedNode(this, new IGridNodeListener<>() {
            @Override
            public void onStateChanged(ChannelIndicatorBlockEntity nodeOwner, IGridNode node, State state) {
                if (nodeOwner.level == null || nodeOwner.worldPosition == null) return;

                nodeOwner.level.setBlockAndUpdate(
                        nodeOwner.worldPosition,
                        nodeOwner.getBlockState().setValue(HAS_CHANNEL, node.isActive())
                );
            }

            @Override
            public void onSecurityBreak(ChannelIndicatorBlockEntity nodeOwner, IGridNode node) {
            }

            @Override
            public void onSaveChanges(ChannelIndicatorBlockEntity nodeOwner, IGridNode node) {
            }
        });
        mainNode.setInWorldNode(true);
        mainNode.setFlags(GridFlags.REQUIRE_CHANNEL);
        mainNode.setExposedOnSides(EnumSet.allOf(Direction.class));
        mainNode.setTagName(ID);
    }

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(CHANNEL_INDICATOR_BLOCK_ITEM);
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
}
