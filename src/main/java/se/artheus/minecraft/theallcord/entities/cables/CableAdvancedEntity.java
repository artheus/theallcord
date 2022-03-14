package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.EnergyCableType;
import se.artheus.minecraft.theallcord.entities.BlockEntities;

import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ADVANCED;

public class CableAdvancedEntity extends AbstractCableEntity {

    public CableAdvancedEntity(BlockPos blockPos, BlockState blockState) {
        super(EnergyCableType.ADVANCED, BlockEntities.ENTITY_TYPE_CABLE_ADVANCED, blockPos, blockState);
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
