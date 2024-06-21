package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Darkness extends BaseSpell {
    private final List<LivingEntity> affectedEntities = new ArrayList<>();
    private final Vector origin;
    public Darkness(SpellManager spellManager, RPGEntity rpgEntity) {
        super(spellManager, rpgEntity);
        origin = rpgEntity.getLocation().toVector();
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount >= 200) {
            endSpell();
            return;
        }
        float radius = 5;
        updateEntitiesList(radius);
        blindAffectedEntities();

        if(tickCount % 4 != 0) {
            return;
        }
        spawnParticleSphere(radius);
    }

    @Override
    public void endSpell() {
        super.endSpell();
        for (LivingEntity affectedEntity : affectedEntities) {
            affectedEntity.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    private void updateEntitiesList(float radius) {
        World spellWorld = getSpellWorld();
        Location originLocation = origin.toLocation(spellWorld);
        Collection<Entity> entities = spellWorld.getNearbyEntities(originLocation, radius, radius, radius);
        for (Entity entity : entities) {
            if(!(entity instanceof LivingEntity)) {
                continue;
            }
            if(!affectedEntities.contains(entity) && entity.getLocation().distanceSquared(originLocation) < radius * radius) {
                affectedEntities.add((LivingEntity) entity);
            }
        }
        for (LivingEntity affectedEntity : new ArrayList<>(affectedEntities)) {
            if(affectedEntity.getLocation().distanceSquared(originLocation) >= radius * radius) {
                affectedEntity.removePotionEffect(PotionEffectType.BLINDNESS);
                affectedEntities.remove(affectedEntity);
            }
        }
    }

    private void blindAffectedEntities() {
        for (LivingEntity affectedEntity : affectedEntities) {
            if(!affectedEntity.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                affectedEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, -1, 10, false, false));
            }
        }
    }

    private void spawnParticleSphere(float radius) {
        int diskCount = 17;
        int maxCirclesPerDisk = 5;
        int maxPointsPerCircle = 15;
        World spellWorld = getSpellWorld();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLACK, 4f);
        for(double i = 0; i <= Math.PI; i += Math.PI / diskCount) {
            double iRadius = Math.sin(i) * radius;
            double y = Math.cos(i) * radius;
            if(iRadius != 0) {
                int circlesPerDisk = Math.max(1,(int)(maxCirclesPerDisk * (iRadius/radius)));
                for(double dRadius = 0; dRadius <= iRadius; dRadius += (iRadius)/circlesPerDisk) {
                    int pointsPerCircle = Math.max(1,(int) (maxPointsPerCircle * Math.pow(dRadius/iRadius,2)));
                    for(double a = 0; a < Math.PI * 2; a+= Math.PI / pointsPerCircle) {
                        double x = Math.cos(a + i + dRadius) * dRadius;
                        double z = Math.sin(a + i + dRadius) * dRadius;
                        Location particleLoc = origin.clone().add(new Vector(x, y, z)).toLocation(spellWorld);
                        spellWorld.spawnParticle(Particle.REDSTONE, particleLoc, 1, dustOptions);
                    }
                }
            }
        }
    }
}
