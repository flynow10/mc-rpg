package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class RPGItem {
    private static final Map<ItemStack, RPGItem> TrackedItems = new HashMap<>();
    protected static String ItemIdKey = "item-id";
    protected final SpigotPlugin plugin;
    protected final ItemStack itemStack;
    protected ItemType itemType;

    public RPGItem(SpigotPlugin plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        setItemType(ItemType.UNKNOWN);
        TrackedItems.put(itemStack, this);
    }

    protected void setItemType(ItemType itemType) {
        this.itemType = itemType;
        setPersistentData(ItemIdKey, PersistentDataType.STRING, itemType.toString());
    }

    @Nullable
    protected <P,C> C getPersistentData(String key, PersistentDataType<P,C> dataType) {
        ItemMeta meta = this.itemStack.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataHolder = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = plugin.getItemManager().createKey(key);
        if(!dataHolder.has(namespacedKey)) {
            return null;
        }
        return dataHolder.get(namespacedKey, dataType);
    }

    protected <P,C> void setPersistentData(String key, PersistentDataType<P,C> dataType, C data) {
        ItemMeta meta = this.itemStack.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(plugin.getItemManager().createKey(key), dataType, data);
        itemStack.setItemMeta(meta);
    }

    protected void clearPersistentData(String key) {
        ItemMeta meta = this.itemStack.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().remove(plugin.getItemManager().createKey(key));
        itemStack.setItemMeta(meta);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getDisplayName() {
        ItemMeta meta = this.itemStack.getItemMeta();
        assert meta != null;
        return meta.getDisplayName();
    }

    @Nullable
    public static RPGItem ConvertToCustomItem(SpigotPlugin plugin, ItemStack item) {
        if(item.getType().isAir()) {
            return null;
        }
        if(TrackedItems.containsKey(item)) {
            return TrackedItems.get(item);
        }
        ItemType type = GetItemType(item);
        switch (type) {
            case UNKNOWN -> {
                return new RPGItem(plugin, item);
            }
            case MELEE_WEAPON -> {
                return new MeleeWeapon(plugin, item);
            }
            case WAND -> {
                return new Wand(plugin, item);
            }
            case WAND_CORE -> {
                return new WandCore(plugin, item);
            }
            case ARMOR -> {
                return new Armor(plugin, item);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static ItemType GetItemType(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return ItemType.UNKNOWN;
        }
        String itemTypeString = meta.getPersistentDataContainer().get(ItemManager.getInstance().createKey(ItemIdKey), PersistentDataType.STRING);
        if(itemTypeString == null) {
            return ItemType.UNKNOWN;
        }
        try {
            return ItemType.valueOf(itemTypeString);
        } catch (IllegalArgumentException e) {
            return ItemType.UNKNOWN;
        }
    }
}
