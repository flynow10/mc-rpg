package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DamagePlayerCommand extends PlayerCommand {
    public DamagePlayerCommand(SpigotPlugin plugin) {
        super(plugin, "damage");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length == 0) {
            player.sendMessage(ChatColor.RED + "Missing damage amount!");
            return true;
        }
        RPGPlayer playerToDamage = null;
        if (strings.length == 1) {
            playerToDamage = plugin.getPlayerManager().getPlayer(player);
            if (playerToDamage == null) {
                player.sendMessage(ChatColor.RED + "You have not joined!");
                return true;
            }
        } else if(strings.length == 2) {
            Player bukkitPlayerToDamage = Bukkit.getPlayer(strings[0]);
            if(bukkitPlayerToDamage != null) {
                playerToDamage = plugin.getPlayerManager().getPlayer(bukkitPlayerToDamage);
            }
            if(playerToDamage == null) {
                player.sendMessage(ChatColor.RED + "This player cannot be damaged!");
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }

        int damage = -1;
        try {
            damage = Integer.parseInt(strings[strings.length - 1]);
        } catch (NumberFormatException ignored) {}
        if(damage <= 0) {
            player.sendMessage(ChatColor.RED + "Invalid damage number");
            return true;
        }

        playerToDamage.damage(damage);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        List<RPGPlayer> players = plugin.getPlayerManager().getPlayers();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], players.stream().map(player -> player.getPlayer().getName()).toList(), completions);
        }
        return completions;
    }
}
