package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class CutsceneCommand extends PlayerCommand implements Listener {
    private final List<Player> activePlayers = new ArrayList<>();
    private ArmorStand camera;

    @Override
    public boolean unregister(CommandMap commandMap) {
        return super.unregister(commandMap);
    }

    public CutsceneCommand(SpigotPlugin plugin) {
        super(plugin, "cutscene");
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
////        World world = Bukkit.getWorld("world");
////        assert world != null;
////        camera = (ArmorStand) world.spawnEntity(new Location(world, 621, 191, 613, 208.9f, 3.7f), EntityType.ARMOR_STAND);
////        camera.setMarker(true);
////        camera.setVisible(false);
////        camera.setCustomNameVisible(true);
////        camera.setCustomName("Camera");
////        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
////            float yaw = camera.getLocation().getYaw();
////            float pitch = camera.getLocation().getPitch();
////            camera.setRotation(yaw + 1, pitch);
////        }, 0, 1);
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
//        if(activePlayers.contains(player)) {
//            activePlayers.remove(player);
//            player.setGameMode(GameMode.CREATIVE);
//            Entity spectatorTarget = player.getSpectatorTarget();
//            if(spectatorTarget != null && spectatorTarget.equals(camera)) {
//                player.setSpectatorTarget(null);
//            }
//            player.sendMessage("You have been removed from the test cutscene");
//        } else {
//            activePlayers.add(player);
//            player.setGameMode(GameMode.SPECTATOR);
//            player.setSpectatorTarget(camera);
//            player.sendMessage("You have been added to the test cutscene");
//        }
        return true;
    }

//    @EventHandler
//    public void playerLeaveSpectate(PlayerToggleSneakEvent event) {
//        if(activePlayers.contains(event.getPlayer())) {
//            event.setCancelled(true);
//        }
//    }
}
