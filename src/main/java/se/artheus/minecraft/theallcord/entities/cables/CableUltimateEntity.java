package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.CableType;

import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CABLE_ULTIMATE_DENSE;
import static se.artheus.minecraft.theallcord.entities.BlockEntities.ENTITY_TYPE_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ULTIMATE;

public class CableUltimateEntity extends AbstractNetworkCableEntity {

    public CableUltimateEntity(BlockPos blockPos, BlockState blockState) {
        super(CableType.ULTIMATE, ENTITY_TYPE_CABLE_ULTIMATE, blockPos, blockState);
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_ULTIMATE.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return isDense() ? new ItemStack(BLOCK_ITEM_CABLE_ULTIMATE_DENSE):new ItemStack(BLOCK_ITEM_CABLE_ULTIMATE);
    }
}
