package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.entity.RPGEntity;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public abstract class BaseSpell {
    protected SpellManager spellManager;
    protected final RPGEntity spellCaster;
    protected boolean isActive = true;
    protected int tickCount = 0;
    public BaseSpell(SpellManager spellManager, RPGEntity spellCaster) {
        this.spellManager = spellManager;
        this.spellCaster = spellCaster;
    }

    public void tick() {
        tickCount ++;
    }

    public void endSpell() {
        isActive = false;
        spellManager.removeSpell(this);
    }

    public boolean isActive() {
        return isActive;
    }

    public World getSpellWorld() {
        return spellCaster.getWorld();
    }

    public RPGEntity getSpellCaster() {
        return spellCaster;
    }

    @Nullable
    public RPGEntity findEntity(Entity entity) {
        return spellManager.getPlugin().getEntityManager().getEntity(entity);
    }
}
