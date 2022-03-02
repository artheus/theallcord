package se.artheus.minecraft.theallcord.block;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.block.entity.ChannelIndicatorBlockEntity;
import se.artheus.minecraft.theallcord.block.entity.ChannelIndicatorBlockEntityTicker;

public class ChannelIndicatorBlock<E extends ChannelIndicatorBlockEntity> extends Block implements IInWorldGridNodeHost, EntityBlock {

    public static final BooleanProperty HAS_CHANNEL = BooleanProperty.create("has_channel");

    public static final String ID = "channel_indicator";
    public static final ChannelIndicatorBlock<ChannelIndicatorBlockEntity> INSTANCE = new ChannelIndicatorBlock<>();

    private BlockEntityType<E> blockEntityType;

    private final BlockEntityTicker<E> blockEntityServerTicker;
    private final BlockEntityTicker<E> blockEntityClientTicker;

    public ChannelIndicatorBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(5.0f)
                .requiresCorrectToolForDrops()
                .lightLevel((state) -> state.getValue(HAS_CHANNEL) ? 10 : 0)
        );

        this.blockEntityServerTicker = ChannelIndicatorBlockEntityTicker::serverTick;
        this.blockEntityClientTicker = ChannelIndicatorBlockEntityTicker::clientTick;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HAS_CHANNEL, false)
        );
    }

    public void setBlockEntityType(BlockEntityType<E> beType) {
        this.blockEntityType = beType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return this.blockEntityType.create(pos, state);
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        return null;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_CHANNEL);
    }

    public BlockEntityTicker<E> getServerTicker() {
        return this.blockEntityServerTicker;
    }

    public BlockEntityTicker<E> getClientTicker() {
        return this.blockEntityClientTicker;
    }


    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? (BlockEntityTicker<T>) this.getClientTicker() : (BlockEntityTicker<T>) this.getServerTicker();
    }
}
