package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.mob.utils.RPGMobDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public abstract class AbstractMob implements Mob, Listener {
    protected int health = getMaxHealth();
    protected MobManager mobManager;


    public int getHealth() {
        return health;
    }

    public void damage(int damage) {
        RPGMobDamageEvent damageEvent = new RPGMobDamageEvent(this, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if(damageEvent.isCancelled()) {
            return;
        }
        damage = damageEvent.getDamage();
        if(damage != 0) {
            this.health = Math.max(0, this.health - damage);
        }

        if(this.health == 0) {
            onDeath();
        }
    }

    public String getHoverText() {
        return getName() + " " + ChatColor.RED + health + "/" + getMaxHealth();
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
