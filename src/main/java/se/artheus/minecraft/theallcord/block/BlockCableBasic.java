package se.artheus.minecraft.theallcord.block;

import appeng.api.util.AEColor;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;

import java.util.EnumSet;
import java.util.Set;

public class BlockCableBasic<E extends AbstractCableEntity> extends AbstractBlockCable<E> {

    public static final Set<AEColor> COLORS = EnumSet.of(
            AEColor.TRANSPARENT
    );

    public BlockCableBasic(boolean dense) {
        super(0.125F, dense);
    }

    @Override
    public Set<AEColor> colors() {
        return COLORS;
    }
}
