package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.spell.SpellType;
import com.wagologies.spigotplugin.utils.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Wand extends CustomItem {
    private static final String CoreTypeKey = "wand-type";
    private static final String LoadedSpellKey = "wand-loaded-spell";
    private WandCoreType coreType;
    private SpellType loadedSpell = null;
    public Wand(SpigotPlugin plugin, ItemStack item) {
        super(plugin, item);
        setItemType(ItemType.WAND);
        loadCoreType();
        loadLoadedSpell();
        updateItemDisplay();
    }

    public void updateItemDisplay() {
        if (isSpellLoaded()) {
            itemStack.setType(Material.BLAZE_ROD);
        } else {
            itemStack.setType(Material.STICK);
        }
        ItemHelper.AddGlow(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.LIGHT_PURPLE + "Wand");
        meta.setLore(List.of(ChatColor.RESET.toString() + ChatColor.GRAY + coreType.getName(), "", ChatColor.RESET.toString() + ChatColor.GRAY + "Charged Spell: " + ChatColor.LIGHT_PURPLE + (isSpellLoaded() ? loadedSpell.getName() : "None")));
        itemStack.setItemMeta(meta);
    }

    private void loadCoreType() {
        String coreData = getPersistentData(CoreTypeKey, PersistentDataType.STRING);
        setCoreType(WandCoreType.fromString(coreData));
    }

    private void loadLoadedSpell() {
        String coreData = getPersistentData(LoadedSpellKey, PersistentDataType.STRING);
        setLoadedSpell(SpellType.fromString(coreData));
    }

    public void setCoreType(WandCoreType coreType) {
        this.coreType = coreType;
        setPersistentData(CoreTypeKey, PersistentDataType.STRING, coreType.toString());
        updateItemDisplay();
    }

    public void setLoadedSpell(SpellType type) {
        loadedSpell = type;
        if(type == null) {
            clearPersistentData(LoadedSpellKey);
        } else {
            setPersistentData(LoadedSpellKey, PersistentDataType.STRING, loadedSpell.toString());
        }
        updateItemDisplay();
    }

    public SpellType getLoadedSpell() {
        return loadedSpell;
    }

    public boolean isSpellLoaded() {
        return loadedSpell != null;
    }

    public WandCoreType getCoreType() {
        return coreType;
    }
}
