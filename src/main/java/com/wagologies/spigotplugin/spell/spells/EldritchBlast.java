package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.event.SpellHitEntityEvent;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

public class EldritchBlast extends BaseSpell {
    // Speed in blocks per tick
    private static final float BoltSpeed = 1.5f;
    private final Vector direction;
    private final Vector position;
    private final Random damageRandomizer = new Random();
    public EldritchBlast(SpellManager spellManager, RPGEntity rpgEntity) {
        super(spellManager, rpgEntity);
        Location eyeLine = rpgEntity.getEyeLocation();
        direction = eyeLine.getDirection().normalize();
        position = eyeLine.toVector().add(direction.clone().multiply(2));
        getSpellWorld().playSound(position.toLocation(getSpellWorld()), Sound.ENTITY_BREEZE_DEATH, 10 ,1);
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount >= 40) {
            this.endSpell();
            return;
        }
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
                    RPGEntity rpgEntity = findEntity(entity);
                    if(rpgEntity != null) {
                        boolean canHit = onHitEntity(rpgEntity);
                        if(!canHit) {
                            return;
                        }
                    }
                }
            }
            endSpell();
        }
    }

    public boolean onHitEntity(RPGEntity rpgEntity) {
        if(!spellCaster.canTarget(rpgEntity)) {
            return false;
        }
        Vector direction = this.getDirection().clone().normalize();
        Entity entity = rpgEntity.getMainEntity();
        direction.setY(0.4);
        entity.setVelocity(direction);
        int damage = damageRandomizer.nextInt(5,16);
        rpgEntity.damage(new DamageSource(DamageSource.DamageType.FORCE, true, spellCaster, getPosition().toLocation(getSpellWorld())), damage);
        return true;
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
                if(!spellCaster.isEntityPart(entity) && intersectsWith(entity.getBoundingBox(), boltLocation.toVector(), 0.9f)) {
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
