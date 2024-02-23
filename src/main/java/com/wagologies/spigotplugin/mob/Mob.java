package com.wagologies.spigotplugin.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface Mob {
    int getMaxHealth();
    int getHealth();
    void spawn(Location location);
    void teleport(Location location);
    void teleport(Entity entity);
    void setVelocity(Vector velocity);
    boolean damage(int damage);
    String getName();
    void die();
    void remove();
    MobManager getMobManager();
    void setMobManager(MobManager mobManager);
}

