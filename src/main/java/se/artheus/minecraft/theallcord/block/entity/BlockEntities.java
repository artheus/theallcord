package se.artheus.minecraft.theallcord.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import se.artheus.minecraft.theallcord.block.CableAdvanced;
import se.artheus.minecraft.theallcord.block.CableBasic;
import se.artheus.minecraft.theallcord.block.CableBasicDense;
import se.artheus.minecraft.theallcord.block.ChannelIndicatorBlock;

import static se.artheus.minecraft.theallcord.Mod.MOD_NS;

public class BlockEntities {

    public static BlockEntityType<CableBasicEntity> CABLE_BASIC_ENTITY;
    public static BlockEntityType<CableBasicDenseEntity> CABLE_BASIC_DENSE_ENTITY;
    public static BlockEntityType<CableAdvancedEntity> CABLE_ADVANCED_ENTITY;

    public static BlockEntityType<ChannelIndicatorBlockEntity> CHANNEL_INDICATOR_ENTITY;

    public static void registerBlockEntities() {
        // Channel indicator entity
        CHANNEL_INDICATOR_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new ResourceLocation(MOD_NS, ChannelIndicatorBlockEntity.ID),
                FabricBlockEntityTypeBuilder.create(ChannelIndicatorBlockEntity::new, ChannelIndicatorBlock.INSTANCE).build(null)
        );

        ChannelIndicatorBlock.INSTANCE.setBlockEntityType(CHANNEL_INDICATOR_ENTITY);

        // Basic cable entity
        CABLE_BASIC_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new ResourceLocation(MOD_NS, CableBasicEntity.ID),
                FabricBlockEntityTypeBuilder.create(CableBasicEntity::new, CableBasic.INSTANCE).build(null)
        );

        CableBasic.INSTANCE.setBlockEntityType(CABLE_BASIC_ENTITY);

        // Basic cable (dense) entity
        CABLE_BASIC_DENSE_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new ResourceLocation(MOD_NS, CableBasicDenseEntity.ID),
                FabricBlockEntityTypeBuilder.create(CableBasicDenseEntity::new, CableBasicDense.INSTANCE).build(null)
        );

        CableBasicDense.INSTANCE.setBlockEntityType(CABLE_BASIC_DENSE_ENTITY);

        // Advanced cable entity
        CABLE_ADVANCED_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new ResourceLocation(MOD_NS, CableAdvancedEntity.ID),
                FabricBlockEntityTypeBuilder.create(CableAdvancedEntity::new, CableAdvanced.INSTANCE).build(null)
        );

        CableAdvanced.INSTANCE.setBlockEntityType(CABLE_ADVANCED_ENTITY);
    }
}
