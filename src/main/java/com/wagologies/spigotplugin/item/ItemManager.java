package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.NamespacedKey;

public class ItemManager {
    private static ItemManager instance;
    private final SpigotPlugin plugin;

    public ItemManager(SpigotPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        plugin.getLogger().info("Loaded Item Manager");
    }

    public NamespacedKey createKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public static ItemManager getInstance() {
        return instance;
    }
}
