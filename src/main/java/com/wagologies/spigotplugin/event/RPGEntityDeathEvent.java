package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.RPGEntity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RPGEntityDeathEvent extends RPGEntityEvent {
    public static final HandlerList handlerList = new HandlerList();
    private final DamageSource damageSource;

    public RPGEntityDeathEvent(@NotNull RPGEntity entity, @NotNull DamageSource damageSource) {
        super(entity);
        this.damageSource = damageSource;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
