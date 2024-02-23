package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.item.Wand;
import org.bukkit.World;

public abstract class BaseSpell {
    protected SpellManager spellManager;
    protected SpellCaster spellCaster;
    protected boolean isActive = true;
    public BaseSpell(SpellManager spellManager, SpellCaster spellCaster) {
        this.spellManager = spellManager;
        this.spellCaster = spellCaster;
    }

    public abstract void tick();

    public void endSpell() {
        isActive = false;
        spellManager.removeSpell(this);
    }

    public boolean isActive() {
        return isActive;
    }

    public World getSpellWorld() {
        return spellCaster.getCastingEntity().getWorld();
    }
}
