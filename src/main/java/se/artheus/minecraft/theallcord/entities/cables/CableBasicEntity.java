package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.EnergyCableType;

import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.entities.BlockEntities.ENTITY_TYPE_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_BASIC;

public class CableBasicEntity extends AbstractCableEntity {

    public CableBasicEntity(BlockPos blockPos, BlockState blockState) {
        super(EnergyCableType.BASIC, ENTITY_TYPE_CABLE_BASIC, blockPos, blockState);
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_BASIC.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(BLOCK_ITEM_CABLE_BASIC);
    }
}
