package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class MeleeWeapon extends CustomItem {
    private static final String BaseDamageKey = "sword-base-damage";
    private int baseDamage;
    public MeleeWeapon(SpigotPlugin plugin, ItemStack itemStack) {
        super(plugin, itemStack);
        setItemType(ItemType.MELEE_WEAPON);
        loadBaseDamage();
        updateItemDisplay();
    }

    private void updateItemDisplay() {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
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
}
