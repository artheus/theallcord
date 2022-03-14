package se.artheus.minecraft.theallcord.blocks.indicators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import se.artheus.minecraft.theallcord.blocks.AbstractBlock;
import se.artheus.minecraft.theallcord.entities.indicators.AEChannelIndicatorEntity;

public class AEChannelIndicator extends AbstractBlock<AEChannelIndicatorEntity> {

    public static final BooleanProperty ONLINE = BooleanProperty.create("online");

    public AEChannelIndicator() {
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
