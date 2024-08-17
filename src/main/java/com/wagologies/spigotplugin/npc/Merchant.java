package com.wagologies.spigotplugin.npc;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public abstract class Merchant extends NPC {
    public Merchant(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        openShop(event.getRPGPlayer());
    }

    public void openShop(RPGPlayer clickingPlayer) {
        ShopItem[] shopItems = getShopItems();
        int[] borderSlots = getBorderSlots(getShopRows());
        System.out.println(Arrays.toString(borderSlots));

        SGMenu shopMenu = getPlugin().getGuiManager().create(getShopName(), getShopRows() + 2);

        int currentSlot = 10;
        for (ShopItem shopItem : shopItems) {
            ItemBuilder builder = new ItemBuilder(shopItem.getItem().getItemStack().clone());
            List<String> lore = builder.getLore();
            lore.add("");
            lore.add(ChatColor.RESET.toString() + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + shopItem.getCost());
            lore.add(ChatColor.RESET.toString() + ChatColor.YELLOW + "Click to buy!");
            builder.lore(lore);
            SGButton button = new SGButton(builder.build()).withListener((InventoryClickEvent inventoryEvent) -> {
                if(clickingPlayer.payCoins(shopItem.getCost())) {
                    PlayerInventory playerInventory = clickingPlayer.getPlayer().getInventory();
                    playerInventory.addItem(shopItem.getItem().getItemStack());
                } else {
                    clickingPlayer.getPlayer().sendMessage(ChatColor.RED + "You don't have enough coins for this!");
                }
            });

            shopMenu.setButton(currentSlot, button);
            do {
                currentSlot ++;
            } while(ArrayUtils.contains(borderSlots, currentSlot) && currentSlot < 35);

            if(currentSlot >= 35) {
                break;
            }
        }
        for (int borderSlot : borderSlots) {
            shopMenu.setButton(borderSlot, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(ChatColor.RESET.toString() + ChatColor.DARK_GRAY + "Click an item to buy it!").build()));
        }
        clickingPlayer.getPlayer().openInventory(shopMenu.getInventory());
    }

    public abstract ShopItem[] getShopItems();

    public abstract String getShopName();

    /**
     * @return A number of rows n such that 1 <= n <= 4
     * Defaults to 3 rows
     */
    public int getShopRows() {
        return 3;
    }

    private int[] getBorderSlots(int shopRows) {
        int[] borderSlots = new int[shopRows * 2 + 18];
        int totalSlots = (shopRows + 2) * 9;
        for (int s = 0, idx = 0; s < totalSlots; s++) {
            if(s < 9 || s % 9 == 0 || (s + 1) % 9 == 0 || s + 9 > totalSlots) {
                borderSlots[idx++] = s;
            }
        }
        return borderSlots;
    }

    public static class ShopItem {
        private RPGItem item;
        private int cost;
        public ShopItem(RPGItem item, int cost) {
            this.item = item;
            this.cost = cost;
        }

        public RPGItem getItem() {
            return item;
        }

        public int getCost() {
            return cost;
        }
    }
}
