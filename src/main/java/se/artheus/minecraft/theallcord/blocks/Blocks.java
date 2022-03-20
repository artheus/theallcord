package se.artheus.minecraft.theallcord.blocks;

import appeng.api.util.AEColor;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.Sets;
import se.artheus.minecraft.theallcord.entities.cables.CableAdvancedEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableBasicEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableEliteEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableUltimateEntity;
import se.artheus.minecraft.theallcord.entities.indicators.AEChannelIndicatorEntity;
import se.artheus.minecraft.theallcord.networking.CableType;

import java.util.EnumSet;
import java.util.Set;

import static appeng.api.util.AEColor.BLACK;
import static appeng.api.util.AEColor.BLUE;
import static appeng.api.util.AEColor.GREEN;
import static appeng.api.util.AEColor.ORANGE;
import static appeng.api.util.AEColor.RED;
import static appeng.api.util.AEColor.TRANSPARENT;
import static appeng.api.util.AEColor.YELLOW;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ADVANCED_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_BASIC_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ELITE_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CABLE_ULTIMATE_DENSE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_BLOCK_CHANNEL_INDICATOR;

/**
 * Class holding the {@link Block} and {@link BlockItem} instances for all mod blocks
 * <p>
 * Registration of {@link Block}'s and {@link BlockItem}'s are also done in this class
 */
public class Blocks {

    // Cable AE color sets
    public static final Set<AEColor> CABLE_BASIC_COLORS = EnumSet.of(TRANSPARENT);
    public static final Set<AEColor> CABLE_ADVANCED_COLORS = EnumSet.of(TRANSPARENT, BLACK, RED, YELLOW);
    public static final Set<AEColor> CABLE_ELITE_COLORS = EnumSet.of(TRANSPARENT, BLACK, RED, YELLOW, GREEN, BLUE, ORANGE);
    public static final Set<AEColor> CABLE_ULTIMATE_COLORS = EnumSet.allOf(AEColor.class);

    // Indicator blocks
    public static final AbstractBlock<AEChannelIndicatorEntity> BLOCK_CHANNEL_INDICATOR = new BlockAEChannelIndicator();

    // Cable blocks
    public static final BlockCable<CableBasicEntity> BLOCK_CABLE_BASIC = new BlockCable<>(CableType.BASIC, false, CABLE_BASIC_COLORS);
    public static final BlockCable<CableBasicEntity> BLOCK_CABLE_BASIC_DENSE = new BlockCable<>(CableType.BASIC, true, CABLE_BASIC_COLORS);
    public static final BlockCable<CableAdvancedEntity> BLOCK_CABLE_ADVANCED = new BlockCable<>(CableType.ADVANCED, false, CABLE_ADVANCED_COLORS);
    public static final BlockCable<CableAdvancedEntity> BLOCK_CABLE_ADVANCED_DENSE = new BlockCable<>(CableType.ADVANCED, true, CABLE_ADVANCED_COLORS);
    public static final BlockCable<CableEliteEntity> BLOCK_CABLE_ELITE = new BlockCable<>(CableType.ELITE, false, CABLE_ELITE_COLORS);
    public static final BlockCable<CableEliteEntity> BLOCK_CABLE_ELITE_DENSE = new BlockCable<>(CableType.ELITE, true, CABLE_ELITE_COLORS);
    public static final BlockCable<CableUltimateEntity> BLOCK_CABLE_ULTIMATE = new BlockCable<>(CableType.ULTIMATE, false, CABLE_ULTIMATE_COLORS);
    public static final BlockCable<CableUltimateEntity> BLOCK_CABLE_ULTIMATE_DENSE = new BlockCable<>(CableType.ULTIMATE, true, CABLE_ULTIMATE_COLORS);

    // Tab settings
    private static final FabricItemSettings TAB_SETTING_MISC = new FabricItemSettings().group(CreativeModeTab.TAB_MISC);

