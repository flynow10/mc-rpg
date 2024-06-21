package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.event.CastSpellEvent;
import com.wagologies.spigotplugin.particle.CircleEffect;
import com.wagologies.spigotplugin.particle.Particle;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import com.wagologies.spigotplugin.spell.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;

public class AuraOfVitality extends BaseSpell implements Listener {
    final Particle<?> particle;
    final CircleEffect effect;
    final static int RADIUS = 7;
    public AuraOfVitality(SpellManager spellManager, RPGEntity spellCaster) {
        super(spellManager, spellCaster);
        effect = new CircleEffect(spellManager.getPlugin(), RADIUS, new Vector(0, 1, 0), 100);
        particle = new Particle<>(org.bukkit.Particle.SCRAPE, 0, new Vector(0, 10, 0), null);
        getSpellWorld().playSound(spellCaster.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
        Bukkit.getPluginManager().registerEvents(this, spellManager.getPlugin());
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 1200) {
            endSpell();
            return;
        }
        if(tickCount % 10 == 0) {
            int particleCount =  (int) Math.round((Math.random() * 40) + 30);
            effect.setParticleCount(particleCount);
            effect.draw(particle, getLocationBelowCaster());
            if(tickCount % 20 == 0) {
                for (int i = 0; i < particleCount; i++) {
                    double angle = ((double) i/particleCount) * 2 * Math.PI;
                    double speed = 10;
                    double x = Math.cos(angle) * speed;
                    double z = Math.sin(angle) * speed;
                    Particle<Object> fanParticle = new Particle<>(org.bukkit.Particle.SCRAPE, 0, new Vector(x, 0, z), null);
                    fanParticle.spawnParticle(getLocationBelowCaster().add(0, 0.1, 0), getSpellWorld());
                }
            }
        }
        if(tickCount % 30 == 0) {
            healOthers();
        }
    }

    public void healOthers() {
        boolean castByPlayer = spellCaster instanceof RPGPlayer;
        SpigotPlugin plugin = spellManager.getPlugin();
        List<? extends RPGEntity> entities = castByPlayer ? plugin.getPlayerManager().getPlayers() : plugin.getEntityManager().getEntities();
        for (RPGEntity entity : entities) {
            if(entity instanceof RPGPlayer && !castByPlayer) {
                continue;
            }
            if(entity.getLocation().distanceSquared(spellCaster.getLocation()) <= RADIUS * RADIUS) {
                heal(entity);
            }
        }
    }

    public void heal(RPGEntity entity) {
        int regenAmount = (int) Math.round(((float)entity.getMaxHealth()/60) * Math.exp(-((double) entity.getHealth()/1000)) * 1.5f);
        if(entity.getHealth() < entity.getMaxHealth()) {
            entity.setHealth(entity.getHealth() + regenAmount);
        }
    }


    public Location getLocationBelowCaster() {
        Location location = spellCaster.getLocation();
        for (int i = 0; i < 10; i++) {
            location.subtract(0, 1, 0);
            if(!location.getBlock().isEmpty()) {
                location.setY(Math.floor(location.getY() + 1));
                return location;
            }
        }
        return spellCaster.getLocation();
    }

    @EventHandler
    public void onCastSpell(CastSpellEvent event) {
        if(event.getSpellcaster().equals(spellCaster)) {
            if(spellCaster instanceof RPGPlayer player) {
                player.getPlayer().sendMessage(ChatColor.RED + "You broke concentration and your " + SpellType.AuraOfVitality.getColoredName() + ChatColor.RED + " dissipated!");
            }
            endSpell();
        }
    }

    @Override
    public void endSpell() {
        super.endSpell();
        HandlerList.unregisterAll(this);
    }
}
