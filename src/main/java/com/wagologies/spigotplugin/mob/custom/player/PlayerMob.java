package com.wagologies.spigotplugin.mob.custom.player;

import com.wagologies.spigotplugin.mob.AbstractMob;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;

public abstract class PlayerMob extends AbstractMob {
    private NPC npc;

//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//        if(player.equals(targetPlayer)) {
//            if(player.getLocation().distanceSquared(npc.getStoredLocation()) > 4) {
//                Location targetLocation = npc.getStoredLocation().subtract(player.getLocation()).toVector().normalize().multiply(3).toLocation(player.getWorld()).add(player.getLocation());
//                npc.getNavigator().setTarget(targetLocation);
//            }
//        }
//    }
}
