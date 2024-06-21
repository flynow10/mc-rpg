package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.particle.CircleEffect;
import com.wagologies.spigotplugin.particle.Particle;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GustOfWind extends BaseSpell {
    public Location origin;
    public Vector direction;
    public GustOfWind(SpellManager spellManager, RPGEntity spellCaster) {
        super(spellManager, spellCaster);
        Location eyeLine = spellCaster.getEyeLocation();
        direction = eyeLine.getDirection().normalize();
        origin = eyeLine.add(direction.clone().multiply(2));
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 30) {
            endSpell();
            return;
        }
        Particle<Vector> windParticle = new Particle<>(org.bukkit.Particle.CLOUD, 0, direction.clone(), null);
        CircleEffect circleEffect = new CircleEffect(spellManager.getPlugin(), 0.7, direction, 20);
        circleEffect.draw(windParticle, origin);

        if(tickCount % 4 == 0) {
            getSpellWorld().playSound(origin, Sound.ENTITY_HORSE_BREATHE, 3f, 0.3f);
        }

        Collection<Entity> nearbyEntities = getSpellWorld().getNearbyEntities(origin, 15, 15, 15);
        List<RPGEntity> searchedEntities = new ArrayList<>();
        for (Entity nearbyEntity : nearbyEntities) {
            RPGEntity rpgEntity = findEntity(nearbyEntity);
            if(rpgEntity != null && !searchedEntities.contains(rpgEntity)) {
                searchedEntities.add(rpgEntity);
                RayTraceResult result = rpgEntity.getBoundingBox().rayTrace(origin.toVector(), direction, 15);
                if(result != null) {
                    onHitEntity(rpgEntity);
                }
            }
        }
    }

    public void onHitEntity(RPGEntity rpgEntity) {
        Entity entity = rpgEntity.getMainEntity();
        double distance = rpgEntity.getLocation().distance(origin);
        Vector direction = this.direction.clone().normalize();
        double speedDampener = Math.max(0, 1 - entity.getVelocity().lengthSquared());
        double distanceDampener = (1 - distance/20);
        entity.setVelocity(entity.getVelocity().add(direction.multiply(distanceDampener).multiply(speedDampener)));
    }
}
