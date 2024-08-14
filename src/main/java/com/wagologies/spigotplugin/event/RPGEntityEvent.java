package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.entity.RPGEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class RPGEntityEvent extends Event {
    private final RPGEntity entity;

    public RPGEntityEvent(@NotNull RPGEntity entity) {
        this.entity = entity;
    }

    public RPGEntity getEntity() {
        return entity;
    }
}
