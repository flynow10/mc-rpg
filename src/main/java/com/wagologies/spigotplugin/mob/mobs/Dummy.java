package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;

import javax.annotation.Nullable;

public class Dummy extends EntityMob {
    public Dummy(SpigotPlugin plugin) {
        super(plugin);
    }

    @Override
    public int getMaxHealth() {
        return 1000000;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        return null;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[0];
    }

    @Override
    public int getWeight() {
        return 4000;
    }

    @Override
    public MobType getType() {
        return MobType.DUMMY;
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        Zombie zombie = world.createEntity(location, Zombie.class);
        zombie.setAI(false);
        return zombie;
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if(event.getEntity().equals(this.entity)) {
            event.setCancelled(true);
        }
    }
}
