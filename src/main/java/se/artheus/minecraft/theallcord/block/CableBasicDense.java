package se.artheus.minecraft.theallcord.block;

import appeng.api.util.AEColor;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import se.artheus.minecraft.theallcord.block.entity.CableBasicDenseEntity;
import se.artheus.minecraft.theallcord.block.entity.CableBasicEntity;
import se.artheus.minecraft.theallcord.block.entity.CableBasicEntityDenseTicker;
import se.artheus.minecraft.theallcord.block.entity.CableBasicEntityTicker;

public class CableBasicDense extends AbstractCableBlock<CableBasicDenseEntity> {

    public static final String ID = "cable_basic_dense";
    public static final CableBasicDense INSTANCE = new CableBasicDense(true);

    public CableBasicDense(boolean dense) {
        super(0.125F, dense);
    }

    @Override
    public BlockEntityTicker<CableBasicDenseEntity> getServerTicker() {
        return CableBasicEntityDenseTicker::serverTick;
    }

    @Override
    public BlockEntityTicker<CableBasicDenseEntity> getClientTicker() {
        return CableBasicEntityDenseTicker::clientTick;
    }

    @Override
    public AEColor[] colors() {
        return CableBasicEntity.COLORS;
    }
}
