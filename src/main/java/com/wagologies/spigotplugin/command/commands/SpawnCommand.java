package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.mob.*;
import com.wagologies.spigotplugin.mob.mobs.GelatinousCube;
import com.wagologies.spigotplugin.mob.mobs.Kobold;
import com.wagologies.spigotplugin.mob.mobs.Kobold2;
import com.wagologies.spigotplugin.mob.mobs.SplitterSpider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class SpawnCommand extends PlayerCommand {

    public SpawnCommand(SpigotPlugin plugin) {
        super(plugin, "spawnmob");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length >= 1) {
            try {
                MobType mobType = Arrays.stream(MobType.values()).filter(type -> type.getName().equals(strings[0])).findAny().orElseThrow();
                plugin.getMobManager().spawn(mobType, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Successfully spawned a " + mobType.getName());
            } catch (NoSuchElementException e) {
                player.sendMessage(ChatColor.RED + "There is no mob type named " + strings[0] + "!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Missing mob type!");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.stream(MobType.values()).map(MobType::getName).toList(), completions);
        }
        return completions;
    }
}
