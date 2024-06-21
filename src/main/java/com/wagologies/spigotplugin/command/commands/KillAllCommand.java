package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.entity.EntityManager;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class KillAllCommand extends BaseCommand {
    public KillAllCommand(SpigotPlugin plugin) {
        super(plugin, "killall");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        EntityManager entityManager = plugin.getEntityManager();
        int mobCount = 0;
        for (RPGEntity entity : new ArrayList<>(entityManager.getEntities())) {
            if(!(entity instanceof RPGPlayer)) {
                entity.kill();
                mobCount ++;
            }
        }
        commandSender.sendMessage("Killed " + ChatColor.GREEN + mobCount +  ChatColor.RESET + " mobs");
        return true;
    }
}
