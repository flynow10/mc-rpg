package com.wagologies.spigotplugin.spell;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SpellCaster {
    Location getLocation();
    Location getEyeLocation();
    Entity getCastingEntity();
}
