package se.artheus.minecraft.theallcord.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.Sets;
import se.artheus.minecraft.theallcord.block.entity.EntityCableAdvanced;
import se.artheus.minecraft.theallcord.block.entity.EntityCableBasic;
import se.artheus.minecraft.theallcord.block.entity.ChannelIndicatorBlockEntity;

import java.util.Set;

import static se.artheus.minecraft.theallcord.resource.ResourceLocations.*;

public class Blocks {

    private static final FabricItemSettings TAB_SETTING = new FabricItemSettings().group(CreativeModeTab.TAB_MISC);

    public static final AbstractBlock<ChannelIndicatorBlockEntity> BLOCK_CHANNEL_INDICATOR = new BlockChannelIndicator();
    public static final AbstractBlock<EntityCableBasic> BLOCK_CABLE_BASIC = new BlockCableBasic<>(false);
    public static final AbstractBlock<EntityCableBasic> BLOCK_CABLE_BASIC_DENSE = new BlockCableBasic<>(true);
    public static final AbstractBlock<EntityCableAdvanced> BLOCK_CABLE_ADVANCED = new BlockCableAdvanced<>(false);
    public static final AbstractBlock<EntityCableAdvanced> BLOCK_CABLE_ADVANCED_DENSE = new BlockCableAdvanced<>(true);
    public static final AbstractBlock<EntityCableAdvanced> BLOCK_CABLE_ELITE = new BlockCableElite<>(false);
    public static final AbstractBlock<EntityCableAdvanced> BLOCK_CABLE_ELITE_DENSE = new BlockCableElite<>(true);
    public static final AbstractBlock<EntityCableAdvanced> BLOCK_CABLE_ULTIMATE = new BlockCableUltimate<>(false);
    public static final AbstractBlock<EntityCableAdvanced> BLOCK_CABLE_ULTIMATE_DENSE = new BlockCableUltimate<>(true);

    public static final BlockItem BLOCK_ITEM_CHANNEL_INDICATOR = new BlockItem(BLOCK_CHANNEL_INDICATOR, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_BASIC = new BlockItemCable(BLOCK_CABLE_BASIC, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_BASIC_DENSE = new BlockItemCable(BLOCK_CABLE_BASIC_DENSE, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_ADVANCED = new BlockItemCable(BLOCK_CABLE_ADVANCED, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_ADVANCED_DENSE = new BlockItemCable(BLOCK_CABLE_ADVANCED_DENSE, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_ELITE = new BlockItemCable(BLOCK_CABLE_ELITE, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_ELITE_DENSE = new BlockItemCable(BLOCK_CABLE_ELITE_DENSE, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_ULTIMATE = new BlockItemCable(BLOCK_CABLE_ULTIMATE, TAB_SETTING);
    public static final BlockItem BLOCK_ITEM_CABLE_ULTIMATE_DENSE = new BlockItemCable(BLOCK_CABLE_ULTIMATE_DENSE, TAB_SETTING);

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

    static void registerBlockItem(ResourceLocation id, BlockItem b) {
        Registry.register(Registry.ITEM, id, b);
    }

    private record BlockMapper(ResourceLocation id, AbstractBlock<?> block, @Nullable BlockItem blockItem) {
    }
}
