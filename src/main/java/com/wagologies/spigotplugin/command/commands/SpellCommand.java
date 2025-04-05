package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.SubCommands;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.spell.SpellType;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellCommand extends SubCommands {

    public static final String[] SUB_COMMANDS = {"learn", "unlearn", "spellbook"};
    public SpellCommand(SpigotPlugin plugin) {
        super(plugin, "spell");
    }

    @Override
    public boolean subCommandExecutor(Player player, String s, String[] subCommands) {
        RPGPlayer rpgPlayer = plugin.getPlayerManager().getPlayer(player);
        boolean needsPlayerArg = rpgPlayer == null;
        boolean hasPlayerArg = needsPlayerArg;
        if(!needsPlayerArg) {
            hasPlayerArg = !ArrayUtils.contains(SUB_COMMANDS, subCommands[0]);
        }
        String subCommand = hasPlayerArg ? subCommands[1] : subCommands[0];
        if(hasPlayerArg) {
            rpgPlayer = plugin.getPlayerManager().getPlayers().stream().filter(p -> p.getName().equals(subCommands[0])).findFirst().orElse(null);
        }

        if(rpgPlayer == null) {
            player.sendMessage(ChatColor.RED + "Could not find player with name " + subCommands[0]);
            return true;
        }

        SpellType type = null;
        if(subCommand.equals("learn") || subCommand.equals("unlearn")) {
            if(subCommands.length < (hasPlayerArg ? 3 : 2)) {
                player.sendMessage(ChatColor.RED + "You must provide a spell name!");
                return true;
            }
            String spellName = hasPlayerArg ? subCommands[2] : subCommands[1];
            try {
                type = SpellType.valueOf(spellName);
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Invalid spell name!");
                return true;
            }
        }

        switch (subCommand) {
            case "learn": {
                rpgPlayer.learnSpell(type);
                rpgPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Added spell " + type.getColoredName() + ChatColor.GREEN + " to " + ChatColor.AQUA + rpgPlayer.getName() + "'s" + ChatColor.GREEN + " spell book");
                break;
            }
            case "unlearn": {
                rpgPlayer.unlearnSpell(type);
                rpgPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Removed spell " + type.getColoredName() + ChatColor.GREEN + " fromt " + ChatColor.AQUA + rpgPlayer.getName() + "'s" + ChatColor.GREEN + " spell book");
                break;
            }
            case "spellbook": {
                rpgPlayer.updateSpellBook(true);
                rpgPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Updated " + ChatColor.AQUA + rpgPlayer.getName() + "'s" + ChatColor.GREEN + " spell book");
                break;
            }
        }
        return true;
    }

    @Override
    public String[] getSubCommands(int argNumber, Player player, String[] args) {
        RPGPlayer rpgPlayer = plugin.getPlayerManager().getPlayer(player);
        boolean needsPlayerArg = rpgPlayer == null;
        List<String> subCommands = new ArrayList<>();
        String[] spellTypes = Arrays.stream(SpellType.values()).map(SpellType::name).toArray(String[]::new);
        if(argNumber == 1) {
            if(!needsPlayerArg) {
                subCommands.addAll(List.of(SUB_COMMANDS));
            }
            subCommands.addAll(plugin.getPlayerManager().getPlayers().stream().map(RPGPlayer::getName).toList());
            return subCommands.toArray(new String[0]);
        } else if(argNumber == 2) {
            if(ArrayUtils.contains(SUB_COMMANDS, args[0])) {
                if(args[0].equals("learn") || args[0].equals("unlearn")) {
                    return spellTypes;
                }
            } else {
                return SUB_COMMANDS;
            }
        } else if(argNumber == 3) {
            if(ArrayUtils.contains(SUB_COMMANDS, args[1])) {
                if(args[1].equals("learn") || args[1].equals("unlearn")) {
                    return spellTypes;
                }
            } else {
                return new String[0];
            }
        }
        return new String[0];
    }
}
