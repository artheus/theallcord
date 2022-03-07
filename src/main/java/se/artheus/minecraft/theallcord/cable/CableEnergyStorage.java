package se.artheus.minecraft.theallcord.cable;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

public class CableEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    public long amount = 0;
    public final long capacity;
    public final long maxInsert, maxExtract;

    public CableEnergyStorage(boolean dense, long capacity) {
        capacity = dense ? capacity * 4 : capacity;

        this.capacity = capacity;
        this.maxInsert = capacity;
        this.maxExtract = capacity;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(maxInsert, Math.min(maxAmount, capacity - amount));

        if (inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;
            return inserted;
        }

        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

        if (extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;
            return extracted;
        }

        return 0;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    protected Long createSnapshot() {
        return this.amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.amount = snapshot;
    }
}
