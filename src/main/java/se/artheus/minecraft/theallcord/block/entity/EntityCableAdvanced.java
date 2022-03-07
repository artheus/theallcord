package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.block.AbstractBlockCable;
import se.artheus.minecraft.theallcord.cable.CableEnergyStorage;

import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.ENTITY_TYPE_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ADVANCED;

public class EntityCableAdvanced extends AbstractCableEntity {

    private static final long ENERGY_RATE = 2048;

    public EntityCableAdvanced(BlockPos blockPos, BlockState blockState) {
        super(ENTITY_TYPE_CABLE_ADVANCED, blockPos, blockState);

        if (blockState.getBlock() instanceof AbstractBlockCable abc) {
            this.setEnergyStorage(new CableEnergyStorage(
                    abc.isDense(),
                    ENERGY_RATE
            ));
        }
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_ADVANCED.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return null;
    }
}