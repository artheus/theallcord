package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.CableType;

import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_ITEM_CABLE_ELITE_DENSE;
import static se.artheus.minecraft.theallcord.entities.BlockEntities.ENTITY_TYPE_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ELITE;

public class CableEliteEntity extends AbstractNetworkCableEntity {

    public CableEliteEntity(BlockPos blockPos, BlockState blockState) {
        super(CableType.ELITE, ENTITY_TYPE_CABLE_ELITE, blockPos, blockState);
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_ELITE.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return isDense() ? new ItemStack(BLOCK_ITEM_CABLE_ELITE_DENSE):new ItemStack(BLOCK_ITEM_CABLE_ELITE);
    }
}
