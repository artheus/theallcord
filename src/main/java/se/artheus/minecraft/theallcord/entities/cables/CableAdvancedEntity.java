package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.entities.BlockEntities;
import se.artheus.minecraft.theallcord.networking.CableType;

import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ADVANCED_DENSE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ADVANCED;

public class CableAdvancedEntity extends AbstractNetworkCableEntity {

    public CableAdvancedEntity(BlockPos blockPos, BlockState blockState) {
        super(CableType.ADVANCED, BlockEntities.ENTITY_TYPE_CABLE_ADVANCED, blockPos, blockState);
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_ADVANCED.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return isDense() ? new ItemStack(BLOCK_CABLE_ADVANCED_DENSE):new ItemStack(BLOCK_ITEM_CABLE_ADVANCED);
    }
}
