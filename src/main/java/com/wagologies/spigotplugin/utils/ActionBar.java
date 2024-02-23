package com.wagologies.spigotplugin.utils;

import com.wagologies.spigotplugin.SpigotPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActionBar {

    private final SpigotPlugin plugin;

    public ActionBar(SpigotPlugin plugin) {
        this.plugin = plugin;

        plugin.getLogger().info("Loaded Action Bar Manager");
    }
    public void sendActionBar(Player player, String message) {
        if(!player.isOnline()) {
            return;
        }

        ActionBarEvent actionBarEvent = new ActionBarEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarEvent);
        if(actionBarEvent.isCancelled()) {
            return;

        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void sendActionBar(Player player, String message, int duration) {
        this.sendActionBar(player, message, duration, true);
    }

    public void sendActionBar(Player player, String message, int duration, boolean clearAtEnd) {
        sendActionBar(player, message);
        if(duration >= 0 && clearAtEnd) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                sendActionBar(player, "");
            }, duration + 1);
        }

        while(duration > 40) {
            duration -= 40;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                sendActionBar(player, message);
            }, duration);
        }
    }

    public static class ActionBarEvent extends Event implements Cancellable {
        private static final HandlerList handlers = new HandlerList();
        private final Player player;
        private String message;
        private boolean cancelled = false;

        public ActionBarEvent(Player player, String message) {
            this.player = player;
            this.message = message;
        }
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public Player getPlayer() {
            return player;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean b) {
            cancelled = b;
        }
    }
}