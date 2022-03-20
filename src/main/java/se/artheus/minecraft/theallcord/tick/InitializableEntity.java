package se.artheus.minecraft.theallcord.tick;

import net.minecraft.server.level.ServerLevel;

interface InitializableEntity {
    void initialize(ServerLevel level);
}
