package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.custom.EntityAttackWolf;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class Wolf extends EntityMob {

    public Wolf(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(12, 15,12, 3, 12,6);
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        EntityAttackWolf attackWolf = new EntityAttackWolf(plugin, craftWorld.getHandle());
        attackWolf.setPos(location);
        return (org.bukkit.entity.Wolf) attackWolf.getBukkitEntity();
    }

    @Override
    public int getMaxHealth() {
        return 30;
    }

    @Override
    public RPGItem getHeldItem() {
        MeleeWeapon teeth = new MeleeWeapon(plugin, new ItemStack(Material.GHAST_TEAR));
        teeth.setDamageType(DamageSource.DamageType.PIERCING);
        teeth.setBaseDamage(7);
        return teeth;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[4];
    }

    @Override
    public int getArmorClass() {
        return 13;
    }

    @Override
    public MobType getType() {
        return MobType.WOLF;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_WOLF_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_WOLF_HURT;
    }
}
