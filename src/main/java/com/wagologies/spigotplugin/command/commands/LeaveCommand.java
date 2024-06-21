package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.command.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends PlayerCommand {

    public LeaveCommand(SpigotPlugin plugin) {
        super(plugin, "leave");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        plugin.getPlayerManager().leavePlayer(player);
        plugin.getLobbyManager().leavePlayer(player);
        player.sendMessage(ChatColor.GREEN + "Successfully removed from lobby and rpg listeners!");
        return true;
    }
}
