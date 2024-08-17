package com.wagologies.spigotplugin.event.player;

import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.HandlerList;

public class RPGPlayerDeathEvent extends RPGPlayerEvent {
    public static HandlerList handlerList = new HandlerList();
    private final DamageSource damageSource;
    public RPGPlayerDeathEvent(RPGPlayer who, DamageSource damageSource) {
        super(who);
        this.damageSource = damageSource;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
