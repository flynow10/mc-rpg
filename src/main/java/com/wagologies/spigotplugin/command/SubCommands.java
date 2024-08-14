package com.wagologies.spigotplugin.command;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class SubCommands extends PlayerCommand {

    public SubCommands(SpigotPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            String subCommand = strings[i];
            String[] subCommands = getSubCommands(i + 1, player, strings);
            if (Arrays.stream(subCommands).noneMatch(cmd -> Objects.equals(subCommand, cmd))) {
                player.sendMessage(ChatColor.RED + "Invalid sub command!");
                return true;
            }
        }
        if(strings.length == 0) {
            player.sendMessage(ChatColor.RED + "No sub commands specified!");
            return true;
        }

        return subCommandExecutor(player, s, strings);
    }

    public abstract boolean subCommandExecutor(Player player, String s, String[] subCommands);

    public abstract String[] getSubCommands(int argNumber, Player player, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(sender instanceof Player player) {
            String[] subCommands = getSubCommands(args.length, player, args);
            if(subCommands.length != 0) {
                StringUtil.copyPartialMatches(args[args.length - 1], List.of(subCommands), completions);
            }
        }
        return completions;
    }
}
