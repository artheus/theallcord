package se.artheus.minecraft.theallcord.entities;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.collections4.list.SetUniqueList;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.blocks.cables.AbstractCable;
import se.artheus.minecraft.theallcord.networking.CableEnergyContainer;
import se.artheus.minecraft.theallcord.networking.EnergyCableType;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPoweredNetworkEntity extends AbstractNetworkEntity {

    private final boolean dense;
    private final CableEnergyContainer energyContainer;
    private final List<EnergyStorage> energyStorages = SetUniqueList.setUniqueList(new ArrayList<>());

    public AbstractPoweredNetworkEntity(EnergyCableType cableType, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        this.dense = ((AbstractCable<?>) blockState.getBlock()).isDense();
        this.energyContainer = new CableEnergyContainer(this.dense, cableType);
    }

    public boolean isDense() {
        return dense;
    }

    public List<EnergyStorage> getEnergyStorages() {
        return this.energyStorages;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addEnergyStorage(EnergyStorage energyStorage) {
        return this.energyStorages.add(energyStorage);
    }

    public boolean removeEnergyStorage(EnergyStorage energyStorage) {
        return this.energyStorages.remove(energyStorage);
    }

    public CableEnergyContainer getEnergyContainer() {
        return this.energyContainer;
    }

    public EnergyStorage getEnergyStorage(Direction dir) {
        return energyContainer.getSideStorage(dir);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void serverTick(ServerLevel level) {
        // TODO: Go through all the AbstractPoweredNetworkEntities in the network and distribute one ticks worth of energy
        //       something like this
        for (var poweredEntity :
                getNetwork().getEntitiesOfType(AbstractPoweredNetworkEntity.class)) {
            // TODO: Do something, to distribute the energy, here..
            Mod.NULL_LOGGER.trace("{}", poweredEntity);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void clientTick(ClientLevel level) {

    }
}
