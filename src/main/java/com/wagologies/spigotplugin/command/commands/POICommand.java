package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.PointOfInterest;
import com.wagologies.spigotplugin.command.SubCommands;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class POICommand extends SubCommands {

    public POICommand(SpigotPlugin plugin) {
        super(plugin, "poi");
    }

    @Override
    public boolean subCommandExecutor(Player player, String s, String[] subCommands) {
        if(subCommands.length == 0) {
            player.sendMessage(ChatColor.RED + "Missing point of interest!");
            return true;
        }
        PointOfInterest pointOfInterest = PointOfInterest.valueOf(subCommands[0]);
        Location location = pointOfInterest.toLocation(player.getWorld());
        if(player.getWorld().getName().equals("lobby")) {
            player.sendMessage(ChatColor.YELLOW + "Warning points of interest are not meant to be used in the lobby!");
        }
        player.teleport(location);
        return true;
    }

    @Override
    public String[] getSubCommands(int argNumber, Player player, String[] args) {
        if(argNumber == 1) {
            return Arrays.stream(PointOfInterest.values()).map(Enum::toString).toArray(String[]::new);
        }
        return new String[0];
    }
}
