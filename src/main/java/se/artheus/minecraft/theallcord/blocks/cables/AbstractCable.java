package se.artheus.minecraft.theallcord.blocks.cables;

import appeng.api.util.AEColor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.blocks.AbstractBlock;
import se.artheus.minecraft.theallcord.entities.cables.AbstractCableEntity;
import se.artheus.minecraft.theallcord.networking.CableConnections;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
public abstract class AbstractCable<E extends AbstractCableEntity> extends AbstractBlock<E> {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.WEST, WEST);
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
    }));

    protected final VoxelShape[] shapeByIndex;

    public static final HashMap<Direction, BooleanProperty> DIRECTION_PROPERTY_MAP = new HashMap<>() {{
        put(Direction.DOWN, DOWN);
        put(Direction.UP, UP);
        put(Direction.NORTH, NORTH);
        put(Direction.SOUTH, SOUTH);
        put(Direction.WEST, WEST);
        put(Direction.EAST, EAST);
    }};

    private final boolean dense;

    public AbstractCable(float cableRadius, boolean dense) {
        super(BlockBehaviour.Properties.of(Material.METAL).noOcclusion().dynamicShape().requiresCorrectToolForDrops().strength(5.0f));

        this.dense = dense;
        this.shapeByIndex = this.makeShapes(cableRadius);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
        );
    }

    @NotNull
    public abstract Set<AEColor> colors();

    public boolean isDense() {
        return this.dense;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof AbstractCableEntity ace) {
            ace.flagForUpdate();
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        return state.setValue(DIRECTION_PROPERTY_MAP.get(direction), CableConnections.isConnectable((Level) level, neighborPos, direction.getOpposite(), level.getBlockEntity(neighborPos)));
    }

    private VoxelShape[] makeShapes(float apothem) {
        float f = 0.5f - apothem;
        float g = 0.5f + apothem;
        VoxelShape voxelShape = Block.box(f * 16.0f, f * 16.0f, f * 16.0f, g * 16.0f, g * 16.0f, g * 16.0f);
        VoxelShape[] voxelShapes = new VoxelShape[DIRECTIONS.length];
        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            voxelShapes[i] = Shapes.box(0.5 + Math.min((double) (-apothem), (double) direction.getStepX() * 0.5), 0.5 + Math.min((double) (-apothem), (double) direction.getStepY() * 0.5), 0.5 + Math.min((double) (-apothem), (double) direction.getStepZ() * 0.5), 0.5 + Math.max((double) apothem, (double) direction.getStepX() * 0.5), 0.5 + Math.max((double) apothem, (double) direction.getStepY() * 0.5), 0.5 + Math.max((double) apothem, (double) direction.getStepZ() * 0.5));
        }
        VoxelShape[] i = new VoxelShape[64];
        for (int direction = 0; direction < 64; ++direction) {
            VoxelShape voxelShape2 = voxelShape;
            for (int j = 0; j < DIRECTIONS.length; ++j) {
                if ((direction & 1 << j) == 0) continue;
                voxelShape2 = Shapes.or(voxelShape2, voxelShapes[j]);
            }
            i[direction] = voxelShape2;
        }
        return i;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapeByIndex[this.getAABBIndex(state)];
    }

    protected int getAABBIndex(BlockState state) {
        int i = 0;
        for (int j = 0; j < DIRECTIONS.length; ++j) {
            if (!state.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) continue;
            i |= 1 << j;
        }
        return i;
    }
}