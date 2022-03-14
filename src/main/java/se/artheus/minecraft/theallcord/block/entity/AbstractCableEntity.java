package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import appeng.me.ManagedGridNode;
import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.collections4.list.SetUniqueList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.block.AbstractBlockCable;
import se.artheus.minecraft.theallcord.cable.CableConnections;
import se.artheus.minecraft.theallcord.cable.EnergyCableType;
import se.artheus.minecraft.theallcord.cable.Network;
import se.artheus.minecraft.theallcord.tick.TickTrackingEntity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractCableEntity extends AbstractPoweredNetworkEntity implements RenderAttachmentBlockEntity, TickTrackingEntity {

    private long lastTick = 0;
    private boolean shouldUpdate = true;
    private boolean initialized = false;

    private final Map<AEColor, IManagedGridNode> managedNodes = new HashMap<>();
    private final List<Storage<ItemVariant>> itemStorages = SetUniqueList.setUniqueList(new ArrayList<>());
    private final List<Storage<FluidVariant>> fluidStorages = SetUniqueList.setUniqueList(new ArrayList<>());

    public AbstractCableEntity(EnergyCableType cableType, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(cableType, blockEntityType, blockPos, blockState);

        Preconditions.checkArgument(blockState.getBlock() instanceof AbstractBlockCable<?>);

        for (AEColor color : ((AbstractBlockCable<?>) blockState.getBlock()).colors()) {
            var managedNode = new ManagedGridNode(this, new GridListener())
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

    public void updateCableConnections() {
        if (level == null) return;

        level.setBlockAndUpdate(worldPosition, CableConnections.connectToNearbyEntities(level, worldPosition));
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

    public List<Storage<FluidVariant>> getFluidStorages() {
        return this.fluidStorages;
    }

    public boolean addFluidStorage(Storage<FluidVariant> fluidStorage) {
        return this.fluidStorages.add(fluidStorage);
    }

    public boolean removeFluidStorage(Storage<FluidVariant> fluidStorage) {
        return this.fluidStorages.remove(fluidStorage);
    }

    public List<Storage<ItemVariant>> getItemStorages() {
        return this.itemStorages;
    }

    public boolean addItemStorage(Storage<ItemVariant> itemStorage) {
        return this.itemStorages.add(itemStorage);
    }

    public boolean removeItemStorage(Storage<ItemVariant> itemStorage) {
        return this.itemStorages.remove(itemStorage);
    }

    @Override
    public long getLastTick() {
        return this.lastTick;
    }

    @Override
    public void updateTick(long tick) {
        this.lastTick = tick;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        this.managedNodes.forEach((color, node) -> node.destroy());
    }

    @Override
    public boolean isOnline() {
        var isOnline = true;

        for (var node : managedNodes.values()) {
            if (node.getNode() == null || !node.getNode().isPowered()) {
                isOnline = false;
                break;
            }
        }

        return isOnline;
    }

    @Override
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

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (this.shouldUpdate()) this.updateCableConnections();
    }

    private static final class GridListener extends AbstractGridNodeListener<AbstractCableEntity> {
    }
}
