package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.entity.EntityManager;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MagicMissile extends BaseSpell {
    private static final int BOLTS = 3;
    private final List<Bolt> bolts = new ArrayList<>();

    public MagicMissile(SpellManager spellManager, RPGEntity spellCaster) {
        super(spellManager, spellCaster);
        Vector direction = spellCaster.getEyeLocation().getDirection().normalize();
        Location spawnLocation = spellCaster.getEyeLocation().add(direction.multiply(2));
        Vector perpendicular = direction.getCrossProduct(new Vector(0, 1, 0)).normalize().multiply(0.7);
        spawnLocation.subtract(perpendicular.clone().multiply((double) (BOLTS-1) / 2));

        for (int i = 0; i < BOLTS; i++) {
            Vector velocity = spawnLocation.toVector().subtract(spellCaster.getEyeLocation().toVector()).normalize();
            Bolt bolt = new Bolt(this, spawnLocation.clone(), velocity);
            bolts.add(bolt);
            spawnLocation.add(perpendicular);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 100) {
            endSpell();
        }

        for (Bolt bolt : new ArrayList<>(bolts)) {
            bolt.update();
            bolt.display();
        }
    }

    static class Bolt {
        private static final int MAX_RANGE = 40;
        private static final double BOLT_SPEED = 0.6;
        private final MagicMissile spell;
        private Location oldPosition;
        private final Location position;
        private final Vector velocity;
        private Vector acceleration = new Vector(0,0,0);
        private RPGEntity target;
        private boolean isDestroyed = false;
        public Bolt(MagicMissile spell, Location position, Vector velocity) {
            this.spell = spell;
            this.position = position;
            this.velocity = velocity.multiply(BOLT_SPEED);
            this.target = findTarget(position.toVector(), velocity.clone().normalize());
            playLaunchSound();
        }

        private RPGEntity findTarget(Vector rayOrigin, Vector rayDir) {
            EntityManager entityManager = spell.spellManager.getPlugin().getEntityManager();
            double minDistance = Double.MAX_VALUE;
            RPGEntity closestEntity = null;
            for (RPGEntity entity : entityManager.getEntities()) {
                if(entity.isInvulnerable() || !spell.spellCaster.canTarget(entity) || !spell.spellCaster.getWorld().equals(entity.getWorld())) {
                   continue;
                }
                Vector entityPos = entity.getLocation().toVector();
                Vector pointOnRay = getClosestPointOnRay(entityPos, rayOrigin, rayDir);
                if(pointOnRay.equals(rayOrigin)) {
                    // don't select entities behind the caster
                    continue;
                }
                double distanceToBolt = rayOrigin.distance(pointOnRay);
                if(distanceToBolt > MAX_RANGE) {
                    continue;
                }
                double distanceToRay = pointOnRay.distance(entityPos);
                if(distanceToRay < minDistance) {
                    minDistance = distanceToRay;
                    closestEntity = entity;
                }
            }
            return closestEntity;
        }

        private Vector getClosestPointOnRay(Vector point, Vector rayOrigin, Vector rayDir) {
            Vector v = point.clone().subtract(rayOrigin);
            double dotProduct = v.dot(rayDir);
            if(dotProduct < 0) {
                // point is behind ray
                return rayOrigin;
            }
            return rayOrigin.clone().add(rayDir.clone().multiply(dotProduct));
        }


        public void update() {
            oldPosition = this.position.clone();
            this.position.add(velocity).add(acceleration.clone().multiply(0.5));

            Vector newAcceleration = new Vector(0,0,0);

            Vector velocityDir = velocity.clone().normalize();
            double dragCoeff = 5;
            double dragAccMag = 0.5 * dragCoeff * velocity.lengthSquared();
            Vector dragAcc = velocityDir.clone().multiply(-dragAccMag);
            double thrustCoeff = BOLT_SPEED*dragCoeff/2;
            Vector thrustAcc = velocity.clone().multiply(thrustCoeff);

            newAcceleration.add(dragAcc).add(thrustAcc);

            if(target == null) {
                target = findTarget(this.position.toVector(), this.velocity.clone().normalize());
            } else {
                if(target.isDead()) {
                    target = null;
                } else {
                    Vector toTarget = target.getBoundingBox().getCenter().subtract(position.toVector());
                    double distance = toTarget.length();
                    Vector centripetalAcc = toTarget.clone().multiply(1/distance).multiply(velocity.lengthSquared()*2/distance);
                    newAcceleration.add(centripetalAcc);
                }
            }

            this.velocity.add(acceleration.clone().add(newAcceleration).multiply(0.5));
            double newVelMagnitude = this.velocity.length();
            if(newVelMagnitude > BOLT_SPEED * 2) {
                this.velocity.multiply(BOLT_SPEED * 2/newVelMagnitude);
            }
            this.acceleration = newAcceleration;

            if(target != null) {
                BoundingBox boundingBox = target.getBoundingBox();
                if(boundingBox.contains(this.position.toVector()) || boundingBox.contains(oldPosition.add(this.position).multiply(0.5).toVector())) {
                    spell.getSpellWorld().spawnParticle(Particle.EXPLOSION_LARGE, position, 1);
                    target.damage(new DamageSource(DamageSource.DamageType.BLUNT, true), 20);
                    destroy();
                }
            }
            if(!this.position.getBlock().isEmpty()) {
                destroy();
            }
        }

        public void display() {
            if(!isDestroyed) {
                displayParticle(position);
                displayParticle(position.clone().add(oldPosition).multiply(0.5));
            }
        }

        private void displayParticle(Location location) {
            Particle.DustTransition options = new Particle.DustTransition(Color.fromRGB(0xc36cf5), Color.fromRGB(0xefcee0), 1f);
            spell.getSpellWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location.clone(), 20, 0.02, 0.02, 0.02, 0, options, true);
            Particle.DustOptions sparkOptions = new Particle.DustOptions(Color.fromRGB(0xdf6ee0), 0.3f);
            spell.getSpellWorld().spawnParticle(Particle.REDSTONE, location.clone(), 8, 0.2, 0.2, 0.2, 0, sparkOptions, true);
        }

        private void playLaunchSound() {
            spell.getSpellWorld().playSound(this.position, Sound.ENTITY_ARROW_SHOOT, 1f, 0.7f);
        }

        public void destroy() {
            this.isDestroyed = true;
            this.spell.bolts.remove(this);
        }

        public RPGEntity getTarget() {
            return target;
        }
    }
}
