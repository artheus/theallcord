package se.artheus.minecraft.theallcord.blocks;

import appeng.api.util.AEColor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
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
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.networking.CableType;

import java.util.Map;
import java.util.Set;

public class BlockCable<E extends AbstractNetworkCableEntity> extends AbstractBlock<E> {
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

    private static final Direction[] DIRECTIONS = Direction.values();
    protected final VoxelShape[] shapeByIndex;

    private final boolean dense;
    private final CableType cableType;

    private final Set<AEColor> colors;

    public BlockCable(CableType cableType, boolean dense, Set<AEColor> colors) {
        this(cableType, dense, colors, BlockBehaviour.Properties.of(Material.METAL)
            .noOcclusion()
            .dynamicShape()
            .requiresCorrectToolForDrops()
            .strength(5.0f));
    }

    public BlockCable(CableType cableType, boolean dense, Set<AEColor> colors, BlockBehaviour.Properties properties) {
        super(properties);

        this.colors = colors;
        this.cableType = cableType;
        this.dense = dense;
        this.shapeByIndex = this.makeShapes(cableType.getCableRadius());

        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(EAST, false)
            .setValue(UP, false)
            .setValue(DOWN, false)
        );
    }

    public @NotNull Set<AEColor> colors() {
        return colors;
    }

    public CableType getCableType() {
        return cableType;
    }

    public boolean isDense() {
        return this.dense;
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN));
    }

    private VoxelShape[] makeShapes(float apothem) {
        float f = 0.5f - apothem;
        float g = 0.5f + apothem;
        VoxelShape voxelShape = Block.box(f * 16.0f, f * 16.0f, f * 16.0f, g * 16.0f, g * 16.0f, g * 16.0f);
        VoxelShape[] voxelShapes = new VoxelShape[DIRECTIONS.length];
        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            voxelShapes[i] = Shapes.box(0.5 + Math.min(-apothem, (double) direction.getStepX() * 0.5), 0.5 + Math.min(-apothem, (double) direction.getStepY() * 0.5), 0.5 + Math.min(-apothem, (double) direction.getStepZ() * 0.5), 0.5 + Math.max(apothem, (double) direction.getStepX() * 0.5), 0.5 + Math.max(apothem, (double) direction.getStepY() * 0.5), 0.5 + Math.max(apothem, (double) direction.getStepZ() * 0.5));
        }
        VoxelShape[] i = new VoxelShape[64];
        for (int direction = 0; direction < 64; ++direction) {
            VoxelShape voxelShape2 = voxelShape;
            for (int j = 0; j < DIRECTIONS.length; ++j) {
                if ((direction & 1 << j)==0) continue;
                voxelShape2 = Shapes.or(voxelShape2, voxelShapes[j]);
            }
            i[direction] = voxelShape2;
        }
        return i;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
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