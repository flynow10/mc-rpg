package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.mob.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends PlayerCommand {

    public SpawnCommand(SpigotPlugin plugin) {
        super(plugin, "spawnmob");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        Mob mob = null;
        if(strings.length >= 1) {
            if(strings[0].equals("0")) {
                mob = new Kobold();
            }
            if(strings[0].equals("1")) {
                mob = new GelatinousCube();
            }
            if(strings[0].equals("2")) {
                mob = new SplitterSpider();
            }
        }
        if(mob == null) {
            mob = new Kobold();
        }
        plugin.getMobManager().spawn(mob, player.getLocation());
        return true;
    }
}
