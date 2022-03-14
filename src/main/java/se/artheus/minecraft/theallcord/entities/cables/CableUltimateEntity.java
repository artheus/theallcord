package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.EnergyCableType;

import static se.artheus.minecraft.theallcord.entities.BlockEntities.ENTITY_TYPE_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ULTIMATE;

public class CableUltimateEntity extends AbstractCableEntity {

    public CableUltimateEntity(BlockPos blockPos, BlockState blockState) {
        super(EnergyCableType.ULTIMATE, ENTITY_TYPE_CABLE_ULTIMATE, blockPos, blockState);
    }

    @Override
    public String getTagPrefix() {
        return ID_ENTITY_CABLE_ULTIMATE.getPath();
    }

    @Override
    public ItemStack asItemStack() {
        return null;
    }
}
