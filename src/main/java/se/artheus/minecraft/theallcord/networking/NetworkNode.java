package se.artheus.minecraft.theallcord.networking;

import se.artheus.minecraft.theallcord.entities.AbstractNetworkEntity;

import java.util.Objects;

public record NetworkNode(AbstractNetworkEntity entity) {
    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        NetworkNode that = (NetworkNode) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

    @Override
    public String toString() {
        return "NetworkNode{%s}".formatted(this.entity.toString());
    }
}
