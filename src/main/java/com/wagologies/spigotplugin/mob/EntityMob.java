package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.event.DamageMobByPlayer;
import com.wagologies.spigotplugin.event.DamageMobEvent;
import com.wagologies.spigotplugin.event.SpellHitEntityEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellType;
import com.wagologies.spigotplugin.spell.spells.EldritchBlast;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMob extends AbstractMob {
    protected Entity baseEntity;
    public void spawn(Location location) {
        World world = location.getWorld();
        assert world != null;
        baseEntity = createBaseEntity(world, location);
        updateName();
    }


    public void teleport(Location location) {
        this.baseEntity.teleport(location);
    }

    public void teleport(Entity entity) {
        this.teleport(entity.getLocation());
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.baseEntity.setVelocity(velocity);
    }

    public void updateName() {
        this.baseEntity.setCustomName(getHoverText());
        this.baseEntity.setCustomNameVisible(true);
    }

    public Entity getBaseEntity() {
        return baseEntity;
    }

    public abstract Entity createBaseEntity(World world, Location location);

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!entity.equals(baseEntity)) {
            return;
        }

        DamageMobEvent damageEvent = createDamageEvent(event);

        Bukkit.getPluginManager().callEvent(damageEvent);
        if(damageEvent.isCancelled()) {
            return;
        }

        event.setDamage(0);
        damage(damageEvent.getDamage());
        updateName();
    }

    @EventHandler
    public void onSpellHit(SpellHitEntityEvent event) {
        if(event.getEntity().equals(baseEntity)) {
            BaseSpell spell = event.getSpell();
            if(spell instanceof EldritchBlast blast) {
                if(baseEntity instanceof LivingEntity livingEntity) {
                    livingEntity.playHurtAnimation(0);
                    Sound hurtSound = livingEntity.getHurtSound();
                    if(hurtSound != null) {
                        baseEntity.getWorld().playSound(livingEntity, hurtSound, 1, 1);
                    }
                }
                Vector direction = blast.getDirection().clone().normalize();
                direction.setY(0.4);
                baseEntity.setVelocity(direction);
                damage(5);
                updateName();
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().equals(baseEntity)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
    @Override
    public void onDeath() {
        super.onDeath();
        if(baseEntity instanceof Damageable) {
            ((Damageable) baseEntity).setHealth(0);
        } else {
            baseEntity.remove();
        }
    }

    @Override
    public void remove() {
        super.remove();
        baseEntity.remove();
    }
}