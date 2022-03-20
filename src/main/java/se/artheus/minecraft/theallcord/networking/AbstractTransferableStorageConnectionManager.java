package se.artheus.minecraft.theallcord.networking;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractTransferableStorageConnectionManager<A, O, C extends AbstractOfferedType<A, O>> extends AbstractConnectionManager<A, O, C> {
    private final long capacity;
    private long amount;
    private boolean isBlocked;

    public AbstractTransferableStorageConnectionManager(ServerLevel level, AbstractNetworkCableEntity entity, long capacity) {
        super(level, entity);
        this.capacity = capacity;
    }

    @Override
    protected Long createSnapshot() {
        return amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot;
    }

    public CableType getCableType() {
        return this.getEntity().getCableType();
    }

    public long getTransferRate() {
        return this.getEntity().getItemTransferRate();
    }

    public long getCapacity() {
        return capacity;
    }

    public abstract boolean isRemoved();

    public long getMaxInsert(@Nullable Direction side) {
        if (isBlocked()) return 0;

        return getTransferRate();
    }

    public long getMaxExtract(@Nullable Direction side) {
        if (isBlocked()) return 0;

        return getTransferRate();
    }

    public long insert(long maxAmount, TransactionContext transaction) {
        if (isRemoved() || isBlocked()) return 0;

        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(getTransferRate(), Math.min(maxAmount, getCapacity() - amount));

        if (inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;
            return inserted;
        }

        return 0;
    }

    public long extract(long maxAmount, TransactionContext transaction) {
        if (isRemoved() || isBlocked()) return 0;

        StoragePreconditions.notNegative(maxAmount);

        long extracted = Math.min(getTransferRate(), Math.min(maxAmount, amount));

        if (extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;
            return extracted;
        }

        return 0;
    }

    public long getAmount() {
        return this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }
}
