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
        while(ticks > 3) {
            ticks -= 3;
            Bukkit.getScheduler().runTaskLater(plugin, () -> draw(particle, location), ticks);
        }
    }

    protected void spawnParticle(Particle<?> particle, Location location, World world) {
        Vector offset = particle.getOffset();
        world.spawnParticle(particle.getParticleType(), location, particle.getCount(), offset.getX(), offset.getY(), offset.getZ(), particle.getData());
    }
}
