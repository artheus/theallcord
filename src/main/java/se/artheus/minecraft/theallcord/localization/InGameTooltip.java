package se.artheus.minecraft.theallcord.localization;

import appeng.core.localization.LocalizationEnum;

public enum InGameTooltip implements LocalizationEnum {
    ChannelsOf("%1$d of %2$d Channels"),
    DeviceOffline("Device Offline"),
    DeviceOnline("Device Online"),
    ErrorControllerConflict("Error: Controller Conflict"),
    ErrorNestedP2PTunnel("Error: Nested P2P Tunnel"),
    ErrorTooManyChannels("Error: Too Many Channels");

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
