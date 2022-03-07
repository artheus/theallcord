package se.artheus.minecraft.theallcord.cable;

import appeng.api.exceptions.ExistingConnectionException;
import appeng.api.exceptions.FailedConnectionException;
import appeng.api.implementations.blockentities.IColorableBlockEntity;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.util.AEColor;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.me.GridConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;

import java.util.Objects;

import static se.artheus.minecraft.theallcord.block.AbstractBlockCable.DIRECTION_PROPERTY_MAP;

public class CableConnections {

    public static boolean isConnectable(BlockEntity blockEntity) {
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

        var state = level.getBlockState(pos);

        if (level.getBlockEntity(pos) instanceof AbstractCableEntity ace) {
            if (!ace.isInitialized()) {
                ace.shouldUpdate();
                return state;
            }

            // Attempt to create connection between AE Grid nodes
            for (final Direction dir : directions) {
                var relativeEntity = level.getBlockEntity(pos.relative(dir));

                state = state.setValue(DIRECTION_PROPERTY_MAP.get(dir), connectGridNodes(level, dir, ace, relativeEntity));
            }
        }

        return state;
    }

    private static boolean connectGridNodes(LevelAccessor level, Direction dir, AbstractCableEntity ace, BlockEntity otherEntity) {
        if (!isConnectable(otherEntity)) return false;

        for (var managedNode : ace.getManagedNodes().values()) {
            var gridNode = managedNode.getNode();

            if (gridNode == null) {
                ace.flagForUpdate();
                continue;
            }

            var color = gridNode.getGridColor();
            var otherGridNode = getGridNodeFrom(otherEntity, color, dir.getOpposite());

            if (otherGridNode == null) {
                ace.flagForUpdate();
                continue;
            }

            try {
                GridConnection.create(gridNode, otherGridNode, dir);

                Mod.LOGGER.debug("created {} grid connection between {} and {}", color, gridNode, otherGridNode);
            } catch (FailedConnectionException e) {
                if (e instanceof ExistingConnectionException) {
                    Mod.LOGGER.debug("connection between {} and {} already exists", gridNode, otherGridNode);
                } else {
                    Mod.LOGGER.error("failed to create connection for {} in direction {}", ace, dir);
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    @Nullable
    private static IGridNode getGridNodeFrom(Object nodeHost, AEColor color, Direction dir) {
        if (nodeHost instanceof AbstractCableEntity otherAce && otherAce.getManagedNodeByColor(color) != null) {
            return otherAce.getManagedNodeByColor(color).getNode();
        } else if (nodeHost instanceof IInWorldGridNodeHost inWorldGridNodeHost) {
            var otherGridNode = inWorldGridNodeHost.getGridNode(dir);

            if (inWorldGridNodeHost instanceof IColorableBlockEntity colorableBlockEntity) {
                if (!color.equals(colorableBlockEntity.getColor()))
                    return null;
            }

            // Allow only AEColor.TRANSPARENT for direct connection to ME Controller
            if (inWorldGridNodeHost instanceof ControllerBlockEntity && !color.equals(AEColor.TRANSPARENT)) return null;

            // Connect only if the grid nodes have the same color, or otherGridNode is AEColor.TRANSPARENT
            if (otherGridNode == null || (!otherGridNode.getGridColor().equals(color) && !otherGridNode.getGridColor().equals(AEColor.TRANSPARENT)))
                return null;

            return otherGridNode;
        }

        return null;
    }
}
