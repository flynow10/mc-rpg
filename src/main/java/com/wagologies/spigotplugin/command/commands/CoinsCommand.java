package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.SubCommands;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CoinsCommand extends SubCommands {
    private static final String[] SUB_COMMANDS = {"bal", "add", "sub", "set"};
    public CoinsCommand(SpigotPlugin plugin) {
        super(plugin, "coins");
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

        int coins = 0;
        if(subCommand.equals("add") || subCommand.equals("sub") || subCommand.equals("set")) {
            if(subCommands.length < (hasPlayerArg ? 3 : 2)) {
                player.sendMessage(ChatColor.RED + "You must provide an amount of coins!");
                return true;
            }
            String numberArg = hasPlayerArg ? subCommands[2] : subCommands[1];
            try {
                coins = Integer.parseInt(numberArg);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Could not parse number " + numberArg);
                return true;
            }
        }

        switch (subCommand) {
            case "bal": {
                player.sendMessage(
                        ChatColor.AQUA + rpgPlayer.getName() + ChatColor.GREEN + " has " + ChatColor.GOLD + rpgPlayer.getCoins() + ChatColor.GREEN + " coins.");
                break;
            }
            case "add": {
                rpgPlayer.gainCoins(coins);
                player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.GOLD + coins + ChatColor.GREEN + " coins to " + ChatColor.AQUA + rpgPlayer.getName() + ChatColor.GREEN + ".");
                break;
            }
            case "sub": {
                boolean transactionSuccess = rpgPlayer.payCoins(coins);
                if(transactionSuccess) {
                    player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.GOLD + coins + ChatColor.GREEN + " coins from " + ChatColor.AQUA + rpgPlayer.getName() + ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to remove coins from " + ChatColor.AQUA + rpgPlayer.getName() + ChatColor.GREEN + " because their balance is too low.");
                }
                break;
            }
            case "set": {
                rpgPlayer.setCoins(coins);
                player.sendMessage(ChatColor.GREEN + "Set " + ChatColor.AQUA + rpgPlayer.getName() + "'s" + ChatColor.GREEN + " balance to " + ChatColor.GOLD + rpgPlayer.getCoins() + ChatColor.GREEN + " coins.");
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
        if(argNumber == 1) {
            if(!needsPlayerArg) {
                subCommands.addAll(List.of(SUB_COMMANDS));
            }
            subCommands.addAll(plugin.getPlayerManager().getPlayers().stream().map(RPGPlayer::getName).toList());
        } else if(argNumber == 2) {
            if(ArrayUtils.contains(SUB_COMMANDS, args[0])) {
                return new String[0];
            } else {
                return SUB_COMMANDS;
            }
        }
        return subCommands.toArray(new String[0]);
    }
}
