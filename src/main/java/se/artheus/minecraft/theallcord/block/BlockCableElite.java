package se.artheus.minecraft.theallcord.block;

import appeng.api.util.AEColor;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;
import se.artheus.minecraft.theallcord.block.entity.TickerCableEntity;

import java.util.EnumSet;
import java.util.Set;

public class BlockCableElite<T extends AbstractCableEntity> extends AbstractBlockCable<T> {

    public static final Set<AEColor> COLORS = EnumSet.of(
            AEColor.TRANSPARENT,
            AEColor.BLACK,
            AEColor.RED,
            AEColor.YELLOW,
            AEColor.GREEN,
            AEColor.BLUE,
            AEColor.ORANGE
    );

    public BlockCableElite(boolean dense) {
        super(0.25F, dense);
    }

    @Override
    public BlockEntityTicker<T> getServerTicker() {
        return TickerCableEntity::serverTick;
    }

    @Override
    public BlockEntityTicker<T> getClientTicker() {
        return TickerCableEntity::clientTick;
    }

    @Override
    public @NotNull Set<AEColor> colors() {
        return COLORS;
    }
}
