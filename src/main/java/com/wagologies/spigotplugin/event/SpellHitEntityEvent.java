package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.spell.BaseSpell;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class SpellHitEntityEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final BaseSpell spell;
    private boolean cancelled;
    public SpellHitEntityEvent(BaseSpell spell, Entity entity) {
        super(entity);
        this.spell = spell;
    }

    public BaseSpell getSpell() {
        return spell;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
