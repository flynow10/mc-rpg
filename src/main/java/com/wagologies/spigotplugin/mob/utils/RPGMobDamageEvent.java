package com.wagologies.spigotplugin.mob.utils;

import com.wagologies.spigotplugin.mob.Mob;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RPGMobDamageEvent extends Event implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    protected final Mob mob;
    protected int damage;

    public RPGMobDamageEvent(Mob mob, int damage) {
        this.mob = mob;
        this.damage = damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public Mob getMob() {
        return mob;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
