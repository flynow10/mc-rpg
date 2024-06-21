package com.wagologies.spigotplugin.lobby;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CampaignSelector implements Listener {
    private final SpigotPlugin plugin;
    private final Inventory inventory;

    public CampaignSelector(SpigotPlugin plugin) {
        this.plugin = plugin;
        inventory = Bukkit.createInventory(null, 9);
        setupInventory();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        setupInventory();
        player.openInventory(this.inventory);
    }

    public void setupInventory() {
        inventory.clear();
        List<Campaign> campaigns = plugin.getCampaignManager().getCampaigns();
        for (int i = 0; i < Math.min(campaigns.size(), 7); i++) {
            Campaign campaign = campaigns.get(i);
            ItemStack displayItem = createCampaignItem(campaign);
            inventory.setItem(i, displayItem);
        }
        inventory.setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        ItemStack newCampaign = new ItemStack(Material.PAPER);
        ItemMeta newCampaignMeta = newCampaign.getItemMeta();
        assert newCampaignMeta != null;
        newCampaignMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "[+] Create a new campaign");
        newCampaignMeta.setLore(List.of(ChatColor.RESET.toString() + ChatColor.GRAY + "A new adventure awaits ..."));
        newCampaign.setItemMeta(newCampaignMeta);
        inventory.setItem(8, newCampaign);
    }

    public ItemStack createCampaignItem(Campaign campaign) {
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + campaign.getName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.RESET.toString() + ChatColor.GRAY + "Players:");
        for (OfflinePlayer player : campaign.getPlayers()) {
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(player.getPlayerId()));
            lore.add(ChatColor.RESET.toString() + ChatColor.GREEN + " > " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " (" + ChatColor.DARK_GRAY + offlinePlayer.getName() + ChatColor.GRAY + ")");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getView().getTopInventory().equals(inventory)) {
            event.setCancelled(true);
            if(Objects.equals(event.getClickedInventory(), inventory)) {
                HumanEntity humanEntity = event.getWhoClicked();
                if(!(humanEntity instanceof Player player)) {
                    return;
                }
                if(event.getSlot() == 8) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN + "Creating a new adventure ...");
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            Campaign campaign = plugin.getCampaignManager().createNewCampaign();
                            player.sendMessage(ChatColor.GREEN + "Successfully created a new campaign!");
                            plugin.getLobbyManager().openCharacterCreator(player, campaign);
                            setupInventory();
                        }, 1);
                    });
                }
                if(event.getSlot() < 7) {
                    Campaign campaign = plugin.getCampaignManager().getCampaigns().get(event.getSlot());
                    if(campaign == null) {
                        return;
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if(campaign.getPlayers().stream().anyMatch(p -> p.getPlayerId().equals(player.getUniqueId().toString()))) {
                            plugin.getLobbyManager().joinCampaign(player, campaign);
                        } else {
                            plugin.getLobbyManager().openCharacterCreator(player, campaign);
                        }
                    });
                }
            }
        }
    }
}
