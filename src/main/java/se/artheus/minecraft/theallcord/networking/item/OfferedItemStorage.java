package se.artheus.minecraft.theallcord.networking.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import se.artheus.minecraft.theallcord.networking.AbstractOfferedType;

@SuppressWarnings("UnstableApiUsage")
public class OfferedItemStorage extends AbstractOfferedType<Storage<ItemVariant>, Direction> {
    public OfferedItemStorage(ServerLevel level, BlockPos blockPos, Direction direction) {
        super(level, blockPos, direction, ItemStorage.SIDED);
    }
}