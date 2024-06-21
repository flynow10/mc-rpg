package com.wagologies.spigotplugin.lobby;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.player.OfflinePlayer;
import com.wagologies.spigotplugin.utils.WorldHelper;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LobbyManager implements Listener {
    public static final String LobbyWorldName = "lobby";
    private final SpigotPlugin plugin;
    private final CampaignSelector campaignSelector;
    private final NamespacedKey lobbyItemType;
    private final World lobby;
    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final Map<Player, CharacterCreator> inProgressCharacters = new HashMap<>();
    public LobbyManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        this.campaignSelector = new CampaignSelector(this.plugin);
        lobbyItemType = new NamespacedKey(this.plugin, "lobby-item");
        lobby = WorldHelper.loadWorld(LobbyWorldName);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Loaded Lobby Manager");
    }

    public void leavePlayer(Player player) {
        lobbyPlayers.remove(player);
        CharacterCreator characterCreator = inProgressCharacters.remove(player);
        if(characterCreator != null) {
            characterCreator.remove();
        }
    }

    public void joinPlayer(Player player) {
        lobbyPlayers.add(player);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(lobby.getSpawnLocation());
        setLobbyItems(player);
        player.sendTitle(ChatColor.AQUA + "Welcome!", "", 10, 40, 10);
    }

    public void setLobbyItems(Player player) {
        ItemStack campaignManagerItem = getCampaignManagerItem();
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        playerInventory.setItem(0, campaignManagerItem);
    }

    public ItemStack getCampaignManagerItem() {
        ItemStack createNewCampaign = new ItemStack(Material.BOOK);
        ItemMeta meta = createNewCampaign.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Campaign Manager");
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(lobbyItemType, PersistentDataType.STRING, "manager");
        meta.setLore(List.of(ChatColor.RESET.toString() + ChatColor.GRAY + "Here there be dragons"));
        createNewCampaign.setItemMeta(meta);
        return createNewCampaign;
    }

    public void openCharacterCreator(Player player, Campaign campaign) {
        CharacterCreator characterCreator;
        if(inProgressCharacters.containsKey(player)) {
            characterCreator = inProgressCharacters.get(player);
        } else {
            characterCreator = new CharacterCreator(plugin, player);
            inProgressCharacters.put(player, characterCreator);
        }
        characterCreator.setCampaign(campaign);
        characterCreator.open();
    }

    public void openCampaignSelector(Player player) {
        campaignSelector.open(player);
    }

    public void joinCampaign(Player player, Campaign campaign) {
        leavePlayer(player);
        player.closeInventory();
        campaign.joinPlayer(player);
    }

    public void createNewCharacter(Player player, OfflinePlayer character, Campaign campaign) {
        campaign.getPlayers().add(character);
        joinCampaign(player, campaign);
    }

    public boolean isLobbyItem(ItemStack itemStack) {
        if(itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if(meta != null) {
                return Objects.equals(meta.getPersistentDataContainer().get(lobbyItemType, PersistentDataType.STRING), "manager");
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.isOp()) {
            joinPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        leavePlayer(event.getPlayer());
    }

    public void disabledPlayerEvent(Player player, Cancellable event) {
        if(lobbyPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBreakBlock(BlockBreakEvent event) {
        disabledPlayerEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void playerPlaceBlock(BlockPlaceEvent event) {
        disabledPlayerEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player player) {
            disabledPlayerEvent(player, event);
        }
    }

    @EventHandler
    public void playerHunger(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player player) {
            disabledPlayerEvent(player, event);
        }
    }

    @EventHandler
    public void playerInteractWithInventory(InventoryClickEvent event) {
        Entity entity = event.getWhoClicked();
        if(entity instanceof Player player) {
            if(player.getInventory().equals(event.getClickedInventory()) && event.getView().getType().equals(InventoryType.CRAFTING)) {
                disabledPlayerEvent(player, event);
            }
            if(lobbyPlayers.contains(player)) {
                ItemStack clickedItem = event.getCurrentItem();
                if(isLobbyItem(clickedItem)) {
                    Bukkit.getScheduler().runTask(plugin, () -> openCampaignSelector(player));
                }
            }
        }
    }

    @EventHandler
    public void playerSwapHands(PlayerSwapHandItemsEvent event) {
        disabledPlayerEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void playerPickUpItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player player) {
            disabledPlayerEvent(player, event);
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        disabledPlayerEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void playerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(lobbyPlayers.contains(player)) {
            ItemStack itemStack = event.getItem();
            if(isLobbyItem(itemStack)) {
                openCampaignSelector(player);
            }
        }
    }

    @EventHandler
    public void playerInventoryClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if(humanEntity instanceof Player player) {
            if(lobbyPlayers.contains(player)) {
                player.setItemOnCursor(null);
                setLobbyItems(player);
                player.updateInventory();
            }
        }
    }
}
