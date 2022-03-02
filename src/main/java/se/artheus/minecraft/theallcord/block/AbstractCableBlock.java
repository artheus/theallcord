package se.artheus.minecraft.theallcord.block;

import appeng.api.util.AEColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class AbstractCableBlock<E extends AbstractCableEntity> extends PipeBlock implements EntityBlock {

    static final HashMap<Direction, BooleanProperty> DIRECTION_PROPERTY_MAP = new HashMap<>() {{
        put(Direction.DOWN, DOWN);
        put(Direction.UP, UP);
        put(Direction.NORTH, NORTH);
        put(Direction.SOUTH, SOUTH);
        put(Direction.WEST, WEST);
        put(Direction.EAST, EAST);
    }};

    private BlockEntityType<E> blockEntityType;
    private final boolean dense;

    public AbstractCableBlock(float cableRadius, boolean dense) {
        super(cableRadius, BlockBehaviour.Properties.of(Material.METAL).noOcclusion().dynamicShape().requiresCorrectToolForDrops().strength(5.0f));

        this.dense = dense;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
        );
    }

    @Nullable
    public abstract BlockEntityTicker<E> getServerTicker();

    @Nullable
    public abstract BlockEntityTicker<E> getClientTicker();

    @NotNull
    public abstract AEColor[] colors();

    public void setBlockEntityType(BlockEntityType<E> entityType) {
        this.blockEntityType = entityType;
    }

    public boolean isDense() {
        return this.dense;
    }

    public BlockEntityType<E> getBlockEntityType() {
        return this.blockEntityType;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? (BlockEntityTicker<T>) this.getClientTicker() : (BlockEntityTicker<T>) this.getServerTicker();
    }

    @Nullable
    @Override
    public E newBlockEntity(BlockPos pos, BlockState state) {
        return this.getBlockEntityType().create(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        var entity = level.getBlockEntity(currentPos);

        if (entity != null) {
            entity.setChanged();
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
}