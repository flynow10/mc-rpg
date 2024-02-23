package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.event.DamageMobByPlayer;
import com.wagologies.spigotplugin.event.DamageMobEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class AbstractMob implements Mob, Listener {
    protected int health = getMaxHealth();
    protected MobManager mobManager;

    public int getHealth() {
        return health;
    }

    public boolean damage(int damage) {
        if(damage != 0) {
            this.health = Math.max(0, this.health - damage);
        }

        if(this.health == 0) {
            onDeath();
            return true;
        }
        return false;
    }

    public String getHoverText() {
        return getName() + " " + ChatColor.RED + health + "/" + getMaxHealth();
    }

    protected DamageMobEvent createDamageEvent(EntityDamageEvent baseDamageEvent) {
        int initialDamage = 0;
        DamageMobEvent damageEvent = null;
        if(baseDamageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            Entity attacker = damageByEntityEvent.getDamager();
            if(attacker instanceof Player) {
                RPGPlayer rpgPlayer = mobManager.getPlugin().getPlayerManager().getPlayer((Player) attacker);
                if(rpgPlayer != null) {
                    damageEvent = new DamageMobByPlayer(rpgPlayer, this, initialDamage, baseDamageEvent);
                }
            }
        }

        if(damageEvent == null) {
            damageEvent = new DamageMobEvent(this, initialDamage, baseDamageEvent);
        }
        return damageEvent;
    }

    @Override
    public void die() {
        this.health = 0;
        onDeath();
    }

    public MobManager getMobManager() {
        return mobManager;
    }

    @Override
    public void setMobManager(MobManager mobManager) {
        this.mobManager = mobManager;
    }

    public void onDeath() {
        mobManager.onMobRemove(this);
    }

    @Override
    public void remove() {
        mobManager.onMobRemove(this);
    }
}
