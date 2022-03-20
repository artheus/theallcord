package se.artheus.minecraft.theallcord.networking;

public interface DefaultedSnapshotParticipant<T> {
    default T createSnapshot() {
        return null;
    }

    default void readSnapshot(T snapshot) {

    }
}
