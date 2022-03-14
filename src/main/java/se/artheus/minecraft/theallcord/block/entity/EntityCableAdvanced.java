package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.cable.EnergyCableType;

import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.ENTITY_TYPE_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ADVANCED;

public class EntityCableAdvanced extends AbstractCableEntity {

    public EntityCableAdvanced(BlockPos blockPos, BlockState blockState) {
        super(EnergyCableType.ADVANCED, ENTITY_TYPE_CABLE_ADVANCED, blockPos, blockState);
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
