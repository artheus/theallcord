package se.artheus.minecraft.theallcord.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.block.entity.AbstractEntity;

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

    public BlockEntityTicker<E> getClientTicker()  {
        return null;
    };

    public BlockEntityTicker<E> getServerTicker() {
        return null;
    };

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? (BlockEntityTicker<T>) this.getClientTicker() : (BlockEntityTicker<T>) this.getServerTicker();
    }

    @Nullable
    @Override
    public E newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return this.getBlockEntityType().create(pos, state);
    }
}
