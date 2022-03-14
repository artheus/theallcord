package se.artheus.minecraft.theallcord.entities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import se.artheus.minecraft.theallcord.blocks.AbstractBlock;
import se.artheus.minecraft.theallcord.entities.cables.CableAdvancedEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableBasicEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableEliteEntity;
import se.artheus.minecraft.theallcord.entities.cables.CableUltimateEntity;
import se.artheus.minecraft.theallcord.entities.indicators.AEChannelIndicatorEntity;

import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ADVANCED_DENSE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_BASIC_DENSE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ELITE_DENSE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CABLE_ULTIMATE_DENSE;
import static se.artheus.minecraft.theallcord.blocks.Blocks.BLOCK_CHANNEL_INDICATOR;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CHANNEL_INDICATOR;

@SuppressWarnings("unchecked")
public class BlockEntities {
    public static BlockEntityType<AEChannelIndicatorEntity> ENTITY_TYPE_CHANNEL_INDICATOR;
    public static BlockEntityType<CableBasicEntity> ENTITY_TYPE_CABLE_BASIC;
    public static BlockEntityType<CableAdvancedEntity> ENTITY_TYPE_CABLE_ADVANCED;
    public static BlockEntityType<CableEliteEntity> ENTITY_TYPE_CABLE_ELITE;
    public static BlockEntityType<CableUltimateEntity> ENTITY_TYPE_CABLE_ULTIMATE;

    public static void registerBlockEntities() {
        ENTITY_TYPE_CHANNEL_INDICATOR = register(ID_ENTITY_CHANNEL_INDICATOR, AEChannelIndicatorEntity::new, BLOCK_CHANNEL_INDICATOR);

        ENTITY_TYPE_CABLE_BASIC = register(ID_ENTITY_CABLE_BASIC, CableBasicEntity::new, BLOCK_CABLE_BASIC, BLOCK_CABLE_BASIC_DENSE);
        ENTITY_TYPE_CABLE_ADVANCED = register(ID_ENTITY_CABLE_ADVANCED, CableAdvancedEntity::new, BLOCK_CABLE_ADVANCED, BLOCK_CABLE_ADVANCED_DENSE);
        ENTITY_TYPE_CABLE_ELITE = register(ID_ENTITY_CABLE_ELITE, CableEliteEntity::new, BLOCK_CABLE_ELITE, BLOCK_CABLE_ELITE_DENSE);
        ENTITY_TYPE_CABLE_ULTIMATE = register(ID_ENTITY_CABLE_ULTIMATE, CableUltimateEntity::new, BLOCK_CABLE_ULTIMATE, BLOCK_CABLE_ULTIMATE_DENSE);
    }

    private static <T extends AbstractEntity> BlockEntityType<T> register(ResourceLocation id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        var type = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.create(factory, blocks).build()
        );

        for (var block : blocks) {
            ((AbstractBlock<T>) block).setBlockEntityType(type);
        }

        return type;
    }
}
