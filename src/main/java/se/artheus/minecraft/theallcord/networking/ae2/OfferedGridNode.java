package se.artheus.minecraft.theallcord.networking.ae2;

import appeng.api.networking.IGridNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.lookup.InitApiLookup;
import se.artheus.minecraft.theallcord.networking.AbstractOfferedType;

import static se.artheus.minecraft.theallcord.lookup.InitApiLookup.SIDED_AE2_GRID_NODE;

public class OfferedGridNode extends AbstractOfferedType<IGridNode, InitApiLookup.DirectionAndColor> {
    public OfferedGridNode(ServerLevel level, BlockPos blockPos, InitApiLookup.DirectionAndColor direction) {
        super(level, blockPos, direction, SIDED_AE2_GRID_NODE);
    }
}