package se.artheus.minecraft.theallcord.networking;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractConnectionManager<A, C, O extends AbstractOfferedType<A, C>> extends SnapshotParticipant<Long> {
    protected final Set<Direction> connectedSides = Collections.synchronizedSet(EnumSet.noneOf(Direction.class));
    protected final ServerLevel level;
    protected final AbstractNetworkCableEntity entity;
    private final List<O> targets = Collections.synchronizedList(new ArrayList<>());

    public AbstractConnectionManager(ServerLevel level, AbstractNetworkCableEntity entity) {
        super();
        this.level = level;
        this.entity = entity;
    }

    public Set<Direction> getConnectedSides() {
        return Collections.unmodifiableSet(connectedSides);
    }

    public AbstractNetworkCableEntity getEntity() {
        return entity;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public List<O> getTargets() {
        return ImmutableList.copyOf(targets);
    }

    protected void addTarget(O target) {
        this.targets.add(target);
    }
}
