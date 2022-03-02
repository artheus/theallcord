package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CableBasicEntityDenseTicker {

    public static void serverTick(Level level, BlockPos pos, BlockState state, CableBasicDenseEntity entity) {
        entity.initialize();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CableBasicDenseEntity entity) {

    }
}
