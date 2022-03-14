package se.artheus.minecraft.theallcord;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.artheus.minecraft.theallcord.lookup.InitApiLookup;
import se.artheus.minecraft.theallcord.blocks.Blocks;
import se.artheus.minecraft.theallcord.entities.BlockEntities;
import se.artheus.minecraft.theallcord.item.Items;
import se.artheus.minecraft.theallcord.log.NullLogger;
import se.artheus.minecraft.theallcord.tick.TickHandler;

public class Mod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("theallcord");
    public static final Logger NULL_LOGGER = new NullLogger();

    @Override
    public void onInitialize() {
        // Register blocks and items
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();

        // API Lookup
        InitApiLookup.init();

        // Initialize mod tick handler
        TickHandler.instance().init();
    }
}
