package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.RPGEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public abstract class AbstractMob extends RPGEntity {
    public AbstractMob(SpigotPlugin plugin) {
        super(plugin);
    }

    public abstract void spawn(Location location);
    public abstract MobType getType();

    public abstract void updateName();

    public String getDisplayName() {
        return ChatColor.RED + getName() + ChatColor.GRAY + " [" + ChatColor.DARK_RED + getHealth() + "/" + getMaxHealth() + ChatColor.GRAY + "]";
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    @Override
    public void setHealth(int newHealth) {
        super.setHealth(newHealth);
        updateName();
    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }
}
