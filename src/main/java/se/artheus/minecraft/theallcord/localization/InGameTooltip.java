package se.artheus.minecraft.theallcord.localization;

import appeng.core.localization.LocalizationEnum;

public enum InGameTooltip implements LocalizationEnum {
    ColoredChannels("Colored channels:"),
    ColoredChannelsOf("- %1$s: %2$d of %3$d channels"),
    ChannelsOf("%1$d of %2$d Channels"),
    DeviceOffline("Device Offline"),
    DeviceOnline("Device Online");

    private final String englishText;

    InGameTooltip(String englishText) {
        this.englishText = englishText;
    }

    @Override
    public String getTranslationKey() {
        return "waila.theallcord." + name();
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

}
