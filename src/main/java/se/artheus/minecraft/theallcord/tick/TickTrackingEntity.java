package se.artheus.minecraft.theallcord.tick;

public interface TickTrackingEntity {
    long getLastTick();
    void updateTick(long tick);
}
