package com.wagologies.spigotplugin.npc;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Merchant extends NPC {
    public Merchant(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        SGMenu shopMenu = getPlugin().getGuiManager().create("Shop", 5);
        SGButton button = new SGButton(new ItemBuilder(Material.WOODEN_SWORD).build()).withListener((InventoryClickEvent inventoryEvent) -> {
            MeleeWeapon meleeWeapon = new MeleeWeapon(getPlugin(), new ItemStack(Material.IRON_SWORD));
            Inventory inventory = inventoryEvent.getWhoClicked().getInventory();
            inventory.addItem(meleeWeapon.getItemStack());
        });
        shopMenu.setButton(10, button);

        int[] borderSlots = new int[] {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9,                      17,
                18,                     26,
                27,                     35,
                36,37,38,39,40,41,42,43,44
        };
        for (int borderSlot : borderSlots) {
            shopMenu.setButton(borderSlot, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(ChatColor.RESET.toString() + ChatColor.DARK_GRAY + "Click an item to buy it!").build()));
        }
        event.getPlayer().openInventory(shopMenu.getInventory());
    }
}
