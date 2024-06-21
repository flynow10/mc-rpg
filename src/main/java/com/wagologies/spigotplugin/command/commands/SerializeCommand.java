package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SerializeCommand extends PlayerCommand {
    public SerializeCommand(SpigotPlugin plugin) {
        super(plugin, "serialize");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] inventoryContents = playerInventory.getContents();
        int count = inventoryContents.length;
        try {
            if(strings.length >= 1) {
                int argCount = Integer.parseInt(strings[0]);
                if(argCount > 0) {
                    count = Math.min(argCount, inventoryContents.length);
                }
            }
        } catch (NumberFormatException ignored) {}
        player.sendMessage(ChatColor.GREEN + "Inventory Serialized");
        for (int i = 0; i < count; i++) {
            ItemStack item = inventoryContents[i];
            if(item != null) {
                player.sendMessage("Slot: " + i + " Item: \"" + item.serialize() + "\"");
            }
        }
        return true;
    }
}
