package com.wagologies.spigotplugin.campaign.triggers;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Trigger implements Listener {

    private final SpigotPlugin plugin;

    private final Campaign campaign;
    private boolean activateMultiple = false;
    private final List<RPGPlayer> hasActivated = new ArrayList<>();
    private final List<RPGPlayer> containedPlayers = new ArrayList<>();
    private Consumer<RPGPlayer> callback;

    public Trigger(SpigotPlugin plugin, Campaign campaign) {
        this.plugin = plugin;
        this.campaign = campaign;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public abstract boolean didEnter(Location from, Location to, RPGPlayer player);
    public abstract boolean didLeave(Location from, Location to, RPGPlayer player);
    public abstract void visualize();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        RPGPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer(), campaign);
        if(player == null) {
            return;
        }

        if(!player.getWorld().equals(campaign.getWorld())) {
            return;
        }

        if(didEnter(event.getFrom(), event.getTo(), player)) {
            containedPlayers.add(player);
            if(!hasActivated.contains(player) || activateMultiple) {
                if(callback != null) {
                    callback.accept(player);
                    hasActivated.add(player);
                }
            }
        }

        if(didLeave(event.getFrom(), event.getTo(), player)) {
            containedPlayers.remove(player);
        }
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }

    protected List<RPGPlayer> getContainedPlayers() {
        return containedPlayers;
    }

    public boolean isActivateMultiple() {
        return activateMultiple;
    }

    public Consumer<RPGPlayer> getCallback() {
        return callback;
    }

    public void setActivateMultiple(boolean activateMultiple) {
        this.activateMultiple = activateMultiple;
    }

    public void setCallback(Consumer<RPGPlayer> callback) {
        this.callback = callback;
    }

    public Trigger withActivateMultiple(boolean activateMultiple) {
        setActivateMultiple(activateMultiple);
        return this;
    }

    public Trigger withCallback(Consumer<RPGPlayer> callback) {
        setCallback(callback);
        return this;
    }

    public List<RPGPlayer> getHasActivated() {
        return hasActivated;
    }

    public SpigotPlugin getPlugin() {
        return plugin;
    }

    public Campaign getCampaign() {
        return campaign;
    }
}
