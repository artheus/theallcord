package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import appeng.me.ManagedGridNode;
import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.block.AbstractBlockCable;
import se.artheus.minecraft.theallcord.cable.CableConnections;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCableEntity extends AbstractEntity implements RenderAttachmentBlockEntity {

    private final Map<AEColor, IManagedGridNode> managedNodes = new HashMap<>();

    private boolean shouldUpdate = true;
    private boolean initialized = false;

    private final boolean dense;

    public AbstractCableEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        Preconditions.checkArgument(blockState.getBlock() instanceof AbstractBlockCable<?>);

        this.dense = ((AbstractBlockCable<?>) blockState.getBlock()).isDense();

        var gridListener = new GridListener<>(this);
        var colors = ((AbstractBlockCable<?>) blockState.getBlock()).colors();

        for (AEColor color : colors) {
            var managedNode = new ManagedGridNode(this, gridListener)
                    .setInWorldNode(false)
                    .setGridColor(color)
                    .setIdlePowerUsage(0.0)
                    .setTagName("%s_%s".formatted(this.getTagPrefix(), color.getEnglishName().toLowerCase()))
                    .setExposedOnSides(EnumSet.noneOf(Direction.class));

            if (this.isDense()) {
                managedNode.setFlags(GridFlags.DENSE_CAPACITY);
            }

            managedNodes.put(color, managedNode);
        }
    }

    public abstract String getTagPrefix();

    public boolean shouldUpdate() {
        return this.shouldUpdate;
    }

    public void flagForUpdate() {
        this.shouldUpdate = true;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        TickHandler.instance().addInit(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        this.managedNodes.forEach((color, node) -> node.destroy());
    }

    @Override
    public boolean isOnline() {
        var isOnline = true;

        for(var node : managedNodes.values()) {
            if (node.getNode() == null || !node.getNode().isPowered()) {
                isOnline = false;
                break;
            }
        }

        return isOnline;
    }

    public void initialize() {
        if (level == null) return;
        if (initialized) return;

        this.managedNodes.forEach((color, managedNode) -> {
            if (managedNode.getNode() == null) {
                managedNode.create(level, worldPosition);
            }
        });

        initialized = true;
    }

    public void updateCableConnections() {
        if (level == null) return;

        level.setBlockAndUpdate(worldPosition, CableConnections.connectToNearbyGrid(level, worldPosition));
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Map<AEColor, IManagedGridNode> getManagedNodes() {
        return this.managedNodes;
    }

    public IManagedGridNode getManagedNodeByColor(AEColor color) {
        return this.managedNodes.get(color);
    }

    public boolean isDense() {
        return dense;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        this.managedNodes.forEach((color, node) -> node.saveToNBT(compoundTag));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        this.managedNodes.forEach((color, node) -> node.loadFromNBT(tag));
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return null;
    }

    protected record GridListener<T extends AbstractCableEntity>(T owner) implements IGridNodeListener<T> {
        @Override
        public void onSecurityBreak(T nodeOwner, IGridNode node) {
        }

        @Override
        public void onSaveChanges(T nodeOwner, IGridNode node) {
        }

        @Override
        public void onGridChanged(T nodeOwner, IGridNode node) {
            nodeOwner.setChanged();
        }
    }
}
