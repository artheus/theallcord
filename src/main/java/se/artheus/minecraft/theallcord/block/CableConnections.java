package se.artheus.minecraft.theallcord.block;

import appeng.api.exceptions.ExistingConnectionException;
import appeng.api.exceptions.FailedConnectionException;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.util.AEColor;
import appeng.helpers.AEMultiBlockEntity;
import appeng.me.helpers.IGridConnectedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static se.artheus.minecraft.theallcord.block.AbstractCableBlock.DIRECTION_PROPERTY_MAP;

public class CableConnections {

    public static boolean isConnectableTo(BlockEntity blockEntity) {
        return blockEntity instanceof AbstractCableEntity ||
                blockEntity instanceof IInWorldGridNodeHost;
    }

    public static BlockState connectToNearbyGrid(LevelAccessor level, BlockPos pos) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(pos);

        return attemptConnectionTo(
                level,
                pos,
                DIRECTION_PROPERTY_MAP.keySet().toArray(new Direction[0]) // all directions
        );
    }

    public static BlockState attemptConnectionTo(LevelAccessor level, BlockPos pos, Direction... directions) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(pos);
        Objects.requireNonNull(directions);
        assert directions.length > 0;

        var state = level.getBlockState(pos).getBlock().defaultBlockState();

        // Attempt to create connection between AE Grid nodes
        for (final Direction dir : directions) {
            var prop = DIRECTION_PROPERTY_MAP.get(dir);
            var otherEntity = level.getBlockEntity(pos.relative(dir));
            var connectable = isConnectableTo(otherEntity);

            if (level.getBlockEntity(pos) instanceof AbstractCableEntity ace) {
                for (var managedNode : ace.getManagedNodes().values()) {
                    var gridNode = managedNode.getNode();

                    if (gridNode == null) continue;

                    var color = gridNode.getGridColor();

                    if (connectable) {
                        var otherGridNode = getGridNodeFrom(otherEntity, color, dir.getOpposite());

                        if (otherGridNode == null) continue;

                        try {
                            // AE Multiblock entities (AE native cables)
                            if (otherEntity instanceof AEMultiBlockEntity aeMultiBlockEntity) {
                                if (aeMultiBlockEntity.getColor() == color) {
                                    GridHelper.createGridConnection(gridNode, otherGridNode);
                                }
                            } else {
                                GridHelper.createGridConnection(gridNode, otherGridNode);
                            }

                            state = state.setValue(prop, true);
                            //Mod.LOGGER.info("created {} grid connection between {} and {}", color, gridNode, otherGridNode);
                        } catch (FailedConnectionException e) {
                            if (e instanceof ExistingConnectionException) {
                                state = state.setValue(prop, true);
                            } else {
                                Mod.LOGGER.info("failed to create grid connection between {} and {}", gridNode, otherGridNode);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        var gridConn = gridNode.getConnections().stream().filter((conn) -> conn.getDirection(gridNode) == dir).findAny();

                        gridConn.ifPresent(IGridConnection::destroy);
                    }
                }
            }
        }

        return state;
    }

    @Nullable
    private static IGridNode getGridNodeFrom(Object nodeHost, AEColor color, Direction dir) {
        if (nodeHost instanceof AbstractCableEntity otherAce) {
            return otherAce.getManagedNodeByColor(color).getNode();
        } else if (nodeHost instanceof IGridConnectedBlockEntity connectedBlockEntity) {
            return connectedBlockEntity.getMainNode().getNode();
        } else if (nodeHost instanceof IInWorldGridNodeHost inWorldGridNodeHost) {
            return inWorldGridNodeHost.getGridNode(dir);
        }

        return null;
    }
}
