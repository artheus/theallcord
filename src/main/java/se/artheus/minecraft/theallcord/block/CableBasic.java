package se.artheus.minecraft.theallcord.block;

import appeng.api.util.AEColor;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import se.artheus.minecraft.theallcord.block.entity.CableBasicEntity;
import se.artheus.minecraft.theallcord.block.entity.CableEntityTicker;

public class CableBasic extends AbstractCableBlock<CableBasicEntity> {

    public static final String ID = "cable_basic";
    public static final CableBasic INSTANCE = new CableBasic(false);

    public CableBasic(boolean dense) {
        super(0.125F, dense);
    }

    @Override
    public BlockEntityTicker<CableBasicEntity> getServerTicker() {
        return CableEntityTicker::serverTick;
    }

    @Override
    public BlockEntityTicker<CableBasicEntity> getClientTicker() {
        return CableEntityTicker::clientTick;
    }

    @Override
    public AEColor[] colors() {
        return CableBasicEntity.COLORS;
    }
}
