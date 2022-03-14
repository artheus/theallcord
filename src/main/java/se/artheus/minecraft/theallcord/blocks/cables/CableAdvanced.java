package se.artheus.minecraft.theallcord.blocks.cables;

import appeng.api.util.AEColor;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.entities.cables.AbstractCableEntity;

import java.util.EnumSet;
import java.util.Set;

public class CableAdvanced<T extends AbstractCableEntity> extends AbstractCable<T> {

    public static final Set<AEColor> COLORS = EnumSet.of(
            AEColor.TRANSPARENT,
            AEColor.BLACK,
            AEColor.RED,
            AEColor.YELLOW
    );

    public CableAdvanced(boolean dense) {
        super(0.25F, dense);
    }

    @Override
    public @NotNull Set<AEColor> colors() {
        return COLORS;
    }
}
