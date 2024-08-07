package com.wagologies.spigotplugin.command.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.dungeon.generator.RoomType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DungeonRoomCommand extends PlayerCommand {
    public DungeonRoomCommand(SpigotPlugin plugin) {
        super(plugin, "room");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length != 1) {
            player.sendMessage(ChatColor.RED + "You must provide a name for this schematic");
            return true;
        }
        String schematicName = strings[0];
        com.sk89q.worldedit.entity.Player worldEditPlayer = BukkitAdapter.adapt(player);
        WorldEdit worldEdit = WorldEdit.getInstance();
        LocalSession session = worldEdit.getSessionManager().get(worldEditPlayer);
        try {
            Region region = session.getSelection();
            RoomType.saveSchematic(player.getWorld(), region, schematicName);
            player.sendMessage(ChatColor.GREEN + "Successfully saved room to " + schematicName + ".schem");
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "You must select a region first!");
        } catch (RuntimeException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
