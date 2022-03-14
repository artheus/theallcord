package se.artheus.fabric.gas.api;

import net.minecraft.core.Registry;

public class Gases {

    public static final Gas EMPTY = null;

    public Gases() {
    }

    public static <T extends Gas> T register(String id, T value) {
        return Registry.register(Registry.FLUID, id, value);
    }
}
