package se.artheus.minecraft.theallcord.localization;

import appeng.api.util.AEColor;
import appeng.block.networking.CableBusBlock;
import joptsimple.internal.Strings;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import se.artheus.minecraft.theallcord.entities.AbstractEntity;
import se.artheus.minecraft.theallcord.entities.AbstractNetworkEntity;
import se.artheus.minecraft.theallcord.entities.cables.AbstractNetworkCableEntity;

import java.util.ArrayList;
import java.util.List;

import static se.artheus.minecraft.theallcord.localization.InGameTooltip.ColoredChannels;
import static se.artheus.minecraft.theallcord.localization.InGameTooltip.ColoredChannelsOf;
import static se.artheus.minecraft.theallcord.localization.InGameTooltip.DeviceOffline;
import static se.artheus.minecraft.theallcord.localization.InGameTooltip.DeviceOnline;

public class WailaModule implements IWailaPlugin {

    public void register(IRegistrar registrar) {
        registrar.addDisplayItem(new IconProvider(), CableBusBlock.class);

        var blockEntityProvider = new BlockEntityDataProvider();
        registrar.addBlockData(blockEntityProvider, AbstractEntity.class);
        registrar.addComponent(blockEntityProvider, TooltipPosition.BODY, AbstractEntity.class);
    }

    private static class BlockEntityDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

        private static final String TAG_CABLE_COLORED_CHANNELS = "theallcord:cableColoredChannels";
        private static final String TAG_DEVICE_ONLINE = "theallcord:deviceOnline";
        private static final String TAG_ENERGY_STORAGE = "theallcord:energyStorage";

        private static final String SERVER_DATA_COMPOUND_KEY = "theallcord:server_compound_data";

        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            var serverData = accessor.getServerData().getCompound(SERVER_DATA_COMPOUND_KEY);

            if (serverData.contains(TAG_CABLE_COLORED_CHANNELS, Tag.TAG_STRING)) {
                var tagName = serverData.getString(TAG_CABLE_COLORED_CHANNELS);
                var names = tagName.split(",");

                if (names.length > 0) {
                    tooltip.add(ColoredChannels.text());

                    for (var n : names) {
                        var chanData = n.split(":");
                        if (chanData.length!=3) continue;

                        tooltip.add(ColoredChannelsOf.text(
                            new TranslatableComponent(AEColor.valueOf(chanData[0]).translationKey),
                            chanData[1],
                            chanData[2]
                        ).withStyle(ChatFormatting.ITALIC));
                    }
                }
            }

            if (serverData.contains(TAG_DEVICE_ONLINE, Tag.TAG_BYTE)) {
                if (serverData.getBoolean(TAG_DEVICE_ONLINE)) {
                    tooltip.add(DeviceOnline.text().withStyle(ChatFormatting.GREEN));
                } else {
                    tooltip.add(DeviceOffline.text().withStyle(ChatFormatting.RED));
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag serverData, ServerPlayer player, Level world, BlockEntity be) {
            var data = new CompoundTag();

            if (be instanceof AbstractNetworkCableEntity ace) {

                if (ace.getGridNodeConnectionManager()==null) return;

                final List<String> tagNames = new ArrayList<>();

                ace.getGridNodeConnectionManager().getManagedNodes().forEach((color, mainNode) -> {
                    if (mainNode.getNode()==null) return;

                    var maxChannels = mainNode.getNode().getMaxChannels();
                    var usedChannels = mainNode.getNode().getUsedChannels();

                    tagNames.add("%s:%d:%d".formatted(color.name(), usedChannels, maxChannels));
                });

                data.putString(TAG_CABLE_COLORED_CHANNELS, Strings.join(tagNames, ","));
            }

            if (be instanceof AbstractNetworkEntity entity) {
                data.putBoolean(TAG_DEVICE_ONLINE, entity.isOnline());
            }

            if (!data.isEmpty()) {
                serverData.put(SERVER_DATA_COMPOUND_KEY, data);
            }
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