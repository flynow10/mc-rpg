package com.wagologies.spigotplugin.particle;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public abstract class ParticleEffect {

    private final SpigotPlugin plugin;
    public ParticleEffect(SpigotPlugin plugin) {
        this.plugin = plugin;
    }
    public abstract void draw(Particle<?> particle, Location location);

    public void drawForTicks(Particle<?> particle, Location location, int ticks) {
        this.drawForTicks(particle, location, ticks, 3);
    }
    public void drawForTicks(Particle<?> particle, Location location, int ticks, int spacing) {
        while(ticks > spacing) {
            ticks -= spacing;
            Bukkit.getScheduler().runTaskLater(plugin, () -> draw(particle, location), ticks);
        }
    }

    protected void spawnParticle(Particle<?> particle, Location location, World world) {
        particle.spawnParticle(location, world);
    }
}
