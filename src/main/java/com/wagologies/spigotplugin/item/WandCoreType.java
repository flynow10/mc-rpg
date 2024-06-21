package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.utils.ItemHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public enum WandCoreType {
    INERT("No Core", 0, Material.DEAD_BUSH),
    ENCHANTED_STRING("Enchanted String", 1, Material.STRING);


    private final String name;
    private final int spellSlots;
    private final Material itemMaterial;
    WandCoreType(String name, int spellSlots, Material itemMaterial) {
        this.name = name;
        this.spellSlots = spellSlots;
        this.itemMaterial = itemMaterial;
    }

    public String getName() {
        return name;
    }

    public int getSpellSlots() {
        return spellSlots;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public WandCore getItem(SpigotPlugin plugin) {
        ItemStack itemStack = new ItemStack(getItemMaterial());
        return new WandCore(plugin, itemStack, this);
    }

    public static WandCoreType fromString(@Nullable String coreType) {
        if(coreType == null) {
            return WandCoreType.INERT;
        } else {
            try {
                return WandCoreType.valueOf(coreType);
            } catch (IllegalArgumentException e) {
                return WandCoreType.INERT;
            }
        }
    }

}
