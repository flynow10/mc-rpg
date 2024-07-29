package com.wagologies.spigotplugin.arena;

import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.triggers.BoxTrigger;
import com.wagologies.spigotplugin.event.RPGPlayerDeathEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Arena implements Listener {
    private final Campaign campaign;
    private final BoxTrigger entry;
    private final BoxTrigger exit;
    private final List<RPGPlayer> activePlayers = new ArrayList<>();
    private Mode arenaMode = Mode.TRAINING;
    

    public Arena(Campaign campaign) {
        this.campaign = campaign;
        entry = new BoxTrigger(campaign.getPlugin(), campaign).withBoxSize(617.47, 76.00, 892.44, 623.80, 80.78, 898.53).withActivateMultiple(true).withCallback(this::onEnterArena);
        exit = new BoxTrigger(campaign.getPlugin(), campaign).withBoxSize(625.50, 76.00, 894.01, 627.03, 78.86, 897.03).withActivateMultiple(true).withCallback(this::onExitArena);
        Bukkit.getPluginManager().registerEvents(this, campaign.getPlugin());
    }

    public void onEnterArena(RPGPlayer player) {
        if(activePlayers.contains(player)) {
            return;
        }
        player.setInArena(true);
        activePlayers.add(player);
        player.getPlayer().sendMessage(ChatColor.GREEN + "You have entered the arena");
    }

    public void onExitArena(RPGPlayer player) {
        this.onExitArena(player, true);
    }

    public void onExitArena(RPGPlayer player, boolean voluntary) {
        if(!activePlayers.contains(player)) {
            return;
        }
        player.setInArena(false);
        activePlayers.remove(player);
        if(voluntary) {
            player.getPlayer().sendMessage(ChatColor.RED + "You have left the arena");
        }
    }

    @EventHandler
    public void onPlayerDeath(RPGPlayerDeathEvent event) {
        onExitArena(event.getRPGPlayer(), false);
    }

    public enum Mode {
        PVP,
        TRAINING
    }
}
