package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.SpigotPlugin;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager implements Listener {

    private final SpigotPlugin plugin;
    private boolean shouldAutoJoin = true;

    private final List<RPGPlayer> players = new ArrayList<>();
    public PlayerManager(SpigotPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 1);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Loaded Player Manager");
    }

    private void tick() {
        for (RPGPlayer player : players) {
            player.tick();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(shouldAutoJoin) {
            Player player = event.getPlayer();
            joinPlayer(player);
        }
    }

    public RPGPlayer joinPlayer(Player player) {
        RPGPlayer currentPlayer = players.stream().filter(p -> p.getPlayer().equals(player)).findAny().orElse(null);
        if(currentPlayer != null) {
            return currentPlayer;
        }
        RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
        players.add(rpgPlayer);
        player.sendMessage(ChatColor.GREEN + "Welcome to the unnamed RPG!");
        plugin.getLogger().info("Added player \"" + player.getName() + "\" to player manager");
        return rpgPlayer;
    }

    public void leavePlayer(Player player) {
        players.stream().filter(p -> p.getPlayer().equals(player)).findAny().ifPresent(this::leavePlayer);
    }

    public void leavePlayer(RPGPlayer player) {
        player.leavePlayer();
        player.getPlayer().sendMessage(ChatColor.GREEN + "Successfully left!");
        plugin.getLogger().info("Removed Player \"" + player.getPlayer().getName() + "\" from player manager");
        players.remove(player);
    }

    public RPGPlayer getPlayer(Player player) {
        return players.stream().filter(rpgPlayer -> rpgPlayer.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public List<RPGPlayer> getPlayers() {
        return players;
    }

    public void toggleAutoJoin(boolean newValue) {
        shouldAutoJoin = newValue;
    }

    public boolean getShouldAutoJoin() {
        return shouldAutoJoin;
    }
}
