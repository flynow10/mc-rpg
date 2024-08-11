package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ConversationInteractionEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final RPGPlayer player;
    private final String interactionId;
    private final String response;

    public ConversationInteractionEvent(RPGPlayer player, String interactionId, String response) {
        this.player = player;
        this.interactionId = interactionId;
        this.response = response;
    }

    public RPGPlayer getPlayer() {
        return player;
    }

    public String getInteractionId() {
        return interactionId;
    }

    public String getResponse() {
        return response;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
