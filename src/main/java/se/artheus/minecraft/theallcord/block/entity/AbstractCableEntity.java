package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import appeng.me.ManagedGridNode;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.block.AbstractCableBlock;
import se.artheus.minecraft.theallcord.block.CableConnections;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCableEntity extends AbstractEntity {

    private final Map<AEColor, IManagedGridNode> managedNodes = new HashMap<>();

    private final boolean dense;

    private boolean initialized = false;

    public AbstractCableEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        Preconditions.checkArgument(blockState.getBlock() instanceof AbstractCableBlock<?>);

        this.dense = ((AbstractCableBlock<?>) blockState.getBlock()).isDense();

        var gridListener = new GridListener<>(this);

        for (AEColor color : this.getColors()) {
            var managedNode = new ManagedGridNode(this, gridListener)
                    .setInWorldNode(true)
                    .setGridColor(color)
                    .setIdlePowerUsage(0.0)
                    .setFlags(GridFlags.PREFERRED)
                    .setTagName("%s_%s".formatted(this.getTagPrefix(), color.getEnglishName().toLowerCase()))
                    .setExposedOnSides(EnumSet.allOf(Direction.class));

            if (this.isDense()) {
                managedNode.setFlags(GridFlags.DENSE_CAPACITY);
            }

            managedNodes.put(color, managedNode);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();

        this.updateCableConnections();
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

        this.updateCableConnections();
    }

    void updateCableConnections() {
        if (level == null) return;

        var newState = CableConnections.connectToNearbyGrid(
                level,
                worldPosition
        );

        Mod.LOGGER.info("new state is {}", newState);

        level.setBlockAndUpdate(worldPosition, newState);
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

    public abstract AEColor[] getColors();

    public abstract String getTagPrefix();

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
    public void setRemoved() {
        super.setRemoved();

        this.managedNodes.forEach((color, node) -> node.destroy());
    }

    protected record GridListener<T extends AbstractCableEntity>(T owner) implements IGridNodeListener<T> {
        @Override
        public void onSecurityBreak(T nodeOwner, IGridNode node) {
        }

        @Override
        public void onSaveChanges(T nodeOwner, IGridNode node) {
        }
    }
}
