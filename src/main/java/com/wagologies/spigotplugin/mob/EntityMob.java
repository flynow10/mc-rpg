package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.RPGEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.util.BoundingBox;

import java.util.Arrays;

public abstract class EntityMob extends AbstractMob implements Listener {
    protected LivingEntity entity;

    public EntityMob(SpigotPlugin plugin) {
        super(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public abstract LivingEntity createEntity(World world, Location location);

    @Override
    public void spawn(Location location) {
        World world = location.getWorld();
        if(world == null) {
            throw new IllegalArgumentException("Cannot spawn entity because a world was not provided!");
        }
        LivingEntity entity = createEntity(world, location);
        this.entity = entity;
        entity.setRemoveWhenFarAway(false);
        world.addEntity(entity);
        updateName();
    }

    public void updateName() {
        this.entity.setCustomName(getDisplayName());
        this.entity.setCustomNameVisible(true);
    }

    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    @Override
    public World getWorld() {
        return entity.getWorld();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return entity.getBoundingBox();
    }

    @Override
    public Entity getMainEntity() {
        return entity;
    }

    @Override
    public void remove(boolean isDead) {
        super.remove(isDead);
        if(isDead) {
            entity.setHealth(0);
        } else {
            entity.remove();
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    public void playHurtAnimation() {
        entity.playHurtAnimation(0);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().equals(entity)) {
            event.setCancelled(true);
            onNaturalDamage(event);
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager().equals(entity)) {
            event.setCancelled(true);
            RPGEntity rpgEntity = plugin.getEntityManager().getEntity(event.getEntity());
            if(rpgEntity != null) {
                doDamageTarget(rpgEntity);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().equals(entity)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }
}