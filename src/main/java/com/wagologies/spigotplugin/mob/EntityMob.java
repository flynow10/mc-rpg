package com.wagologies.spigotplugin.mob;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class EntityMob extends AbstractMob {
    protected Entity baseEntity;
    private ArmorStand armorStand;
    public void spawn(Location location) {
        baseEntity = createBaseEntity(location.getWorld(), location);
        armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        Slime slimePositioner = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slimePositioner.setSize(-1);
        slimePositioner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false), true);
        slimePositioner.setPassenger(armorStand);
        baseEntity.setPassenger(slimePositioner);
        updateName();
    }


    public void teleport(Location location) {
        Entity nameTag = this.baseEntity.getPassenger();
        this.baseEntity.eject();
        this.baseEntity.teleport(location);
        this.baseEntity.setPassenger(nameTag);
    }

    public void teleport(Entity entity) {
        this.teleport(entity.getLocation());
    }

    public void updateName() {
        this.armorStand.setCustomName(getHoverText());
    }

    public abstract Entity createBaseEntity(World world, Location location);

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        Entity passenger = baseEntity.getPassenger();
        while(passenger != null) {
            if(entity.equals(passenger)) {
                event.setCancelled(true);
                return;
            }
            passenger = passenger.getPassenger();
        }

        if (entity.equals(baseEntity)) {
            event.setDamage(0);
            damage(10);
            updateName();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().equals(baseEntity)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    private void removeNameTag() {
        Entity passenger = baseEntity.getPassenger();
        while(passenger != null) {
            Entity next = passenger.getPassenger();
            passenger.remove();
            passenger = next;
        }
    }
    @Override
    public void onDeath() {
        super.onDeath();
        removeNameTag();
        if(baseEntity instanceof Damageable) {
            ((Damageable) baseEntity).setHealth(0);
        } else {
            baseEntity.remove();
        }
    }

    @Override
    public void remove() {
        super.remove();
        removeNameTag();
        baseEntity.remove();
    }
}