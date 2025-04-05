package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.entity.AbilityScores;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.spell.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class StatsCommands extends PlayerCommand {

    public StatsCommands(SpigotPlugin plugin) {
        super(plugin, "stats");
        this.setPermission("");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length == 0 || !player.isOp()) {
            RPGPlayer rpgPlayer = plugin.getPlayerManager().getPlayer(player);
            if(rpgPlayer == null) {
                player.sendMessage(ChatColor.RED + "This command can't be used right now!");
                return true;
            }
            printStatsForPlayer(player, rpgPlayer);
            return true;
        } else {
            RPGPlayer rpgPlayer = null;
            Player bukkitPlayer = Bukkit.getPlayer(strings[0]);
            if(bukkitPlayer != null) {
                rpgPlayer = plugin.getPlayerManager().getPlayer(bukkitPlayer);
            }
            if(rpgPlayer == null) {
                player.sendMessage(ChatColor.RED + "This player is not in a campaign!");
                return true;
            }
            printStatsForPlayer(player, rpgPlayer);
            return true;
        }
    }

    public void printStatsForPlayer(Player sender, RPGPlayer player) {
        sender.sendMessage(ChatColor.GREEN + "Ability scores for " + ChatColor.AQUA + player.getName());
        AbilityScores abilityScores = player.getAbilityScores();
        for (AbilityScores.AbilityScore score : AbilityScores.AbilityScore.values()) {
            String name = score.name();
            int modifier = abilityScores.getModifier(score);
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            sender.sendMessage(" " + score.getColor() + ChatColor.BOLD + name + ": " + ChatColor.RESET + ChatColor.WHITE + abilityScores.getScore(score) + ChatColor.GRAY + " (" + (modifier > 0 ? ChatColor.GREEN + "+" : ChatColor.RED) + modifier + ChatColor.GRAY + ")");
        }
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "Known spells");
        for (SpellType spell : player.getKnownSpells()) {
            sender.sendMessage(" " + ChatColor.LIGHT_PURPLE + spell.getName());
        }
        sender.sendMessage("");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        List<RPGPlayer> players = plugin.getPlayerManager().getPlayers();
        if(args.length == 1 && sender.isOp()) {
            StringUtil.copyPartialMatches(args[0], players.stream().map(player -> player.getPlayer().getName()).toList(), completions);
        }
        return completions;
    }
}
