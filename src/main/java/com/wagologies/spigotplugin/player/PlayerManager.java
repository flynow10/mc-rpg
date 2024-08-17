package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.player.RPGPlayerJoinEvent;
import com.wagologies.spigotplugin.event.player.RPGPlayerLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlayerManager implements Listener {
    private final SpigotPlugin plugin;
    private final List<RPGPlayer> players = new ArrayList<>();

    public PlayerManager(SpigotPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Loaded Player Manager");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        leavePlayer(event.getPlayer());
    }

    public RPGPlayer joinPlayer(Player player, Campaign campaign) {
        RPGPlayer currentPlayer = getPlayer(player);
        if(currentPlayer != null) {
            if(currentPlayer.getCampaign().equals(campaign)) {
                return currentPlayer;
            } else {
                leavePlayer(currentPlayer);
            }
        }

        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin, campaign);
        players.add(rpgPlayer);
        plugin.getEntityManager().addPlayer(rpgPlayer);

        RPGPlayerJoinEvent joinEvent = new RPGPlayerJoinEvent(rpgPlayer);
        Bukkit.getPluginManager().callEvent(joinEvent);

        player.sendMessage(ChatColor.GREEN + "Welcome to the unnamed RPG!");
        plugin.getLogger().info("Added player \"" + player.getName() + "\" to player manager");
        return rpgPlayer;
    }

    public void leavePlayer(Player player) {
        players.stream().filter(p -> p.getPlayer().equals(player)).findAny().ifPresent(this::leavePlayer);
    }

    public void leavePlayer(RPGPlayer player) {
        player.remove(false);

        players.remove(player);
        plugin.getEntityManager().removePlayer(player);

        RPGPlayerLeaveEvent leaveEvent = new RPGPlayerLeaveEvent(player, player.getCampaign());
        Bukkit.getPluginManager().callEvent(leaveEvent);

        plugin.getLogger().info("Removed Player \"" + player.getPlayer().getName() + "\" from player manager");
    }
    @Nullable
    public RPGPlayer getPlayer(Player player, Campaign campaign) {
        return players.stream().filter(rpgPlayer -> rpgPlayer.getPlayer().equals(player) && rpgPlayer.getCampaign().equals(campaign)).findFirst().orElse(null);
    }
    @Nullable
    public RPGPlayer getPlayer(Player player) {
        return players.stream().filter(rpgPlayer -> rpgPlayer.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public List<RPGPlayer> getPlayers() {
        return players;
    }
}
