package se.artheus.minecraft.theallcord.networking;

import appeng.api.exceptions.ExistingConnectionException;
import appeng.api.exceptions.FailedConnectionException;
import appeng.api.implementations.blockentities.IColorableBlockEntity;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.util.AEColor;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.me.GridConnection;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.entities.cables.AbstractCableEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

import static se.artheus.minecraft.theallcord.blocks.cables.AbstractCable.DIRECTION_PROPERTY_MAP;
import static se.artheus.minecraft.theallcord.blocks.cables.AbstractCable.PROPERTY_BY_DIRECTION;

@SuppressWarnings("UnstableApiUsage")
public class CableConnections {

    public static BlockState connectToNearbyEntities(Level level, BlockPos pos) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(pos);

        return attemptConnectionTo(
                level,
                pos,
                DIRECTION_PROPERTY_MAP.keySet().toArray(new Direction[0]) // all directions
        );
    }

    public static BlockState attemptConnectionTo(Level level, BlockPos pos, Direction... directions) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(pos);
        Objects.requireNonNull(directions);
        assert directions.length > 0;

        var state = level.getBlockState(pos).getBlock().defaultBlockState();

        if (level.getBlockEntity(pos) instanceof AbstractCableEntity ace) {
            if (!ace.isInitialized()) {
                ace.shouldUpdate();
                return state;
            }

            // Attempt to create connection between AE Grid nodes
            for (final Direction dir : directions) {
                var relativeEntity = level.getBlockEntity(pos.relative(dir));

                if (!isConnectable(level, pos.relative(dir), dir.getOpposite(), ace)) {
                    state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), false);
                    continue;
                }

                if (relativeEntity instanceof AbstractCableEntity neighborAce) {
                    if (!ace.getNetwork().has(neighborAce)) {
                        ace.mergeNetworksWith(neighborAce);
                    }

                    state = state.setValue(DIRECTION_PROPERTY_MAP.get(dir), true);
                } else {
                    var energyStorage = getEnergyStorage(level, pos.relative(dir), dir.getOpposite());
                    if (energyStorage != null) {
                        ace.addEnergyStorage(energyStorage);
                        state = state.setValue(DIRECTION_PROPERTY_MAP.get(dir), true);
                    }

                    var fluidStorage = getFluidStorage(level, pos.relative(dir), dir.getOpposite());
                    if (fluidStorage != null) {
                        ace.addFluidStorage(fluidStorage);
                        state = state.setValue(DIRECTION_PROPERTY_MAP.get(dir), true);
                    }

                    var itemStorage = getItemStorage(level, pos.relative(dir), dir.getOpposite());
                    if (itemStorage != null) {
                        ace.addItemStorage(itemStorage);
                        state = state.setValue(DIRECTION_PROPERTY_MAP.get(dir), true);
                    }
                }

                if (relativeEntity instanceof IInWorldGridNodeHost neighborNodeHost) {
                    state = state.setValue(
                            DIRECTION_PROPERTY_MAP.get(dir),
                            connectGridNodes(dir, ace, neighborNodeHost)
                    );
                }
            }
        }

        return state;
    }

    private static boolean connectGridNodes(Direction dir, AbstractCableEntity myEntity, IInWorldGridNodeHost neighborNodeHost) {
        if (dir == null || myEntity == null || neighborNodeHost == null) return false;

        var connectionsMade = false;

        for (var managedNode : myEntity.getManagedNodes().values()) {
            var gridNode = managedNode.getNode();

            if (gridNode == null || !hasAvailableAENode(dir.getOpposite(), myEntity)) {
                myEntity.flagForUpdate();
                continue;
            }

            var color = gridNode.getGridColor();
            var otherGridNode = getGridNodeFrom(neighborNodeHost, color, dir.getOpposite());

            if (otherGridNode == null) continue;

            try {
                GridConnection.create(gridNode, otherGridNode, dir);
                connectionsMade = true;
                Mod.LOGGER.debug("created {} grid connection between {} and {}", color, gridNode, otherGridNode);
            } catch (FailedConnectionException e) {
                if (e instanceof ExistingConnectionException) {
                    Mod.LOGGER.debug("connection between {} and {} already exists", gridNode, otherGridNode);
                } else {
                    Mod.LOGGER.error("failed to create connection for {} in direction {}", myEntity, dir);
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return connectionsMade;
    }

    @Nullable
    private static IGridNode getGridNodeFrom(IInWorldGridNodeHost nodeHost, AEColor color, Direction dir) {
        if (nodeHost instanceof AbstractCableEntity neighborAce) {
            if (neighborAce.getManagedNodeByColor(color) != null)
                return neighborAce.getManagedNodeByColor(color).getNode();
        } else {
            var otherGridNode = nodeHost.getGridNode(dir);

            if (nodeHost instanceof IColorableBlockEntity colorableBlockEntity &&
                    !color.equals(colorableBlockEntity.getColor())) return null;

            // TODO: This should be configurable by gui
            // Allow only AEColor.TRANSPARENT for direct connection to ME Controller
            if (nodeHost instanceof ControllerBlockEntity && !color.equals(AEColor.TRANSPARENT)) return null;

            // Connect only if the grid nodes have the same color, or otherGridNode is AEColor.TRANSPARENT
            if (otherGridNode == null || (!otherGridNode.getGridColor().equals(color) && !otherGridNode.getGridColor().equals(AEColor.TRANSPARENT)))
                return null;

            return otherGridNode;
        }

        return null;
    }

    public static boolean isConnectable(Level level, BlockPos neighborPos, Direction dir, BlockEntity neighbor) {
        if (level == null || neighborPos == null || dir == null || neighbor == null) return false;

        return isAllcordCable(neighbor) ||
                neighbor instanceof IInWorldGridNodeHost ||
                getEnergyStorage(level, neighborPos, dir) != null ||
                getFluidStorage(level, neighborPos, dir) != null ||
                getItemStorage(level, neighborPos, dir) != null;
    }

    @Nullable
    public static EnergyStorage getEnergyStorage(Level level, BlockPos pos, Direction dir) {
        return EnergyStorage.SIDED.find(level, pos, dir);
    }

    @Nullable
    public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos, Direction dir) {
        return FluidStorage.SIDED.find(level, pos, dir);
    }

    @Nullable
    public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos, Direction dir) {
        return ItemStorage.SIDED.find(level, pos, dir);
    }

    public static boolean hasAvailableAENode(Direction dir, BlockEntity neighbor) {
        return neighbor instanceof IInWorldGridNodeHost &&
                ((IInWorldGridNodeHost) neighbor).getGridNode(dir) != null;
    }

    public static boolean isAllcordCable(BlockEntity entity) {
        return entity instanceof AbstractCableEntity;
    }
}
