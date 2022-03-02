package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEntity extends BlockEntity {

    protected BlockEntityType<?> type;

    public AbstractEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        this.type = blockEntityType;
    }

    public abstract ItemStack asItemStack();
}
