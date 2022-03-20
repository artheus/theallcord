package se.artheus.minecraft.theallcord.networking.ae2;

import appeng.api.exceptions.ExistingConnectionException;
import appeng.api.exceptions.FailedConnectionException;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AEColor;
import appeng.me.GridConnection;
import appeng.me.ManagedGridNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.entities.AbstractGridNodeListener;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.lookup.InitApiLookup;
import se.artheus.minecraft.theallcord.networking.AbstractConnectionManager;
import se.artheus.minecraft.theallcord.tick.ITickingEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@ParametersAreNonnullByDefault
public final class GridNodeConnectionManager extends AbstractConnectionManager<IGridNode, InitApiLookup.DirectionAndColor, OfferedGridNode> implements ITickingEntity {

    private final static IGridNodeListener<AbstractNetworkCableEntity> GRID_NODE_LISTENER = new AbstractGridNodeListener<>() {
    };

    private final Map<AEColor, IManagedGridNode> managedNodes = Collections.synchronizedMap(new HashMap<>());

    private boolean removed = false;

    public GridNodeConnectionManager(ServerLevel level, AbstractNetworkCableEntity entity) {
        super(level, entity);
    }

    @Override
    public void initialize(ServerLevel level) {
        for (var dir : Direction.values()) {
            for (var color : entity.getCableType().getColors()) {

                this.addTarget(
                    new OfferedGridNode(
                        (ServerLevel) entity.getLevel(),
                        entity.getBlockPos().relative(dir),
                        new InitApiLookup.DirectionAndColor(dir.getOpposite(), color)
                    )
                );
            }
        }

        ITickingEntity.super.initialize(level);
    }

    public @Nullable
    IManagedGridNode getManagedNodeFor(InitApiLookup.DirectionAndColor context) {
        if (!entity.getCableType().getColors().contains(context.color())) return null;

        var managedNode = managedNodes.get(context.color());

        if (Objects.isNull(managedNode)) {
            managedNode = new ManagedGridNode(entity, GRID_NODE_LISTENER)
                .setInWorldNode(false)
                .setGridColor(context.color())
                .setIdlePowerUsage(0.0)
                .setTagName("%s_%s".formatted(entity.getTagPrefix(), context.color().getEnglishName().toLowerCase()))
                .setExposedOnSides(EnumSet.noneOf(Direction.class));

            if (entity.isDense()) {
                managedNode.setFlags(GridFlags.DENSE_CAPACITY);
            }

            try {
                managedNode.create(level, entity.getBlockPos());
            } catch (Throwable t) {
                t.printStackTrace();
                throw new IllegalStateException(t);
            }

            managedNodes.put(context.color(), managedNode);
        }

        return managedNode;
    }

    public void destroy() {
        for (var managedNode : getManagedNodes().values()) {
            managedNode.destroy();
        }

        removed = true;
    }

    public Map<AEColor, IManagedGridNode> getManagedNodes() {
        return Collections.unmodifiableMap(this.managedNodes);
    }

    public @Nullable
    IGridNode getNode(InitApiLookup.DirectionAndColor context) {
        var managedNode = getManagedNodeFor(context);
        if (managedNode==null) return null;

        return managedNode.getNode();
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public BlockPos getBlockPos() {
        return getEntity().getBlockPos();
    }

    @Override
    public Set<Direction> getConnectedSides() {
        return managedNodes.values().stream()
            .map(IManagedGridNode::getNode)
            .filter(Objects::nonNull)
            .map(IGridNode::getConnectedSides)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void tick(ServerLevel level) {
        for (var target : getTargets()) {
            var node = target.find();
            if (Objects.isNull(node)) continue;

            if (node.getGridColor()!=AEColor.TRANSPARENT
                && node.getGridColor()!=target.context().color()) continue;

            // create managed nodes only when they are needed
            var managedNode = getManagedNodeFor(target.context());
            if (Objects.isNull(managedNode)) continue;

            var myNode = managedNode.getNode();
            if (Objects.isNull(myNode)) continue;

            try {
                GridConnection.create(node, myNode, target.context().direction());
            } catch (FailedConnectionException e) {
                if (!(e instanceof ExistingConnectionException)) {
                    Mod.LOGGER.warn("Failed to connect grid nodes, cause: {}", e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Long createSnapshot() {
        return null;
    }

    @Override
    protected void readSnapshot(Long snapshot) {

    }
}

