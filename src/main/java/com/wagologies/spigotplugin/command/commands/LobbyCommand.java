package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import org.bukkit.entity.Player;

public class LobbyCommand extends PlayerCommand {
    public LobbyCommand(SpigotPlugin plugin) {
        super(plugin, "lobby");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        plugin.getLobbyManager().joinPlayer(player);
        return true;
    }
}
