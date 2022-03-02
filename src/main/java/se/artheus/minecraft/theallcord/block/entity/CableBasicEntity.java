package se.artheus.minecraft.theallcord.block.entity;

import appeng.api.util.AEColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static se.artheus.minecraft.theallcord.block.Blocks.CABLE_BASIC_BLOCK_ITEM;
import static se.artheus.minecraft.theallcord.block.Blocks.CHANNEL_INDICATOR_BLOCK_ITEM;
import static se.artheus.minecraft.theallcord.block.entity.BlockEntities.CABLE_BASIC_ENTITY;

public class CableBasicEntity extends AbstractCableEntity {

    final static String ID = "cable_basic_entity";

    public static final AEColor[] COLORS = new AEColor[]{
            AEColor.TRANSPARENT
    };

    public CableBasicEntity(BlockPos blockPos, BlockState blockState) {
        super(CABLE_BASIC_ENTITY, blockPos, blockState);
    }

    @Override
    public AEColor[] getColors() {
        return COLORS;
    }

    @Override
    public String getTagPrefix() {
        return ID;
    }

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(CABLE_BASIC_BLOCK_ITEM);
    }
}
