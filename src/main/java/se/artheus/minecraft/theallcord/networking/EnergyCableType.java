package se.artheus.minecraft.theallcord.networking;

public enum EnergyCableType {
    BASIC(256),
    ADVANCED(2048),
    ELITE(16384),
    ULTIMATE(131072);

    private final long transferRate;

    EnergyCableType(long transferRate) {
        this.transferRate = transferRate;
    }

    public long getTransferRate(boolean isDense) {
        return isDense ? this.transferRate * 4 : this.transferRate;
    }
}
