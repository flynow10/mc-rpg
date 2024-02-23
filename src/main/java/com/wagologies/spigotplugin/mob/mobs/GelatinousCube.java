package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.custom.CustomEntityGelatinousCube;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.util.Vector;

public class GelatinousCube extends EntityMob {

    @Override
    public Entity createBaseEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        CustomEntityGelatinousCube gelatinousCube = new CustomEntityGelatinousCube(EntityTypes.aM, craftWorld.getHandle());
        gelatinousCube.setPos(location);
        craftWorld.getHandle().addFreshEntity(gelatinousCube, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Slime slime = (Slime) gelatinousCube.getBukkitEntity();
        slime.setSize(7);
        return slime;
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
