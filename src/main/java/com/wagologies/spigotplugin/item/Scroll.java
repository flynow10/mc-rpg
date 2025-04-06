package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.spell.SpellType;
import com.wagologies.spigotplugin.utils.ItemHelper;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Scroll extends RPGItem {
    public static final String SpellTypeKey = "scroll-spell-type";
    private SpellType spellType;

    public Scroll(SpigotPlugin plugin, ItemStack itemStack) {
        super(plugin, itemStack);
        setItemType(ItemType.SCROLL);
        loadSpellType();
        updateItemDisplay();
    }

    private void updateItemDisplay() {
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        List<String> lore = spellType.getLoreText(true);
        lore.addFirst("");
        lore.addFirst(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Right Click" + ChatColor.RESET + ChatColor.YELLOW + " to Learn Spell");
        meta.setLore(StringHelper.prependWithReset(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        ItemHelper.AddGlow(itemStack);
    }

    private void loadSpellType() {
        String spellTypeString = getPersistentData(SpellTypeKey, PersistentDataType.STRING);
        SpellType spellType = SpellType.fromString(spellTypeString);
        if(spellType == null) {
            spellType = SpellType.AuraOfVitality;
        }
        setSpellType(spellType);
    }

    public void setSpellType(SpellType spellType) {
        this.spellType = spellType;
        setPersistentData(SpellTypeKey, PersistentDataType.STRING, spellType.toString());
        updateItemDisplay();
    }

    public SpellType getSpellType() {
        return this.spellType;
    }
}
