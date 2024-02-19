package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.mob.custom.CustomEntityGelatinousCube;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GelatinousCube extends EntityMob {

    @Override
    public Entity createBaseEntity(World world, Location location) {
        CustomEntityGelatinousCube gelatinousCube = new CustomEntityGelatinousCube(((CraftWorld) world).getHandle());
        gelatinousCube.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        ((CraftWorld)world).getHandle().addEntity(gelatinousCube, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return gelatinousCube.getBukkitEntity();
    }

    @Override
    public String getName() {
        return "Gelatinous Cube";
    }

    @Override
    public int getMaxHealth() {
        return 200;
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        if(event.getEntity() == this.baseEntity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if(event.getEntity() == this.baseEntity) {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onKnockback(EntityDamageByEntityEvent event) {
        if(event.getEntity() == this.baseEntity) {
            Vector lastVel = event.getEntity().getVelocity();
            Bukkit.getScheduler().runTaskLater(mobManager.getPlugin(), () -> this.baseEntity.setVelocity(lastVel), 1L);
        }
    }
}
