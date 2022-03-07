package se.artheus.minecraft.theallcord.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TickerCableEntity {

    public static <T extends AbstractCableEntity> void serverTick(Level level, BlockPos pos, BlockState state, T entity) {
        if (entity.shouldUpdate()) entity.updateCableConnections();
    }

    public static <T extends AbstractCableEntity> void clientTick(Level level, BlockPos pos, BlockState state, T entity) {

    }
}
