package se.artheus.minecraft.theallcord.tick;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;

public interface ITickingBlockEntity {
    void serverTick(ServerLevel level);

    void clientTick(ClientLevel level);
}