    // Block items
    public static final BlockItem BLOCK_ITEM_CHANNEL_INDICATOR = new BlockItem(BLOCK_CHANNEL_INDICATOR, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_BASIC = new BlockItemCable(BLOCK_CABLE_BASIC, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_BASIC_DENSE = new BlockItemCable(BLOCK_CABLE_BASIC_DENSE, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_ADVANCED = new BlockItemCable(BLOCK_CABLE_ADVANCED, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_ADVANCED_DENSE = new BlockItemCable(BLOCK_CABLE_ADVANCED_DENSE, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_ELITE = new BlockItemCable(BLOCK_CABLE_ELITE, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_ELITE_DENSE = new BlockItemCable(BLOCK_CABLE_ELITE_DENSE, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_ULTIMATE = new BlockItemCable(BLOCK_CABLE_ULTIMATE, TAB_SETTING_MISC);
    public static final BlockItem BLOCK_ITEM_CABLE_ULTIMATE_DENSE = new BlockItemCable(BLOCK_CABLE_ULTIMATE_DENSE, TAB_SETTING_MISC);

    /**
     * Set of mappings between {@link ResourceLocation}'s, {@link Block}'s and {@link BlockItem}'s
     */
    private static final Set<BlockMapper> blockMaps = Sets.newHashSet(
        // blocks
        map(ID_BLOCK_CHANNEL_INDICATOR, BLOCK_CHANNEL_INDICATOR, BLOCK_ITEM_CHANNEL_INDICATOR),

        // cables
        map(ID_BLOCK_CABLE_BASIC, BLOCK_CABLE_BASIC, BLOCK_ITEM_CABLE_BASIC),
        map(ID_BLOCK_CABLE_BASIC_DENSE, BLOCK_CABLE_BASIC_DENSE, BLOCK_ITEM_CABLE_BASIC_DENSE),
        map(ID_BLOCK_CABLE_ADVANCED, BLOCK_CABLE_ADVANCED, BLOCK_ITEM_CABLE_ADVANCED),
        map(ID_BLOCK_CABLE_ADVANCED_DENSE, BLOCK_CABLE_ADVANCED_DENSE, BLOCK_ITEM_CABLE_ADVANCED_DENSE),
        map(ID_BLOCK_CABLE_ELITE, BLOCK_CABLE_ELITE, BLOCK_ITEM_CABLE_ELITE),
        map(ID_BLOCK_CABLE_ELITE_DENSE, BLOCK_CABLE_ELITE_DENSE, BLOCK_ITEM_CABLE_ELITE_DENSE),
        map(ID_BLOCK_CABLE_ULTIMATE, BLOCK_CABLE_ULTIMATE, BLOCK_ITEM_CABLE_ULTIMATE),
        map(ID_BLOCK_CABLE_ULTIMATE_DENSE, BLOCK_CABLE_ULTIMATE_DENSE, BLOCK_ITEM_CABLE_ULTIMATE_DENSE)
    );

    /**
     * Looping through the mappings, to register all {@link Block}'s and {@link BlockItem}'s
     */
    public static void registerBlocks() {
        for (var mapper : blockMaps) {
            Blocks.registerBlock(mapper.id, mapper.block);

            if (mapper.blockItem!=null) {
                registerBlockItem(mapper.id, mapper.blockItem);
            }
        }
    }

    /**
     * Static method for adding a {@link Block} to the registry
     *
     * @param id {@link ResourceLocation} for {@link Block} registration
     * @param b  {@link Block} to register
     */
    static void registerBlock(ResourceLocation id, Block b) {
        Registry.register(Registry.BLOCK, id, b);
    }

    /**
     * Static method for adding a {@link BlockItem} to the registry
     *
     * @param id {@link ResourceLocation} for {@link BlockItem} registration
     * @param b  {@link BlockItem} to register
     */
    static void registerBlockItem(ResourceLocation id, BlockItem b) {
        Registry.register(Registry.ITEM, id, b);
    }

    /**
     * @param id        {@link ResourceLocation} for registration
     * @param block     {@link Block} mapped for the given {@link ResourceLocation}
     * @param blockItem {@link BlockItem} mapped for the given {@link ResourceLocation}
     * @return {@link BlockMapper} as a utility for easier {@link Block} registration
     */
    private static BlockMapper map(@NotNull ResourceLocation id, @NotNull AbstractBlock<?> block, @Nullable BlockItem blockItem) {
        return new BlockMapper(id, block, blockItem);
    }

    /**
     * Simple mapper record mapping {@link ResourceLocation}'s, {@link Block}'s and {@link BlockItem}'s
     * for an easier for-loop registration.
     */
    private record BlockMapper(@NotNull ResourceLocation id,
                               @NotNull AbstractBlock<?> block,
                               @Nullable BlockItem blockItem) {
    }
}
