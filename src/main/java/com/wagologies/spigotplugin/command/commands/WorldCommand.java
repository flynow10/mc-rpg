package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.battle.Battle;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.utils.WorldHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WorldCommand extends PlayerCommand {
    private static final String[] SUB_COMMANDS = {"tp", "load", "create", "list"};
    public WorldCommand(SpigotPlugin plugin) {
        super(plugin, "world");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length < 1 || (strings.length < 2 && !strings[0].equals("list"))) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            return true;
        }
        if(Arrays.stream(SUB_COMMANDS).noneMatch(cmd -> Objects.equals(strings[0], cmd))) {
            player.sendMessage(ChatColor.RED + "Invalid sub command!");
            return true;
        }
        World world = null;
        if(strings.length >= 2) {
            world = Bukkit.getWorld(strings[1]);
        }
        switch (strings[0]) {
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
                    World newWorld = WorldHelper.loadWorld(strings[1]);
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
                    World newWorld = WorldHelper.createCampaignWorld(strings[1]);
                    player.sendMessage(ChatColor.GREEN + "Created a new campaign world " + newWorld.getName());
                } catch (RuntimeException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
                break;
            }
            case "list": {
                player.sendMessage(ChatColor.GREEN + "Worlds list");
                for (World loadedWorld : Bukkit.getWorlds()) {
                    player.sendMessage(loadedWorld.getName());
                }
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of(SUB_COMMANDS), completions);
        }
        if(args.length == 2 && args[0].equals("tp")) {
            StringUtil.copyPartialMatches(args[1], Bukkit.getWorlds().stream().map(WorldInfo::getName).toList(), completions);
        }
        return completions;
    }
}
