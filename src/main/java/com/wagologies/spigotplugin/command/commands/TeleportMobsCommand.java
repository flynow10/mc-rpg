package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.mob.Mob;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportMobsCommand extends PlayerCommand {

    public TeleportMobsCommand(SpigotPlugin plugin) {
        super(plugin, "tpmobs");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        List<Mob> mobs = plugin.getMobManager().getMobs();
        for (Mob mob : mobs) {
            mob.teleport(player);
        }
        player.sendMessage("Teleported " + ChatColor.GREEN + mobs.size() + ChatColor.RESET + " to you");
        return true;
    }
}
