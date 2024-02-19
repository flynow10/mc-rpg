package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class RPGPlayer implements Listener {
    private final Player player;
    private final SpigotPlugin plugin;
    public RPGPlayer(Player player, SpigotPlugin plugin) {
        this.player = player;
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void leavePlayer() {
        HandlerList.unregisterAll(this);
    }

    public Player getPlayer() {
        return player;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterPlace(PlayerBucketEmptyEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterPickup(PlayerBucketFillEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onActivateBlock(PlayerInteractEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeaponDamage(PlayerItemDamageEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }
}
