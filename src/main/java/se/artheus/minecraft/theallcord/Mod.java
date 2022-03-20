package se.artheus.minecraft.theallcord;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.artheus.minecraft.theallcord.blocks.Blocks;
import se.artheus.minecraft.theallcord.entities.BlockEntities;
import se.artheus.minecraft.theallcord.item.Items;
import se.artheus.minecraft.theallcord.lookup.InitApiLookup;
import se.artheus.minecraft.theallcord.tick.TickHandler;

public class Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("theallcord");

    @Override
    public void onInitialize() {
        {
            // Register blocks and items
            Blocks.registerBlocks();
            BlockEntities.registerBlockEntities();
            Items.registerItems();

            // API Lookup
            InitApiLookup.init();

            // Initialize mod tick handler
            TickHandler.instance().init();

            // Destroy TickHandler when server is stopping
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> TickHandler.instance().destroy());
        }
    }
}
