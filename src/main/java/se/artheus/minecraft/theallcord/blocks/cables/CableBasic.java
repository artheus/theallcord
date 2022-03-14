package se.artheus.minecraft.theallcord.blocks.cables;

import appeng.api.util.AEColor;
import se.artheus.minecraft.theallcord.entities.cables.AbstractCableEntity;

import java.util.EnumSet;
import java.util.Set;

public class CableBasic<E extends AbstractCableEntity> extends AbstractCable<E> {

    public static final Set<AEColor> COLORS = EnumSet.of(
            AEColor.TRANSPARENT
    );

    public CableBasic(boolean dense) {
        super(0.125F, dense);
    }

    @Override
    public Set<AEColor> colors() {
        return COLORS;
    }
}
