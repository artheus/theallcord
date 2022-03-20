package se.artheus.minecraft.theallcord.networking;

import com.google.common.collect.Queues;
import joptsimple.internal.Strings;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.collections4.list.SetUniqueList;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.Mod;
import se.artheus.minecraft.theallcord.entities.AbstractNetworkEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Network implements Iterable<NetworkNode> {
    public static final Map<BlockPos, NetworkNode> NODES = new ConcurrentHashMap<>();
    public static final BlockApiLookup<NetworkNode, Direction> SIDED =
        BlockApiLookup.get(new ResourceLocation("theallcord:sided_network_node"), NetworkNode.class, Direction.class);
    private static final Collection<Network> NETWORKS = Collections.synchronizedCollection(new ArrayList<>());
    private static final Deque<AbstractNetworkEntity> NETWORK_REQUESTS = Queues.synchronizedDeque(new ArrayDeque<>());
    private static final Deque<NetworkNode> NODE_REMOVAL_QUEUE = Queues.synchronizedDeque(new ArrayDeque<>());

    static {
        ServerTickEvents.END_WORLD_TICK.register(Network::networkTick);
    }

    private final Collection<NetworkNode> nodes = Collections.synchronizedCollection(SetUniqueList.setUniqueList(new ArrayList<>()));
    private boolean removed = false;

    private Network() {

    }

    private Network(List<NetworkNode> nodes) {
        this.addNodes(nodes);
    }

    private Network(NetworkNode node) {
        this.addNode(node);
    }

    public static void removeNodeFor(AbstractNetworkEntity networkEntity) {
        synchronized (NODE_REMOVAL_QUEUE) {
            NODES.values().stream()
                .filter(n -> n.entity().equals(networkEntity))
                .forEach(NODE_REMOVAL_QUEUE::add);
        }
    }

    private static List<NetworkNode> getNodesConnectedToAndIncluding(NetworkNode node) {
        List<NetworkNode> connectedNodes = new ArrayList<>();
        Deque<NetworkNode> bfsQueue = new ArrayDeque<>();

        bfsQueue.add(node);

        while (!bfsQueue.isEmpty()) {
            var currentNode = bfsQueue.removeFirst();
            var level = currentNode.entity().getLevel();
            connectedNodes.add(currentNode);

            if (level==null) continue;

            for (var dir : Direction.values()) {
                var relativePos = currentNode.entity().getBlockPos().relative(dir);
                var ent = level.getBlockEntity(relativePos);
                var foundNode = NODES.get(relativePos);

                if (foundNode==null && ent instanceof AbstractNetworkEntity networkEntity) {
                    foundNode = new NetworkNode(networkEntity);
                }

                if (foundNode==null || connectedNodes.contains(foundNode)) continue;

                bfsQueue.add(foundNode);
                NODES.putIfAbsent(foundNode.entity().getBlockPos(), foundNode);
            }
        }

        return connectedNodes;
    }

    public static void requestNetworkFor(AbstractNetworkEntity networkEntity) {
        NETWORK_REQUESTS.add(networkEntity);
    }

    private static void networkTick(ServerLevel level) {
        synchronized (NODE_REMOVAL_QUEUE) {
            while (!NODE_REMOVAL_QUEUE.isEmpty()) {
                var node = NODE_REMOVAL_QUEUE.remove();
                var nodeNet = node.entity().getNetwork();

                if (nodeNet!=null) {
                    nodeNet.removeNode(node);
                    nodeNet.updateNetworkConnections();
                }

                NODES.remove(node.entity().getBlockPos(), node);
            }
        }

        if (!NETWORK_REQUESTS.isEmpty()) {
            var networkEntity = NETWORK_REQUESTS.removeFirst();

            // disregard entities already part of a network
            if (networkEntity.getNetwork()!=null) return;

            // Attempt to add entity to any neighboring network nodes networks.
            var addedToNeighborNetwork = false;
            for (var dir : Direction.values()) {
                var neighborNode = SIDED.find(level, networkEntity.getBlockPos().relative(dir), dir.getOpposite());

                if (!Objects.isNull(neighborNode) && neighborNode.entity().getNetwork()!=null && !neighborNode.entity().getNetwork().isRemoved()) {
                    addedToNeighborNetwork = true;
                    neighborNode.entity().getNetwork().updateNetworkConnections();
                }
            }
            if (addedToNeighborNetwork) return;

            // Create a new network for node
            var networkEntityDeque = new ArrayDeque<AbstractNetworkEntity>();
            networkEntityDeque.add(networkEntity);

            var network = new Network();
            NETWORKS.add(network);

            while (!networkEntityDeque.isEmpty()) {
                var ne = networkEntityDeque.removeFirst();
                var node = new NetworkNode(ne);

                network.addNode(node);
                ne.setNetwork(network);

                NODES.put(ne.getBlockPos(), node);

                for (var dir : Direction.values()) {
                    if (level.getBlockEntity(ne.getBlockPos().relative(dir)) instanceof AbstractNetworkEntity nextNetEntity
                        && !network.has(nextNetEntity)) {
                        networkEntityDeque.add(nextNetEntity);
                    }
                }
            }

            Mod.LOGGER.info("new network created: {}", network);
        }
    }

    private boolean isRemoved() {
        return this.removed;
    }

    private void setRemoved() {
        this.removed = true;
    }

    @NotNull
    @Override
    public Iterator<NetworkNode> iterator() {
        return this.nodes.iterator();
    }

    private void addNodes(List<NetworkNode> nodes) {
        this.nodes.addAll(nodes);
    }

    private void addNode(NetworkNode node) {
        this.nodes.add(node);
    }

    private void removeNode(NetworkNode node) {
        nodes.remove(node);
    }

    @Override
    public String toString() {
        var nodeString = Strings.join(nodes.stream().map(NetworkNode::toString).collect(Collectors.toSet()), ", ");

        return "Network{" +
            "nodes=" + nodeString +
            ", removed=" + removed +
            '}';
    }

    public void updateNetworkConnections() {
        if (isRemoved()) return;

        if (!this.isEmpty()) {
            List<Network> newNetworks = new ArrayList<>();

            for (var node : nodes) {
                if (newNetworks.stream().anyMatch(net -> net.has(node))) continue;

                if (!NODE_REMOVAL_QUEUE.contains(node) && !node.entity().isRemoved())
                    newNetworks.add(new Network(getNodesConnectedToAndIncluding(node)));
            }

            //Mod.LOGGER.info("Created {} new networks", newNetworks.size());

            for (var net : newNetworks) {
                NETWORKS.add(net);
                //Mod.LOGGER.info("  - contains {} nodes", net.nodes.size());
                net.nodes.forEach(n -> n.entity().setNetwork(net));
            }
        }

        this.nodes.clear();
        this.setRemoved();
        NETWORKS.remove(this);
    }

    public boolean has(NetworkNode node) {
        return this.nodes.contains(node);
    }

    public boolean has(AbstractNetworkEntity networkEntity) {
        return this.nodes.stream().anyMatch(node -> node.entity().equals(networkEntity));
    }

    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    public List<AbstractNetworkEntity> getEntities() {
        return this.nodes.stream().map(NetworkNode::entity).toList();
    }
}
