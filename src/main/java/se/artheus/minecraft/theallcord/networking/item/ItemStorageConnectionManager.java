package se.artheus.minecraft.theallcord.networking.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;
import se.artheus.minecraft.theallcord.networking.AbstractTransferableStorageConnectionManager;
import se.artheus.minecraft.theallcord.tick.ITickingEntity;

import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class ItemStorageConnectionManager extends AbstractTransferableStorageConnectionManager<Storage<ItemVariant>, Direction, OfferedItemStorage> implements ITickingEntity {
    public ItemStorageConnectionManager(@NotNull ServerLevel level, @NotNull AbstractNetworkCableEntity entity) {
        super(
            level,
            entity,
            entity.getEnergyTransferRate() * Direction.values().length
        );

        Arrays.stream(Direction.values())
            .forEach(dir -> this.addTarget(
                new OfferedItemStorage(level, entity.getBlockPos().relative(dir), dir.getOpposite())
            ));
    }

    @Override
    public BlockPos getBlockPos() {
        return getEntity().getBlockPos();
    }

    @Override
    public boolean isRemoved() {
        return getEntity().isRemoved();
    }
}
