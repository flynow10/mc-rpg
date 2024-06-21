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
    private final List<RPGPlayer> activePlayers = new ArrayList<>();
    private Mode arenaMode = Mode.TRAINING;

    public Arena(Campaign campaign) {
        this.campaign = campaign;
        entry = new BoxTrigger(campaign.getPlugin(), campaign).withBoxSize(617.47, 76.00, 892.44, 623.80, 80.78, 898.53).withActivateMultiple(true).withCallback(this::onEnterArena);
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

    @EventHandler
    public void onPlayerDeath(RPGPlayerDeathEvent event) {
        activePlayers.remove(event.getRPGPlayer());
    }

    public enum Mode {
        PVP,
        TRAINING
    }
}
