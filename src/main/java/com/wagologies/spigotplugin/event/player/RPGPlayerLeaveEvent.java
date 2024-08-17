package com.wagologies.spigotplugin.event.player;

import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RPGPlayerLeaveEvent extends RPGPlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Campaign campaign;

    public RPGPlayerLeaveEvent(@NotNull RPGPlayer who, Campaign campaign) {
        super(who);
        this.campaign = campaign;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
