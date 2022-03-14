package se.artheus.minecraft.theallcord.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import se.artheus.minecraft.theallcord.tick.TickHandler;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class AbstractEntity extends BlockEntity {

    public AbstractEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public abstract boolean isOnline();

    public abstract ItemStack asItemStack();

    public abstract void initialize();

    @Override
    @OverridingMethodsMustInvokeSuper
    public void clearRemoved() {
        super.clearRemoved();

        TickHandler.instance().addInit(this);
    }
}
