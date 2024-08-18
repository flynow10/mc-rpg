package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.item.RPGItemBuilder;
import com.wagologies.spigotplugin.item.WandCoreType;
import com.wagologies.spigotplugin.utils.SerializeInventory;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StarterKit {
    public static final StarterKit Barbarian;
    public static final StarterKit Bard;
    public static final StarterKit Rogue;
    public static final StarterKit Wizard;

    public static final StarterKit[] StarterKits;

    static {
        RPGItemBuilder[] barbarianItems = new RPGItemBuilder[] {
                new RPGItemBuilder(Material.IRON_AXE).customType(ItemType.MELEE_WEAPON).name("Greataxe").damage(12).attackSpeed(MeleeWeapon.AttackSpeed.SLOW),
                new RPGItemBuilder(Material.LEATHER_CHESTPLATE).customType(ItemType.ARMOR).name("Patchy Shirt").armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_LEGGINGS).customType(ItemType.ARMOR).name("Patchy Pants").armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_BOOTS).customType(ItemType.ARMOR).name("Light Boots").armorClass(3).weight(2)
        };
        Barbarian = new StarterKit("Barbarian", "A fierce warrior who can enter a battle rage.", barbarianItems, Material.IRON_AXE, ChatColor.RED);
        RPGItemBuilder[] bardItems = new RPGItemBuilder[] {
                new RPGItemBuilder(Material.STICK).customType(ItemType.WAND).name("Light Staff").coreType(WandCoreType.ENCHANTED_STRING),
                new RPGItemBuilder(Material.LEATHER_CHESTPLATE).customType(ItemType.ARMOR).name("Patchy Shirt").armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_LEGGINGS).customType(ItemType.ARMOR).name("Patchy Pants").armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_BOOTS).customType(ItemType.ARMOR).name("Light Boots").armorClass(3).weight(2)
        };
        Bard = new StarterKit("Bard", "An inspiring magician whose power echoes the music of creation.", bardItems, Material.MUSIC_DISC_5, ChatColor.LIGHT_PURPLE);
        RPGItemBuilder[] rogueItems = new RPGItemBuilder[] {
                new RPGItemBuilder(Material.WOODEN_SWORD).customType(ItemType.MELEE_WEAPON).name("Light Dagger").damage(7).attackSpeed(
                        MeleeWeapon.AttackSpeed.FAST),
                new RPGItemBuilder(Material.LEATHER_CHESTPLATE).customType(ItemType.ARMOR).name("Patchy Cowl").color(
                        Color.fromRGB(0x060606)).armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_LEGGINGS).customType(ItemType.ARMOR).name("Dark Pants").color(Color.fromRGB(0x060606)).armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_BOOTS).customType(ItemType.ARMOR).name("Black Boots").color(Color.fromRGB(0x000000)).armorClass(3).weight(2)
        };
        Rogue = new StarterKit("Rogue", "A scoundrel who uses stealth and trickery to overcome obstacles and enemies.", rogueItems, Material.IRON_SWORD, ChatColor.DARK_GRAY);
        RPGItemBuilder[] wizardItems = new RPGItemBuilder[] {
                new RPGItemBuilder(Material.STICK).customType(ItemType.WAND).name("Oak Wand").coreType(WandCoreType.ENCHANTED_STRING),
                new RPGItemBuilder(Material.LEATHER_CHESTPLATE).customType(ItemType.ARMOR).name("Patchy Shirt").armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_LEGGINGS).customType(ItemType.ARMOR).name("Patchy Pants").armorClass(4).weight(2),
                new RPGItemBuilder(Material.LEATHER_BOOTS).customType(ItemType.ARMOR).name("Light Boots").armorClass(3).weight(2)
        };
        Wizard = new StarterKit("Wizard", "A scholarly magic-user capable of manipulating the structures of reality.", wizardItems, Material.BOOK, ChatColor.DARK_AQUA);

        StarterKits = new StarterKit[] {StarterKit.Barbarian, StarterKit.Bard, StarterKit.Rogue, StarterKit.Wizard};
    }

    final String name;
    final String description;
    final RPGItemBuilder[] starterItems;
    final Material displayMaterial;
    final ChatColor displayColor;
    public StarterKit(String name, String description, RPGItemBuilder[] starterItems, Material displayMaterial, ChatColor displayColor) {
        this.name = name;
        this.description = description;
        this.starterItems = starterItems;
        this.displayMaterial = displayMaterial;
        this.displayColor = displayColor;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RPGItemBuilder[] getStarterItems() {
        return starterItems;
    }

    public ChatColor getDisplayColor() {
        return displayColor;
    }

    public ItemStack getDisplayItem() {
        ItemStack displayItem = new ItemStack(this.displayMaterial);
        ItemMeta meta = displayItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET.toString() + displayColor + ChatColor.BOLD + name);
        meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\n" + description), ChatColor.GRAY.toString()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        displayItem.setItemMeta(meta);
        return displayItem;
    }
}
