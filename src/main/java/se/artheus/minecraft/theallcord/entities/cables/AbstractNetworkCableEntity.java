package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.blocks.BlockCable;
import se.artheus.minecraft.theallcord.entities.AbstractNetworkEntity;
import se.artheus.minecraft.theallcord.networking.CableType;
import se.artheus.minecraft.theallcord.networking.ae2.GridNodeConnectionManager;
import se.artheus.minecraft.theallcord.networking.energy.EnergyConnectionManager;
import se.artheus.minecraft.theallcord.networking.fluid.FluidStorageConnectionManager;
import se.artheus.minecraft.theallcord.networking.item.ItemStorageConnectionManager;
import se.artheus.minecraft.theallcord.tick.TickHandler;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static se.artheus.minecraft.theallcord.blocks.BlockCable.PROPERTY_BY_DIRECTION;

public abstract class AbstractNetworkCableEntity extends AbstractNetworkEntity {

    private final boolean dense;
    private final CableType cableType;

    private EnergyConnectionManager energyConnectionManager;
    private ItemStorageConnectionManager itemStorageConnectionManager;
    private FluidStorageConnectionManager fluidStorageConnectionManager;
    private GridNodeConnectionManager gridNodeConnectionManager;

    public AbstractNetworkCableEntity(CableType cableType, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        this.cableType = cableType;
        this.dense = ((BlockCable<?>) blockState.getBlock()).isDense();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (gridNodeConnectionManager!=null) {
            TickHandler.instance().removeTickingEntity(gridNodeConnectionManager);
            gridNodeConnectionManager.destroy();
        }
    }

    @Override
    public void initialize(ServerLevel level) {
        super.initialize(level);

        energyConnectionManager = new EnergyConnectionManager(level, this);
        itemStorageConnectionManager = new ItemStorageConnectionManager(level, this);
        fluidStorageConnectionManager = new FluidStorageConnectionManager(level, this);
        gridNodeConnectionManager = new GridNodeConnectionManager(level, this);

        TickHandler.instance().addInit(energyConnectionManager);
        TickHandler.instance().addInit(itemStorageConnectionManager);
        TickHandler.instance().addInit(fluidStorageConnectionManager);
        TickHandler.instance().addInit(gridNodeConnectionManager);
    }

    public abstract String getTagPrefix();

    public boolean isDense() {
        return dense;
    }

    public CableType getCableType() {
        return cableType;
    }

    public long getEnergyTransferRate() {
        return cableType.getEnergyTransferRate(dense);
    }

    public long getItemTransferRate() {
        return cableType.getItemTransferRate(dense);
    }

    public long getFluidTransferRate() {
        return cableType.getFluidTransferRate(dense);
    }

    public @Nullable
    EnergyConnectionManager getEnergyConnectionManager() {
        return this.energyConnectionManager;
    }

    public @Nullable
    ItemStorageConnectionManager getItemStorageConnectionManager() {
        return itemStorageConnectionManager;
    }

    public @Nullable
    FluidStorageConnectionManager getFluidStorageConnectionManager() {
        return fluidStorageConnectionManager;
    }

    public @Nullable
    GridNodeConnectionManager getGridNodeConnectionManager() {
        return gridNodeConnectionManager;
    }

    @Override
    public boolean isOnline() {
        if (getGridNodeConnectionManager()==null) return false;

        var isOnline = false;

        for (var managedGridNode : gridNodeConnectionManager.getManagedNodes().values()) {
            var node = managedGridNode.getNode();
            if (Objects.nonNull(node) && node.isPowered()) {
                isOnline = true;
                break;
            }
        }

        return isOnline;
    }

    public void updateCableConnections() {
        this.connectedSides.clear();

        this.connectedSides.addAll(
            Stream.of(
                gridNodeConnectionManager.getConnectedSides(),
                energyConnectionManager.getConnectedSides(),
                itemStorageConnectionManager.getConnectedSides(),
                fluidStorageConnectionManager.getConnectedSides()
            ).flatMap(Set::stream).toList()
        );
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick(ServerLevel level) {
        if (this.isRemoved()
            || Objects.isNull(gridNodeConnectionManager)
            || Objects.isNull(energyConnectionManager)
            || Objects.isNull(itemStorageConnectionManager)
            || Objects.isNull(fluidStorageConnectionManager)) return;

        this.updateCableConnections();

        var state = this.getBlockState().getBlock().defaultBlockState();

        for (var side : this.connectedSides) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(side), true);
        }

        level.setBlockAndUpdate(this.worldPosition, state);
    }
}
