package se.artheus.minecraft.theallcord;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.artheus.minecraft.theallcord.block.Blocks;
import se.artheus.minecraft.theallcord.block.entity.BlockEntities;
import se.artheus.minecraft.theallcord.item.Items;

public class Mod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("theallcord");

    public static final String MOD_NS = "theallcord";

    @Override
    public void onInitialize() {
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();
    }

}
