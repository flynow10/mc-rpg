package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class Armor extends RPGItem {
    private static final String ArmorClassKey = "armor-armor-class";
    private static final String WeightKey = "armor-weight";
    private int armorClass;
    private int weight;

    public Armor(SpigotPlugin plugin, ItemStack itemStack) {
        super(plugin, itemStack);
        setItemType(ItemType.ARMOR);
        loadArmorClass();
        loadWeight();
        updateItemDisplay();
    }

    private void updateItemDisplay() {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setLore(List.of(
                ChatColor.RESET.toString() + ChatColor.GRAY + "Armor Class: " + ChatColor.GREEN + getArmorClass(),
                ChatColor.RESET.toString() + ChatColor.GRAY + "Weight: " + ChatColor.RED + getWeight()
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
    }

    private void loadArmorClass() {
        Integer armorClass = getPersistentData(ArmorClassKey, PersistentDataType.INTEGER);
        setArmorClass(Objects.requireNonNullElse(armorClass, 1));
    }

    private void loadWeight() {
        Integer weight = getPersistentData(WeightKey, PersistentDataType.INTEGER);
        setWeight(Objects.requireNonNullElse(weight, 1));
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        setPersistentData(WeightKey, PersistentDataType.INTEGER, weight);
        updateItemDisplay();
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(int armorClass) {
        this.armorClass = armorClass;
        setPersistentData(ArmorClassKey, PersistentDataType.INTEGER, armorClass);
        updateItemDisplay();
    }
}
