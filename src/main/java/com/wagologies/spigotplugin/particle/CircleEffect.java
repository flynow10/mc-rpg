package com.wagologies.spigotplugin.particle;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.utils.Quaternion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CircleEffect extends ParticleEffect {

    private double radius;
    private Quaternion rotation;
    private int particleCount;

    public CircleEffect(SpigotPlugin plugin, double radius) {
        this(plugin, radius, new Quaternion());
    }
    public CircleEffect(SpigotPlugin plugin, double radius, Quaternion rotation) {
        this(plugin, radius, rotation,200);
    }

    public CircleEffect(SpigotPlugin plugin, double radius, Quaternion rotation, int particleCount) {
        super(plugin);
        this.radius = radius;
        this.rotation = rotation;
        this.particleCount = particleCount;
    }

    @Override
    public void draw(Particle<?> particle, Location location) {
        World world = location.getWorld();
        assert world != null;


        for (double angle = 0; angle <= Math.PI * 2; angle += (Math.PI*2)/particleCount) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Vector point = new Vector(x, 0, z);
            Vector rotatedPoint = rotation.rotate(point);

            Vector worldPoint = location.toVector().add(rotatedPoint);
            spawnParticle(particle, worldPoint.toLocation(world), world);
        }
    }

    public double getRadius() {
        return radius;
    }

    public CircleEffect setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public CircleEffect setRotation(Quaternion rotation) {
        this.rotation = rotation;
        return this;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public CircleEffect setParticleCount(int particleCount) {
        this.particleCount = particleCount;
        return this;
    }
}
