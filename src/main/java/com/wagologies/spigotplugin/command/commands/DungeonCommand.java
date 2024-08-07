package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.dungeon.generator.Generator;
import com.wagologies.spigotplugin.dungeon.generator.Maze;
import com.wagologies.spigotplugin.dungeon.generator.Room;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class DungeonCommand extends BaseCommand {
    public DungeonCommand(SpigotPlugin plugin) {
        super(plugin, "dungeon");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        Generator dungeonGenerator = getGenerator(strings);
        Consumer<String> sendMessage;
        if(!(commandSender instanceof Player)) {
            sendMessage = commandSender::sendMessage;
        } else {
            Logger logger = plugin.getLogger();
            sendMessage = logger::info;
        }
        String maze = dungeonGenerator.getMaze().toString();
        for (String line : maze.split("\n")) {
            sendMessage.accept(line);
        }

        for (int i = 0; i < dungeonGenerator.getRooms().length; i++) {
            for (int j = 0; j < dungeonGenerator.getRooms()[i].length; j++) {
                Room room = dungeonGenerator.getRooms()[i][j];
                sendMessage.accept(room.toString());
            }
        }

        if(commandSender instanceof Player player) {
            dungeonGenerator.pasteDungeon(player.getWorld(), player.getLocation());
        }
        return true;
    }

    private @NotNull Generator getGenerator(@NotNull String[] strings) {
        int width = 5;
        int height = 5;
        if(strings.length == 1) {
            try {
                height = width = Integer.parseInt(strings[0]);
            } catch (NumberFormatException ignored) {}
        } else if(strings.length == 2) {
            try {
                width = Integer.parseInt(strings[0]);
            } catch (NumberFormatException ignored) {}
            try {
                height = Integer.parseInt(strings[1]);
            } catch (NumberFormatException ignored) {}
        }
        return new Generator(this.plugin, width, height);
    }
}
