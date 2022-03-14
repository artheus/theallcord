package se.artheus.minecraft.theallcord.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.Sets;
import se.artheus.minecraft.theallcord.blocks.cables.AbstractCable;
import se.artheus.minecraft.theallcord.blocks.cables.CableAdvanced;
import se.artheus.minecraft.theallcord.blocks.cables.CableBasic;
import se.artheus.minecraft.theallcord.blocks.cables.CableElite;
import se.artheus.minecraft.theallcord.blocks.cables.CableUltimate;
import se.artheus.minecraft.theallcord.blocks.cables.BlockItemCable;
import se.artheus.minecraft.theallcord.entities.cables.CableAdvancedEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableBasicEntity;
import se.artheus.minecraft.theallcord.entities.indicators.AEChannelIndicatorEntity;
import se.artheus.minecraft.theallcord.blocks.indicators.AEChannelIndicator;

import java.util.Set;

import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ADVANCED_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_BASIC_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ELITE_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ULTIMATE_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CHANNEL_INDICATOR;

public class Blocks {

    private static final FabricItemSettings TAB_SETTING = new FabricItemSettings().group(CreativeModeTab.TAB_MISC);

    public static final AbstractBlock<AEChannelIndicatorEntity> BLOCK_CHANNEL_INDICATOR = new AEChannelIndicator();
    public static final AbstractCable<CableBasicEntity> BLOCK_CABLE_BASIC = new CableBasic<>(false);
    public static final AbstractCable<CableBasicEntity> BLOCK_CABLE_BASIC_DENSE = new CableBasic<>(true);
    public static final AbstractCable<CableAdvancedEntity> BLOCK_CABLE_ADVANCED = new CableAdvanced<>(false);
    public static final AbstractCable<CableAdvancedEntity> BLOCK_CABLE_ADVANCED_DENSE = new CableAdvanced<>(true);
    public static final AbstractCable<CableAdvancedEntity> BLOCK_CABLE_ELITE = new CableElite<>(false);
    public static final AbstractCable<CableAdvancedEntity> BLOCK_CABLE_ELITE_DENSE = new CableElite<>(true);
    public static final AbstractCable<CableAdvancedEntity> BLOCK_CABLE_ULTIMATE = new CableUltimate<>(false);
    public static final AbstractCable<CableAdvancedEntity> BLOCK_CABLE_ULTIMATE_DENSE = new CableUltimate<>(true);

    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CHANNEL_INDICATOR = new net.minecraft.world.item.BlockItem(BLOCK_CHANNEL_INDICATOR, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_BASIC = new BlockItemCable(BLOCK_CABLE_BASIC, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_BASIC_DENSE = new BlockItemCable(BLOCK_CABLE_BASIC_DENSE, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_ADVANCED = new BlockItemCable(BLOCK_CABLE_ADVANCED, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_ADVANCED_DENSE = new BlockItemCable(BLOCK_CABLE_ADVANCED_DENSE, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_ELITE = new BlockItemCable(BLOCK_CABLE_ELITE, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_ELITE_DENSE = new BlockItemCable(BLOCK_CABLE_ELITE_DENSE, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_ULTIMATE = new BlockItemCable(BLOCK_CABLE_ULTIMATE, TAB_SETTING);
    public static final net.minecraft.world.item.BlockItem BLOCK_ITEM_CABLE_ULTIMATE_DENSE = new BlockItemCable(BLOCK_CABLE_ULTIMATE_DENSE, TAB_SETTING);

    private static final Set<BlockMapper> blockMaps = Sets.newHashSet(
            // blocks
            new BlockMapper(ID_BLOCK_CHANNEL_INDICATOR, BLOCK_CHANNEL_INDICATOR, BLOCK_ITEM_CHANNEL_INDICATOR),

            // cables
            new BlockMapper(ID_BLOCK_CABLE_BASIC, BLOCK_CABLE_BASIC, BLOCK_ITEM_CABLE_BASIC),
            new BlockMapper(ID_BLOCK_CABLE_BASIC_DENSE, BLOCK_CABLE_BASIC_DENSE, BLOCK_ITEM_CABLE_BASIC_DENSE),
            new BlockMapper(ID_BLOCK_CABLE_ADVANCED, BLOCK_CABLE_ADVANCED, BLOCK_ITEM_CABLE_ADVANCED),
            new BlockMapper(ID_BLOCK_CABLE_ADVANCED_DENSE, BLOCK_CABLE_ADVANCED_DENSE, BLOCK_ITEM_CABLE_ADVANCED_DENSE),
            new BlockMapper(ID_BLOCK_CABLE_ELITE, BLOCK_CABLE_ELITE, BLOCK_ITEM_CABLE_ELITE),
            new BlockMapper(ID_BLOCK_CABLE_ELITE_DENSE, BLOCK_CABLE_ELITE_DENSE, BLOCK_ITEM_CABLE_ELITE_DENSE),
            new BlockMapper(ID_BLOCK_CABLE_ULTIMATE, BLOCK_CABLE_ULTIMATE, BLOCK_ITEM_CABLE_ULTIMATE),
            new BlockMapper(ID_BLOCK_CABLE_ULTIMATE_DENSE, BLOCK_CABLE_ULTIMATE_DENSE, BLOCK_ITEM_CABLE_ULTIMATE_DENSE)
    );

    public static void registerBlocks() {
        for (var mapper : blockMaps) {
            Blocks.registerBlock(mapper.id, mapper.block);

            if (mapper.blockItem != null) {
                registerBlockItem(mapper.id, mapper.blockItem);
            }
        }
    }

    static void registerBlock(ResourceLocation id, Block b) {
        Registry.register(Registry.BLOCK, id, b);
    }

    static void registerBlockItem(ResourceLocation id, net.minecraft.world.item.BlockItem b) {
        Registry.register(Registry.ITEM, id, b);
    }

    private record BlockMapper(ResourceLocation id, AbstractBlock<?> block, @Nullable net.minecraft.world.item.BlockItem blockItem) {
    }
}
