package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.custom.EntityNoClimbCaveSpider;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class SplitSpider extends EntityMob {
    public SplitSpider(SpigotPlugin plugin) {
        super(plugin);
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        EntityNoClimbCaveSpider noClimbSpider = new EntityNoClimbCaveSpider(plugin, craftWorld.getHandle());
        noClimbSpider.setPos(location);
        return (LivingEntity) noClimbSpider.getBukkitEntity();
    }

    @Override
    public int getMaxHealth() {
        return 30;
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
        return MobType.SPLIT_SPIDER;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_SPIDER_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_SPIDER_HURT;
    }
}
