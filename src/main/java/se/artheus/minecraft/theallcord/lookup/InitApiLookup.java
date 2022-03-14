package se.artheus.minecraft.theallcord.lookup;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import se.artheus.minecraft.theallcord.entities.AbstractPoweredNetworkEntity;
import se.artheus.minecraft.theallcord.entities.cables.AbstractCableEntity;
import team.reborn.energy.api.EnergyStorage;

public class InitApiLookup {

    public static void init() {
        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
            if (blockEntity instanceof AbstractPoweredNetworkEntity cableEntity) {
                return cableEntity.getEnergyStorage(direction);
            }

            return null;
        });
    }
}
