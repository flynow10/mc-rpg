package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.item.Wand;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SpellManager {
    private SpigotPlugin plugin;
    private final List<BaseSpell> activeSpells = new ArrayList<>();

    public SpellManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loaded Spell Manager");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::tick, 0, 1);
    }

    protected void tick() {
        // Clone to prevent concurrent modification
        for (BaseSpell activeSpell : new ArrayList<>(activeSpells)) {
            activeSpell.tick();
        }
    }

    public BaseSpell castSpell(RPGEntity spellCaster, SpellType spellType) {
        try {
            Constructor<? extends BaseSpell> spellConstructor = spellType.getSpellClass().getDeclaredConstructor(SpellManager.class, RPGEntity.class);
            BaseSpell spell = spellConstructor.newInstance(this, spellCaster);
            activeSpells.add(spell);
            return spell;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BaseSpell> getActiveSpells() {
        return activeSpells;
    }
    public void cancelEntitySpells(RPGEntity entity) {
        for (BaseSpell activeSpell : new ArrayList<>(activeSpells)) {
            if(activeSpell.getSpellCaster().equals(entity)) {
                activeSpell.endSpell();
            }
        }
    }
    public SpigotPlugin getPlugin() {
        return plugin;
    }

    protected void removeSpell(BaseSpell spell) {
        activeSpells.remove(spell);
    }
}
