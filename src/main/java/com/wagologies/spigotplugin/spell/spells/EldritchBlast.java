package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.event.SpellHitEntityEvent;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.MagicAffectable;
import com.wagologies.spigotplugin.spell.SpellCaster;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class EldritchBlast extends BaseSpell {
    // Speed in blocks per tick
    private static final float BoltSpeed = 1.5f;
    private final Vector direction;
    private final Vector position;
    private int tickCount = 0;
    public EldritchBlast(SpellManager spellManager, SpellCaster spellCaster) {
        super(spellManager, spellCaster);
        Location eyeLine = spellCaster.getEyeLocation();
        direction = eyeLine.getDirection().normalize();
        position = eyeLine.toVector().add(direction.clone().multiply(3));
        getSpellWorld().playSound(position.toLocation(getSpellWorld()), Sound.ENTITY_BREEZE_DEATH, 10 ,1);
    }

    @Override
    public void tick() {
        if(tickCount >= 40) {
            this.endSpell();
            return;
        }
        tickCount ++;
        World spellWorld = getSpellWorld();
        position.add(direction.clone().multiply(BoltSpeed));
        Location boltLocation = position.toLocation(spellWorld);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x52c7de), 2f);
        spellWorld.spawnParticle(Particle.REDSTONE, boltLocation, 10, 0.1, 0.1, 0.1,0, dustOptions, true);
        if(didHitSomething(boltLocation)) {
            Entity entity = hitEntity(boltLocation);
            if(entity != null) {
                SpellHitEntityEvent event = new SpellHitEntityEvent(this, entity);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    MagicAffectable magicAffectedEntity = findMagicAffectedEntity(entity);
                    if(magicAffectedEntity != null) {
                        onHitEntity(magicAffectedEntity);
                    }
                }
            }
            endSpell();
        }
    }

    public void onHitEntity(MagicAffectable magicAffectable) {
        Entity entity = magicAffectable.getEntity();
        if(entity instanceof LivingEntity livingEntity) {
            livingEntity.playHurtAnimation(0);
            Sound hurtSound = livingEntity.getHurtSound();
            if(hurtSound != null) {
                entity.getWorld().playSound(livingEntity, hurtSound, 1, 1);
            }
        }
        Vector direction = this.getDirection().clone().normalize();
        direction.setY(0.4);
        entity.setVelocity(direction);
        magicAffectable.damage(5);
    }

    @Override
    public void endSpell() {
        super.endSpell();
        World spellWorld = getSpellWorld();
        spellWorld.spawnParticle(Particle.SONIC_BOOM, position.toLocation(spellWorld), 3);
    }

    public Vector getDirection() {
        return direction;
    }

    public Vector getPosition() {
        return position;
    }

    public boolean didHitSomething(Location boltLocation) {
        return hitBlock(boltLocation) != null || hitEntity(boltLocation) != null;
    }

    public Block hitBlock(Location boltLocation) {
        Block hitBlock = getSpellWorld().getBlockAt(boltLocation);
        if(!hitBlock.isEmpty()) {
            return hitBlock;
        }
        return null;
    }

    public Entity hitEntity(Location boltLocation) {
        Collection<Entity> nearbyEntities = getSpellWorld().getNearbyEntities(boltLocation, 2.5, 2.5, 2.5);
        if(!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if(!entity.equals(spellCaster.getCastingEntity()) && intersectsWith(entity.getBoundingBox(), boltLocation.toVector(), 0.9f)) {
                    return entity;
                }
            }
        }
        return null;
    }
    public boolean intersectsWith(BoundingBox boundingBox, Vector center, float radius) {
        double dmin = 0;

        Vector bmin = boundingBox.getMin();
        Vector bmax = boundingBox.getMax();

        if (center.getX() < bmin.getX()) {
            dmin += Math.pow(center.getX() - bmin.getX(), 2);
        } else if (center.getX() > bmax.getX()) {
            dmin += Math.pow(center.getX() - bmax.getX(), 2);
        }

        if (center.getY() < bmin.getY()) {
            dmin += Math.pow(center.getY() - bmin.getY(), 2);
        } else if (center.getY() > bmax.getY()) {
            dmin += Math.pow(center.getY() - bmax.getY(), 2);
        }

        if (center.getZ() < bmin.getZ()) {
            dmin += Math.pow(center.getZ() - bmin.getZ(), 2);
        } else if (center.getZ() > bmax.getZ()) {
            dmin += Math.pow(center.getZ() - bmax.getZ(), 2);
        }

        return dmin <= Math.pow(radius, 2);
    }
}
