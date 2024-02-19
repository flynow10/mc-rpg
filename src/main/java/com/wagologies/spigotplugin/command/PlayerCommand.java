package com.wagologies.spigotplugin.command;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerCommand extends BaseCommand {
    public PlayerCommand(SpigotPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player) {
            return playerExecutor((Player) commandSender, s, strings);
        } else {
            commandSender.sendMessage("This command can only be run by a player!");
        }
        return true;
    }

    public abstract boolean playerExecutor(Player player, String s, String[] strings);
}
