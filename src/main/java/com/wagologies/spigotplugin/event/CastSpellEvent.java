package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.spell.SpellType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class CastSpellEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    final RPGEntity spellcaster;
    final SpellType spellType;
    private boolean canceled = false;

    public CastSpellEvent(RPGEntity spellcaster, SpellType spellType) {
        this.spellcaster = spellcaster;
        this.spellType = spellType;
    }

    public RPGEntity getSpellcaster() {
        return spellcaster;
    }

    public SpellType getSpellType() {
        return spellType;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
