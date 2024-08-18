package com.wagologies.spigotplugin.campaign.triggers;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Location;

import java.util.List;

public class CompositeTrigger extends Trigger {
    private final List<Trigger> triggers;

    public CompositeTrigger(SpigotPlugin plugin, Campaign campaign, Trigger... triggers) {
        super(plugin, campaign);
        this.triggers = List.of(triggers);
    }

    @Override
    public boolean didEnter(Location from, Location to, RPGPlayer player) {
        boolean entered = false;
        for (Trigger trigger : triggers) {
            if(trigger.getContainedPlayers().contains(player)) {
                return false;
            }
            if (trigger.didEnter(from, to, player)) {
                entered = true;
            }
        }
        return entered;
    }

    @Override
    public boolean didLeave(Location from, Location to, RPGPlayer player) {
        for (Trigger trigger : triggers) {
            if(!trigger.didLeave(from, to, player) && trigger.getContainedPlayers().contains(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void visualize() {
        for (Trigger trigger : triggers) {
            trigger.visualize();
        }
    }
}
