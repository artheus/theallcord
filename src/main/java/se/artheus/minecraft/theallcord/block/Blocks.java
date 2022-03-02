package se.artheus.minecraft.theallcord.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

import static se.artheus.minecraft.theallcord.Mod.MOD_NS;

public class Blocks {

    public static BlockItem CHANNEL_INDICATOR_BLOCK_ITEM;
    public static BlockItem CABLE_BASIC_BLOCK_ITEM;
    public static BlockItem CABLE_BASIC_DENSE_BLOCK_ITEM;
    public static BlockItem CABLE_ADVANCED_BLOCK_ITEM;

    public static void registerBlocks() {
        // Channel indicator block
        CHANNEL_INDICATOR_BLOCK_ITEM = registerBlockWithGenericBlockItem(ChannelIndicatorBlock.ID, ChannelIndicatorBlock.INSTANCE, CreativeModeTab.TAB_MISC);

        // Basic cable
        Blocks.registerBlock(CableBasic.ID, CableBasic.INSTANCE);
        CABLE_BASIC_BLOCK_ITEM = new CableBlockItem(CableBasic.INSTANCE, new FabricItemSettings().group(CreativeModeTab.TAB_MISC));
        Blocks.registerBlockItem(CableBasic.ID, CABLE_BASIC_BLOCK_ITEM);

        // Basic cable (dense)
        Blocks.registerBlock(CableBasicDense.ID, CableBasicDense.INSTANCE);
        CABLE_BASIC_DENSE_BLOCK_ITEM = new CableBlockItem(CableBasicDense.INSTANCE, new FabricItemSettings().group(CreativeModeTab.TAB_MISC));
        Blocks.registerBlockItem(CableBasicDense.ID, CABLE_BASIC_DENSE_BLOCK_ITEM);

        // Advanced cable
        Blocks.registerBlock(CableAdvanced.ID, CableAdvanced.INSTANCE);
        CABLE_ADVANCED_BLOCK_ITEM = new CableBlockItem(CableAdvanced.INSTANCE, new FabricItemSettings().group(CreativeModeTab.TAB_MISC));
        Blocks.registerBlockItem(CableAdvanced.ID, CABLE_ADVANCED_BLOCK_ITEM);
    }

    static void registerBlock(String id, Block b) {
        Registry.register(Registry.BLOCK, new ResourceLocation(MOD_NS, id), b);
    }

    static void registerBlockItem(String id, BlockItem b) {
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_NS, id), b);
    }

    static BlockItem registerBlockWithGenericBlockItem(String id, Block b, CreativeModeTab tab) {
        registerBlock(id, b);

        var blockItem = new BlockItem(b, new FabricItemSettings().group(tab));
        registerBlockItem(id, blockItem);

        return blockItem;
    }
}
