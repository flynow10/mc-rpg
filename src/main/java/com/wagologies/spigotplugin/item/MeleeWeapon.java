package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class MeleeWeapon extends RPGItem {
    private static final String BaseDamageKey = "sword-base-damage";
    private static final String DamageTypeKey = "sword-damage-type";
    private static final String AttackSpeedKey = "sword-attack-speed";
    private int baseDamage;
    private DamageSource.DamageType damageType = DamageSource.DamageType.SLICING;
    private AttackSpeed attackSpeed = AttackSpeed.NORMAL;

    public MeleeWeapon(SpigotPlugin plugin, ItemStack itemStack) {
        super(plugin, itemStack);
        setItemType(ItemType.MELEE_WEAPON);
        loadBaseDamage();
        loadDamageType();
        loadAttackSpeed();
        updateItemDisplay();
    }

    private void updateItemDisplay() {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("attack-speed", attackSpeed.getValue(), attackSpeed.getOperation()));
        meta.setLore(StringHelper.prependWithReset(List.of(
                ChatColor.DARK_GRAY + "Damage: " + ChatColor.RED + getBaseDamage(),
                ChatColor.DARK_GRAY + "Damage Type: " + ChatColor.GRAY + StringHelper.enumToHumanName(getDamageType()),
                ChatColor.DARK_GRAY + "Attack Speed: " + ChatColor.GRAY + StringHelper.enumToHumanName(getAttackSpeed())
        )));
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

    public void loadAttackSpeed() {
        String attackSpeedString = getPersistentData(AttackSpeedKey, PersistentDataType.STRING);
        AttackSpeed attackSpeed = AttackSpeed.fromString(attackSpeedString);
        if(attackSpeed == null) {
            attackSpeed = AttackSpeed.NORMAL;
        }
        setAttackSpeed(attackSpeed);
    }

    public void setAttackSpeed(AttackSpeed attackSpeed) {
        this.attackSpeed = attackSpeed;
        setPersistentData(AttackSpeedKey, PersistentDataType.STRING, attackSpeed.toString());
        updateItemDisplay();
    }

    public AttackSpeed getAttackSpeed() {
        return attackSpeed;
    }

    public enum AttackSpeed {
        SLOW(0),
        NORMAL(0),
        FAST(0),
        VERY_FAST(0);

        private double value;
        private AttributeModifier.Operation operation;

        AttackSpeed(double value) {
            this(value, AttributeModifier.Operation.ADD_NUMBER);
        }

        AttackSpeed(double value, AttributeModifier.Operation operation) {
            this.value = value;
            this.operation = operation;
        }

        public double getValue() {
            return value;
        }

        public AttributeModifier.Operation getOperation() {
            return operation;
        }

        public static AttackSpeed fromString(@Nullable String attackSpeed) {
            if(attackSpeed == null) {
                return null;
            } else {
                try {
                    return AttackSpeed.valueOf(attackSpeed);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
    }
}
