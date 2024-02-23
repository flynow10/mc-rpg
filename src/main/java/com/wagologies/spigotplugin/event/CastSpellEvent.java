package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.spell.SpellType;
import com.wagologies.spigotplugin.spell.SpellCaster;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class CastSpellEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    final SpellCaster spellcaster;
    final SpellType spellType;
    private boolean canceled = false;

    public CastSpellEvent(SpellCaster spellcaster, SpellType spellType) {
        this.spellcaster = spellcaster;
        this.spellType = spellType;
    }

    public SpellCaster getSpellcaster() {
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
