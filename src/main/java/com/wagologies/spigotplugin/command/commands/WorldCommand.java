package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.SubCommands;
import com.wagologies.spigotplugin.utils.WorldHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

public class WorldCommand extends SubCommands {
    private static final String[] SUB_COMMANDS = {"tp", "load", "create", "list"};
    public WorldCommand(SpigotPlugin plugin) {
        super(plugin, "world");
    }

    @Override
    public boolean subCommandExecutor(Player player, String s, String[] subCommands) {
        if(subCommands.length < 1 || (subCommands.length < 2 && !subCommands[0].equals("list"))) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            return true;
        }
        World world = null;
        if(subCommands.length >= 2) {
            world = Bukkit.getWorld(subCommands[1]);
        }
        switch (subCommands[0]) {
            case "tp": {
                if(world == null) {
                    player.sendMessage(ChatColor.RED + "This world is not loaded!");
                    break;
                }
                player.sendMessage(ChatColor.GREEN + "Sending you to " + world.getName());
                player.teleport(world.getSpawnLocation());
                break;
            }
            case "load": {
                if(world != null) {
                    player.sendMessage(ChatColor.GREEN + "This world is already loaded!");
                    break;
                }
                try {
                    World newWorld = WorldHelper.loadWorld(subCommands[1]);
                    player.sendMessage(ChatColor.GREEN + "Loaded world " + newWorld.getName());
                } catch (RuntimeException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
                break;
            }
            case "create": {
                if(world != null) {
                    player.sendMessage(ChatColor.RED + "This name already exists, cannot create again!");
                    break;
                }
                try {
                    World newWorld = WorldHelper.createCampaignWorld(subCommands[1]);
                    player.sendMessage(ChatColor.GREEN + "Created a new campaign world " + newWorld.getName());
                } catch (RuntimeException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
                break;
            }
            case "list": {
                player.sendMessage(ChatColor.GREEN + "Worlds list");
                for (World loadedWorld : Bukkit.getWorlds()) {
                    if(player.getWorld().equals(loadedWorld)) {
                        player.sendMessage(loadedWorld.getName() + ChatColor.GREEN + " <-- You are here");
                    } else {
                        player.sendMessage(loadedWorld.getName());
                    }
                }
                break;
            }
        }
        return true;
    }

    @Override
    public String[] getSubCommands(int argNumber, Player player, String[] args) {
        if(argNumber == 1) {
            return SUB_COMMANDS;
        } else if(argNumber == 2 && args[0].equals("tp")) {
            return Bukkit.getWorlds().stream().map(WorldInfo::getName).toArray(String[]::new);
        }
        return new String[0];
    }
}
