package com.wagologies.spigotplugin.entity;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.AbstractMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private final SpigotPlugin plugin;
    private final List<RPGEntity> entities = new ArrayList<>();

    public EntityManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 1);
        plugin.getLogger().info("Loaded Entity Manager");
    }

    private void tick() {
        for (RPGEntity entity : entities) {
            if(!entity.isRemoved()) {
                entity.tick();
            }
        }
    }

    public RPGEntity spawn(MobType mobType, Location location) {
        return this.spawn(mobType.getMobClass(), location);
    }

    public RPGEntity spawn(Class<? extends AbstractMob> mobClass, Location location) {
        try {
            Constructor<? extends AbstractMob> constructor = mobClass.getDeclaredConstructor(SpigotPlugin.class);
            AbstractMob instantiatedMob = constructor.newInstance(plugin);
            return spawn(instantiatedMob, location);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public RPGEntity spawn(AbstractMob mob, Location location) {
        mob.spawn(location);
        entities.add(mob);
        return mob;
    }

    public void removeEntity(RPGEntity entity) {
        entities.remove(entity);
    }

    public void addPlayer(RPGPlayer player) {
        entities.add(player);
    }

    public void removePlayer(RPGPlayer player) {
        entities.remove(player);
    }

    public List<RPGEntity> getEntities() {
        return entities;
    }

    @Nullable
    public RPGEntity getEntity(Entity entity) {
        for (RPGEntity rpgEntity : entities) {
            if(rpgEntity.isEntityPart(entity)) {
                return rpgEntity;
            }
        }
        return null;
    }
}
