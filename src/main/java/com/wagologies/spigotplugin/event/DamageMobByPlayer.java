package com.wagologies.spigotplugin.event;

import com.wagologies.spigotplugin.mob.Mob;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageMobByPlayer extends DamageMobEvent {
    private final RPGPlayer player;
    public DamageMobByPlayer(RPGPlayer player, Mob mob, int damage, EntityDamageEvent baseEvent) {
        super(mob, damage, baseEvent);
        this.player = player;
    }

    public RPGPlayer getPlayer() {
        return player;
    }
}
