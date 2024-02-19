package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.command.PlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends PlayerCommand {
    public JoinCommand(SpigotPlugin plugin) {
        super(plugin, "join");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        plugin.getPlayerManager().joinPlayer(player);
        return true;
    }
}
