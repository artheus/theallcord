package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.util.AEColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.CABLE_ADVANCED_ENTITY;

public class CableAdvancedEntity extends AbstractCableEntity {

    public static final String ID = "cable_advanced";

    public static final AEColor[] COLORS = new AEColor[]{
            AEColor.TRANSPARENT,
            AEColor.BLACK,
            AEColor.RED,
            AEColor.YELLOW,
    };

    public CableAdvancedEntity(BlockPos blockPos, BlockState blockState) {
        super(CABLE_ADVANCED_ENTITY, blockPos, blockState);
    }

    @Override
    public AEColor[] getColors() {
        return COLORS;
    }

    @Override
    public String getTagPrefix() {
        return "adv";
    }

    @Override
    public ItemStack asItemStack() {
        return null;
    }
}
