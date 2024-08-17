package com.wagologies.spigotplugin.event.player;

import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RPGPlayerJoinEvent extends RPGPlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    public RPGPlayerJoinEvent(RPGPlayer who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
