package se.artheus.minecraft.theallcord.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.entities.AbstractEntity;

import javax.annotation.Nullable;

public abstract class AbstractBlock<E extends AbstractEntity> extends Block implements EntityBlock {

    private BlockEntityType<E> blockEntityType;

    public AbstractBlock(Properties properties) {
        super(properties);
    }

    public void setBlockEntityType(BlockEntityType<E> type) {
        this.blockEntityType = type;
    }

    public BlockEntityType<E> getBlockEntityType() {
        return this.blockEntityType;
    }

    @Nullable
    @Override
    public E newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return this.getBlockEntityType().create(pos, state);
    }
}
