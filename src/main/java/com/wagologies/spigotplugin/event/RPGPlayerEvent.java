package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.player.PlayerEvent;

public abstract class RPGPlayerEvent extends PlayerEvent {
    private final RPGPlayer rpgPlayer;
    public RPGPlayerEvent(RPGPlayer who) {
        super(who.getPlayer());
        this.rpgPlayer = who;
    }

    public RPGPlayer getRPGPlayer() {
        return rpgPlayer;
    }
}
