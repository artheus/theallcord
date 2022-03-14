package se.artheus.minecraft.theallcord;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;
import team.reborn.energy.api.EnergyStorage;

public class InitApiLookup {

    public static void init() {
        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
            if (blockEntity instanceof AbstractCableEntity cableEntity) {
                return cableEntity.getEnergyStorage(direction);
            }
            return null;
        });

        ItemStorage.SIDED.registerFallback();
    }
}
