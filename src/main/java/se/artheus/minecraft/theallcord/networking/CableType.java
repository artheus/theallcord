package se.artheus.minecraft.theallcord.networking;

import appeng.api.util.AEColor;

import java.util.Set;

import static se.artheus.minecraft.theallcord.blocks.Blocks.CABLE_ADVANCED_COLORS;
import static se.artheus.minecraft.theallcord.blocks.Blocks.CABLE_BASIC_COLORS;
import static se.artheus.minecraft.theallcord.blocks.Blocks.CABLE_ELITE_COLORS;
import static se.artheus.minecraft.theallcord.blocks.Blocks.CABLE_ULTIMATE_COLORS;

public enum CableType {
    BASIC(CABLE_BASIC_COLORS, 0.125F, 256, 1, 1000),
    ADVANCED(CABLE_ADVANCED_COLORS, 0.25F, 2048, 2, 8000),
    ELITE(CABLE_ELITE_COLORS, 0.625F, 16384, 4, 16000),
    ULTIMATE(CABLE_ULTIMATE_COLORS, 0.75F, 131072, 8, 32000);

    private final Set<AEColor> colors;
    private final float cableRadius;
    private final long energyTransferRate;
    private final long itemTransferRate;
    private final long fluidTransferRate;

    CableType(Set<AEColor> colors, float cableRadius, long energyTransferRate, long itemTransferRate, long fluidTransferRate) {
        this.colors = colors;
        this.cableRadius = cableRadius;
        this.energyTransferRate = energyTransferRate;
        this.itemTransferRate = itemTransferRate;
        this.fluidTransferRate = fluidTransferRate;
    }

    public Set<AEColor> getColors() {
        return colors;
    }

    public long getEnergyTransferRate(boolean isDense) {
        return isDense ? this.energyTransferRate * 4:this.energyTransferRate;
    }

    public long getItemTransferRate(boolean isDense) {
        return isDense ? this.itemTransferRate * 4:this.itemTransferRate;
    }

    public long getFluidTransferRate(boolean isDense) {
        return isDense ? this.fluidTransferRate * 4:this.fluidTransferRate;
    }

    public float getCableRadius() {
        return cableRadius;
    }
}
