package se.artheus.minecraft.theallcord.tick;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicLong;

public class TickCounter {
    private static final AtomicLong value;

    private static void inc() {
        value.getAndAdd(1);
    }

    public static long current() {
        return value.get();
    }

    public static boolean equals(long val) {
        return value.get() == val;
    }

    static {
        value = new AtomicLong();
        ServerTickEvents.START_SERVER_TICK.register(server -> inc());
    }
}
