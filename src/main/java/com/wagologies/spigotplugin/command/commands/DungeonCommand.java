package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.command.SubCommands;
import com.wagologies.spigotplugin.dungeon.Dungeon;
import com.wagologies.spigotplugin.dungeon.DungeonManager;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DungeonCommand extends SubCommands {
    private static final String[] SUB_COMMANDS = {"create", "start", "delete", "tp", "join"};

    public DungeonCommand(SpigotPlugin plugin) {
        super(plugin, "dungeon");
    }
    @Override
    public boolean subCommandExecutor(Player player, String s, String[] subCommands) {
        if(!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
            return true;
        }
        RPGPlayer rpgPlayer = plugin.getPlayerManager().getPlayer(player);
        if(rpgPlayer == null) {
            player.sendMessage(ChatColor.RED + "This command can't be used right now!");
            return true;
        }

        Campaign campaign = rpgPlayer.getCampaign();

        DungeonManager dungeonManager = plugin.getDungeonManager();
        Dungeon dungeon = dungeonManager.getDungeon(campaign);
        if(dungeon == null && !subCommands[0].equals("create")) {
            player.sendMessage(ChatColor.RED + "No dungeon exists in this campaign!");
            return true;
        }

        switch (subCommands[0]) {
            case "create": {
                if(dungeon != null) {
                    player.sendMessage(ChatColor.RED + "A dungeon already exists in this campaign!");
                    break;
                }
                dungeonManager.createDungeon(campaign, campaign.getLastCompletedFloor() + 1);
                player.sendMessage(ChatColor.GREEN + "Successfully created a new dungeon in " + campaign.getName() + "!");
                break;
            }
            case "start": {
                try {
                    dungeon.start();
                } catch (IllegalStateException e) {
                    player.sendMessage(ChatColor.RED + "This dungeon has already been started!");
                }
                player.sendMessage(ChatColor.GREEN + "Successfully started a dungeon in " + campaign.getName() + "!");
                break;
            }
            case "delete": {
                dungeon.cleanup();
                player.sendMessage(ChatColor.GREEN + "Successfully deleted a dungeon in " + campaign.getName() + "!");
                break;
            }
            case "tp": {
                player.teleport(dungeon.getSpawnLocation());
                player.sendMessage(ChatColor.GREEN + "Successfully teleported you to the dungeon!");
                break;
            }
            case "join": {
                dungeon.getPlayers().add(rpgPlayer);
                player.sendMessage(ChatColor.GREEN + "Successfully joined the dungeon!");
                break;
            }
        }

        return true;
    }

    @Override
    public String[] getSubCommands(int argNumber, Player player, String[] args) {
        if(argNumber == 1) {
            return SUB_COMMANDS;
        }
        return new String[0];
    }
}
