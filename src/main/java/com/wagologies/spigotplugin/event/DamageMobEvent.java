package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.mob.Mob;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageMobEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private int damage;
    private final Mob mob;
    private final EntityDamageEvent baseEvent;

    public DamageMobEvent(Mob mob, int damage, EntityDamageEvent baseEvent) {
        this.damage = damage;
        this.mob = mob;
        this.baseEvent = baseEvent;
    }

    public Mob getMob() {
        return mob;
    }

    public int getDamage() {
        return damage;
    }

    public EntityDamageEvent getBaseEvent() {
        return baseEvent;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return baseEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        baseEvent.setCancelled(b);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
