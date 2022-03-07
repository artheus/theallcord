package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.ENTITY_TYPE_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ELITE;

public class EntityCableElite extends AbstractCableEntity {

    public EntityCableElite(BlockPos blockPos, BlockState blockState) {
        super(ENTITY_TYPE_CABLE_ELITE, blockPos, blockState);
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_ELITE.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return null;
    }
}
