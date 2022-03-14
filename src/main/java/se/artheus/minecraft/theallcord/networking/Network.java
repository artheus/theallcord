package se.artheus.minecraft.theallcord.networking;

import org.apache.commons.collections4.list.SetUniqueList;
import se.artheus.minecraft.theallcord.entities.AbstractNetworkEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Network {
    private final List<AbstractNetworkEntity> cableEntities = SetUniqueList.setUniqueList(new ArrayList<>());

    public Network(AbstractNetworkEntity ...entities) {
        this.addEntities(entities);
    }

    public void addEntity(AbstractNetworkEntity entity) {
        addEntities(List.of(entity));
    }

    public void addEntities(List<AbstractNetworkEntity> entities) {
        this.cableEntities.addAll(entities);
    }

    private void addEntities(AbstractNetworkEntity... entities) {
        this.addEntities(Arrays.asList(entities));
    }

    public boolean has(AbstractNetworkEntity entity) {
        return cableEntities.contains(entity);
    }

    public boolean isEmpty() {
        return this.cableEntities.isEmpty();
    }

    public List<AbstractNetworkEntity> getEntities() {
        return this.cableEntities;
    }

    public <T extends AbstractNetworkEntity> List<T> getEntitiesOfType(Class<T> type) {
        return this.cableEntities.parallelStream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public static Network createMergedNetwork(Network net1, Network... nets) {
        Network mergedNet = new Network();

        mergedNet.addEntities(net1.getEntities());

        for (var net :
                nets) {
            mergedNet.addEntities(net.getEntities());
        }

        return mergedNet;
    }
}
