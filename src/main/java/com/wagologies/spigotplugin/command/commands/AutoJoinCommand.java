package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AutoJoinCommand extends BaseCommand {

    public AutoJoinCommand(SpigotPlugin plugin) {
        super(plugin, "autojoin");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        PlayerManager playerManager = plugin.getPlayerManager();
        playerManager.toggleAutoJoin(!playerManager.getShouldAutoJoin());
        boolean shouldAutoJoin = playerManager.getShouldAutoJoin();
        commandSender.sendMessage("Set auto join to " + ChatColor.GREEN + shouldAutoJoin);
        return true;
    }
}
