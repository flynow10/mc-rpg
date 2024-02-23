package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.utils.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WandCore extends CustomItem {
    private static final String CoreTypeKey = "wand-core-type";
    private WandCoreType coreType;
    public WandCore(SpigotPlugin plugin, ItemStack itemStack) {
        super(plugin, itemStack);
        setItemType(ItemType.WAND_CORE);
        loadCoreType();
        updateItemDisplay();
    }

    public WandCore(SpigotPlugin plugin, ItemStack itemStack, WandCoreType coreType) {
        super(plugin, itemStack);
        setItemType(ItemType.WAND_CORE);
        setCoreType(coreType);
        updateItemDisplay();
    }

    public void updateItemDisplay() {
        ItemHelper.AddGlow(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(coreType.getName());
        itemStack.setItemMeta(meta);
    }

    private void loadCoreType() {
        String coreData = getPersistentData(CoreTypeKey, PersistentDataType.STRING);
        setCoreType(WandCoreType.fromString(coreData));
    }

    private void setCoreType(WandCoreType coreType) {
        this.coreType = coreType;
        setPersistentData(CoreTypeKey, PersistentDataType.STRING, coreType.toString());
    }

    public WandCoreType getCoreType() {
        return coreType;
    }
}
