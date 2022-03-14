package se.artheus.minecraft.theallcord.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import se.artheus.minecraft.theallcord.block.AbstractBlock;

import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_ADVANCED_DENSE;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_BASIC_DENSE;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_ELITE_DENSE;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CABLE_ULTIMATE_DENSE;
import static se.artheus.minecraft.theallcord.block.Blocks.BLOCK_CHANNEL_INDICATOR;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ADVANCED;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_BASIC;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ELITE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CABLE_ULTIMATE;
import static se.artheus.minecraft.theallcord.resource.ResourceLocations.ID_ENTITY_CHANNEL_INDICATOR;

@SuppressWarnings("unchecked")
public class BlockEntities {
    public static BlockEntityType<EntityChannelIndicator> ENTITY_TYPE_CHANNEL_INDICATOR;
    public static BlockEntityType<EntityCableBasic> ENTITY_TYPE_CABLE_BASIC;
    public static BlockEntityType<EntityCableAdvanced> ENTITY_TYPE_CABLE_ADVANCED;
    public static BlockEntityType<EntityCableElite> ENTITY_TYPE_CABLE_ELITE;
    public static BlockEntityType<EntityCableUltimate> ENTITY_TYPE_CABLE_ULTIMATE;

    public static void registerBlockEntities() {
        ENTITY_TYPE_CHANNEL_INDICATOR = register(ID_ENTITY_CHANNEL_INDICATOR, EntityChannelIndicator::new, BLOCK_CHANNEL_INDICATOR);

        ENTITY_TYPE_CABLE_BASIC = register(ID_ENTITY_CABLE_BASIC, EntityCableBasic::new, BLOCK_CABLE_BASIC, BLOCK_CABLE_BASIC_DENSE);
        ENTITY_TYPE_CABLE_ADVANCED = register(ID_ENTITY_CABLE_ADVANCED, EntityCableAdvanced::new, BLOCK_CABLE_ADVANCED, BLOCK_CABLE_ADVANCED_DENSE);
        ENTITY_TYPE_CABLE_ELITE = register(ID_ENTITY_CABLE_ELITE, EntityCableElite::new, BLOCK_CABLE_ELITE, BLOCK_CABLE_ELITE_DENSE);
        ENTITY_TYPE_CABLE_ULTIMATE = register(ID_ENTITY_CABLE_ULTIMATE, EntityCableUltimate::new, BLOCK_CABLE_ULTIMATE, BLOCK_CABLE_ULTIMATE_DENSE);
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
