package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.custom.EntityAttackWolf;
import com.wagologies.spigotplugin.utils.LocationHelper;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class DireWolf extends EntityMob {
    public static final int REINFORCEMENT_COUNT = 8;
    boolean hasSpawnedReinforcements = false;

    public DireWolf(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(17, 15, 15, 12, 3, 7);
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        EntityAttackWolf attackWolf = new EntityAttackWolf(plugin, craftWorld.getHandle());
        attackWolf.setPos(location);
        return (Wolf) attackWolf.getBukkitEntity();
    }

    @Override
    public int getMaxHealth() {
        return 370;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        MeleeWeapon teeth = new MeleeWeapon(plugin, new ItemStack(Material.GHAST_TEAR));
        teeth.setDamageType(DamageSource.DamageType.PIERCING);
        teeth.setBaseDamage(10);
        return teeth;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[4];
    }

    @Override
    public int getArmorClass() {
        return 14;
    }

    @Override
    public void damage(DamageSource damageSource, int damage) {
        super.damage(damageSource, damage);
        if(!isDeadOrDying() && getHealth() < 0.5 * getMaxHealth() && !hasSpawnedReinforcements) {
            callReinforcements();
        }
    }

    public void callReinforcements() {
        hasSpawnedReinforcements = true;
        Location location = entity.getLocation();
        World world = location.getWorld();
        assert world != null;
        world.playSound(location, Sound.ENTITY_WOLF_HOWL, 10, 1);
        List<Location> nearbySpawns = LocationHelper.getNearbySpawnLocations(location, 5, 1, 2);
        Collections.shuffle(nearbySpawns);
        for (int i = 0; i < Math.min(REINFORCEMENT_COUNT, nearbySpawns.size()); i++) {
            Location spawnLoc = nearbySpawns.get(i);
            plugin.getEntityManager().spawn(MobType.WOLF, spawnLoc);
        }
    }

    @Override
    public MobType getType() {
        return MobType.DIRE_WOLF;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_WOLF_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_WOLF_HURT;
    }

    @Override
    public float getVoicePitch() {
        return 0.4f;
    }
}
