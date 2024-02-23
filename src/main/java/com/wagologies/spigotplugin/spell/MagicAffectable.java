package com.wagologies.spigotplugin.spell;

import org.bukkit.entity.Entity;

public interface MagicAffectable {
    boolean damage(int damage);
    Entity getEntity();
}
