package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_ITEM_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.ENTITY_TYPE_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_BASIC;

public class EntityCableBasic extends AbstractCableEntity {

    public EntityCableBasic(BlockPos blockPos, BlockState blockState) {
        super(ENTITY_TYPE_CABLE_BASIC, blockPos, blockState);
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
