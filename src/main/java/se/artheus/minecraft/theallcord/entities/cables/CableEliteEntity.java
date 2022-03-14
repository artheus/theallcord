package se.artheus.minecraft.theallcord.entities.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.networking.EnergyCableType;

import static se.artheus.minecraft.theallcord.entities.BlockEntities.ENTITY_TYPE_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ELITE;

public class CableEliteEntity extends AbstractCableEntity {

    public CableEliteEntity(BlockPos blockPos, BlockState blockState) {
        super(EnergyCableType.ELITE, ENTITY_TYPE_CABLE_ELITE, blockPos, blockState);
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
