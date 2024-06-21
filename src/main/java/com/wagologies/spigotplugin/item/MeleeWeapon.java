package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class MeleeWeapon extends RPGItem {
    private static final String BaseDamageKey = "sword-base-damage";
    private static final String DamageTypeKey = "sword-damage-type";
    private int baseDamage;
    private DamageSource.DamageType damageType = DamageSource.DamageType.SLICING;
    public MeleeWeapon(SpigotPlugin plugin, ItemStack itemStack) {
        super(plugin, itemStack);
        setItemType(ItemType.MELEE_WEAPON);
        loadBaseDamage();
        loadDamageType();
        updateItemDisplay();
    }

    private void updateItemDisplay() {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("attack-speed", 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.setLore(List.of(ChatColor.RESET.toString() + ChatColor.GRAY + "Damage: " + ChatColor.RED + getBaseDamage()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
    }

    private void loadBaseDamage() {
        Integer damage = getPersistentData(BaseDamageKey, PersistentDataType.INTEGER);
        setBaseDamage(Objects.requireNonNullElse(damage, 1));
    }

    public void setBaseDamage(int damage) {
        baseDamage = damage;
        setPersistentData(BaseDamageKey, PersistentDataType.INTEGER, baseDamage);
        updateItemDisplay();
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    private void loadDamageType() {
        String damageTypeString = getPersistentData(DamageTypeKey, PersistentDataType.STRING);
        DamageSource.DamageType damageType = DamageSource.DamageType.fromString(damageTypeString);
        if(damageType == null) {
            damageType = DamageSource.DamageType.SLICING;
        }
        setDamageType(damageType);
    }

    public void setDamageType(DamageSource.DamageType damageType) {
        this.damageType = damageType;
        setPersistentData(DamageTypeKey, PersistentDataType.STRING, damageType.toString());
    }

    public DamageSource.DamageType getDamageType() {
        return damageType;
    }
}
