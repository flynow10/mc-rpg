package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KillAllCommand extends BaseCommand {
    public KillAllCommand(SpigotPlugin plugin) {
        super(plugin, "killall");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        int mobsKilled = plugin.getMobManager().killAll();
        commandSender.sendMessage("Killed " + ChatColor.GREEN + mobsKilled +  ChatColor.RESET + " mobs");
        return true;
    }
}
