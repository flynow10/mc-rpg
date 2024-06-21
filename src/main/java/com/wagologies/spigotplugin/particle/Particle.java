package com.wagologies.spigotplugin.particle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Particle<T> {
    private final org.bukkit.Particle particleType;
    private final int count;
    private final Vector offset;
    private final T data;

    public Particle(org.bukkit.Particle particleType, int count) {
        this(particleType, count, null);
    }

    public Particle(org.bukkit.Particle particleType, int count, T data) {
        this(particleType, count, new Vector(), data);
    }

    public Particle(org.bukkit.Particle particleType, int count, Vector offset, T data) {
        this.particleType = particleType;
        this.count = count;
        this.offset = offset;
        this.data = data;
    }

    public org.bukkit.Particle getParticleType() {
        return particleType;
    }

    public int getCount() {
        return count;
    }

    public Vector getOffset() {
        return offset;
    }

    public T getData() {
        return data;
    }
    public void spawnParticle(Location location, World world) {
        Vector offset = this.getOffset();
        world.spawnParticle(this.getParticleType(), location, this.getCount(), offset.getX(), offset.getY(), offset.getZ(), this.getData());
    }
}
