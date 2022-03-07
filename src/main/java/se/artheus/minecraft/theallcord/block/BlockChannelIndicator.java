package se.artheus.minecraft.theallcord.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import se.artheus.minecraft.theallcord.block.entity.ChannelIndicatorBlockEntity;

public class BlockChannelIndicator extends AbstractBlock<ChannelIndicatorBlockEntity> {

    public static final BooleanProperty ONLINE = BooleanProperty.create("online");

    public BlockChannelIndicator() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(5.0f)
                .requiresCorrectToolForDrops()
                .lightLevel((state) -> state.getValue(ONLINE) ? 10 : 0)
        );

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ONLINE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ONLINE);
    }
}
