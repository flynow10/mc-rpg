package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.custom.EntityGelatinousCube;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

public class GelatinousCube extends EntityMob {

    public GelatinousCube(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(14, 3, 20, 1, 6, 1);
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        EntityGelatinousCube gelatinousCube = new EntityGelatinousCube(plugin, craftWorld.getHandle());
        gelatinousCube.setPos(location);
        Slime slime = (Slime) gelatinousCube.getBukkitEntity();
        slime.setSize(7);
        return slime;
    }
    @Override
    public int getMaxHealth() {
        return 840;
    }

    @Override
    public RPGItem getHeldItem() {
        MeleeWeapon pseudopod = new MeleeWeapon(plugin, new ItemStack(Material.SLIME_BLOCK));
        pseudopod.setDamageType(DamageSource.DamageType.ACID);
        pseudopod.setBaseDamage(10);
        return pseudopod;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[4];
    }

    @Override
    public int getArmorClass() {
        return 6;
    }

    @Override
    public int getWeight() {
        return 40;
    }

    @Override
    public MobType getType() {
        return MobType.GELATINOUS_CUBE;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_SLIME_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_SLIME_HURT;
    }
}
