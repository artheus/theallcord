package se.artheus.minecraft.theallcord;

import appeng.block.networking.CableBusBlock;
import joptsimple.internal.Strings;
import mcp.mobius.waila.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import se.artheus.minecraft.theallcord.block.entity.AbstractCableEntity;
import se.artheus.minecraft.theallcord.block.entity.AbstractEntity;
import se.artheus.minecraft.theallcord.localization.InGameTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class WailaModule implements IWailaPlugin {

    public void register(IRegistrar registrar) {
        registrar.addDisplayItem(new IconProvider(), CableBusBlock.class);

        var blockEntityProvider = new BlockEntityDataProvider();
        registrar.addBlockData(blockEntityProvider, AbstractCableEntity.class);
        registrar.addComponent(blockEntityProvider, TooltipPosition.BODY, AbstractCableEntity.class);
    }

    private static class BlockEntityDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

        private static final String TAG_MAX_CHANNELS = "maxChannels";
        private static final String TAG_USED_CHANNELS = "usedChannels";
        private static final String TAG_CABLE_COLORS = "cableColors";

        private static final String SERVER_CABLE_COMPOUND_KEY = "AllCordCable";

        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            var serverData = accessor.getServerData().getCompound(SERVER_CABLE_COMPOUND_KEY);

            if (serverData.contains(TAG_MAX_CHANNELS, Tag.TAG_INT)) {
                var usedChannels = serverData.getInt(TAG_USED_CHANNELS);
                var maxChannels = serverData.getInt(TAG_MAX_CHANNELS);

                tooltip.add(InGameTooltip.ChannelsOf.text(usedChannels, maxChannels));
            }

            if (serverData.contains(TAG_CABLE_COLORS, Tag.TAG_STRING)) {
                var tagName = serverData.getString(TAG_CABLE_COLORS);
                var names = tagName.split(",");

                if (names.length > 0) {
                    tooltip.add(new TextComponent("colors:"));
                    for (var n : names) {
                        tooltip.add(new TextComponent(" - %s".formatted(n)).withStyle(Style.EMPTY.withItalic(true)));
                    }
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag serverData, ServerPlayer player, Level world, BlockEntity be) {
            var cableData = new CompoundTag();
            var maxChannels = new LongAdder();
            var usedChannels = new LongAdder();
            final List<String> tagNames = new ArrayList<>();

            if (be instanceof AbstractCableEntity ace) {
                ace.getManagedNodes().forEach((color, mainNode) -> {
                    if (mainNode.getNode() == null) return;

                    maxChannels.add(mainNode.getNode().getMaxChannels());
                    usedChannels.add(mainNode.getNode().getUsedChannels());

                    tagNames.add(color.getEnglishName().toLowerCase());
                });

                cableData.putInt(TAG_USED_CHANNELS, usedChannels.intValue());
                cableData.putInt(TAG_MAX_CHANNELS, maxChannels.intValue());
                cableData.putString(TAG_CABLE_COLORS, Strings.join(tagNames, ","));
            }

            serverData.put(SERVER_CABLE_COMPOUND_KEY, cableData);
        }
    }

    private static class IconProvider implements IBlockComponentProvider {
        @Override
        public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
            var blockEntity = accessor.getBlockEntity();

            if (blockEntity instanceof AbstractEntity) {
                return ((AbstractEntity) blockEntity).asItemStack();
            }

            return ItemStack.EMPTY;
        }
    }
}