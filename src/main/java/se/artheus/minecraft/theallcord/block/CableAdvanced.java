package se.artheus.minecraft.theallcord.block;

import appeng.api.util.AEColor;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.block.entity.CableAdvancedEntity;
import se.artheus.minecraft.theallcord.block.entity.CableEntityTicker;

public class CableAdvanced extends AbstractCableBlock<CableAdvancedEntity> {

    public static final String ID = "cable_advanced";
    public static final CableAdvanced INSTANCE = new CableAdvanced(0.25F, false);

    public CableAdvanced(float cableRadius, boolean dense) {
        super(cableRadius, dense);
    }

    @Override
    public BlockEntityTicker<CableAdvancedEntity> getServerTicker() {
        return CableEntityTicker::serverTick;
    }

    @Override
    public BlockEntityTicker<CableAdvancedEntity> getClientTicker() {
        return CableEntityTicker::clientTick;
    }

    @Override
    public @NotNull AEColor[] colors() {
        return CableAdvancedEntity.COLORS;
    }
}
