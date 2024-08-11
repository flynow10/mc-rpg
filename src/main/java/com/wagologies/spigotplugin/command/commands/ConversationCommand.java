package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.event.ConversationInteractionEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConversationCommand extends PlayerCommand {
    public ConversationCommand(SpigotPlugin plugin) {
        super(plugin, "conversation");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        RPGPlayer rpgPlayer = plugin.getPlayerManager().getPlayer(player);
        if(rpgPlayer == null) {
            return errorMessage(player);
        }

        if(strings.length != 2) {
            return errorMessage(player);
        }
        ConversationInteractionEvent event = new ConversationInteractionEvent(rpgPlayer, strings[0], strings[1]);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public boolean errorMessage(Player player) {
        player.sendMessage(ChatColor.RED + "This command is for internal use only");
        return true;
    }
}
