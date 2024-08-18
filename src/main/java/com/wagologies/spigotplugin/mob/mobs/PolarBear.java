package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.custom.EntityPolarBear;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class PolarBear extends EntityMob {
    public PolarBear(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(20, 10, 16, 2, 13, 7);
    }

    @Override
    public int getMaxHealth() {
        return 420;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        return null;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[4];
    }

    @Override
    public MobType getType() {
        return MobType.POLAR_BEAR;
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        EntityPolarBear polarBear = new EntityPolarBear(plugin, craftWorld.getHandle());
        polarBear.setPos(location);
        return (org.bukkit.entity.PolarBear) polarBear.getBukkitEntity();
    }
}
