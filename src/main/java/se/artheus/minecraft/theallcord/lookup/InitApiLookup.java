package se.artheus.minecraft.theallcord.lookup;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.util.AEColor;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.networking.Network;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public class InitApiLookup {

    public static final BlockApiLookup<IGridNode, DirectionAndColor> SIDED_AE2_GRID_NODE =
        BlockApiLookup.get(new ResourceLocation("theallcord:sided_ae2_grid_node"), IGridNode.class, DirectionAndColor.class);

    public static void init() {
        // Register Lookup API for NetworkNodes
        Network.SIDED.registerFallback((world, pos, state, blockEntity, context) -> Network.NODES.get(pos));
        Network.SIDED.registerFallback((world, pos, state, blockEntity, context) -> Network.NODES.get(pos));

        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (Objects.isNull(blockEntity))
                blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof AbstractNetworkCableEntity ance) {
                return ance.getEnergyConnectionManager();
            }

            return null;
        });

        SIDED_AE2_GRID_NODE.registerFallback((world, pos, state, blockEntity, context) -> {
            if (Objects.isNull(blockEntity))
                blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof IInWorldGridNodeHost nodeHost) {
                return nodeHost.getGridNode(context.direction);
            } else if (blockEntity instanceof AbstractNetworkCableEntity ance) {
                var gridNodeConnectionManager = ance.getGridNodeConnectionManager();
                if (gridNodeConnectionManager==null) return null;

                return gridNodeConnectionManager.getNode(context);
            }

            return null;
        });
    }

    public record DirectionAndColor(Direction direction, AEColor color) {
    }
}
