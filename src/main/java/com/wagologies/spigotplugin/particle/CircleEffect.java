package com.wagologies.spigotplugin.particle;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CircleEffect extends ParticleEffect {

    private double radius;
    private Vector normal;
    private int particleCount;

    public CircleEffect(SpigotPlugin plugin, double radius) {
        this(plugin, radius, new Vector());
    }
    public CircleEffect(SpigotPlugin plugin, double radius, Vector normal) {
        this(plugin, radius, normal, 200);
    }

    public CircleEffect(SpigotPlugin plugin, double radius, Vector normal, int particleCount) {
        super(plugin);
        this.radius = radius;
        if(normal.isZero()) {
            this.normal = normal.clone();
        } else {
            this.normal = normal.clone().normalize().setY(-normal.getY());
        }
        this.particleCount = particleCount;
    }

    @Override
    public void draw(Particle<?> particle, Location location) {
        World world = location.getWorld();
        assert world != null;
        Vector yAxis = new Vector(0, -1, 0);
        Vector rotateAxis = normal.getCrossProduct(yAxis);
        double rotateAngle = normal.angle(yAxis);
        for (double angle = 0; angle <= Math.PI * 2; angle += (Math.PI*2)/particleCount) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Vector point = new Vector(x, 0, z);
            if(!normal.isZero() && !rotateAxis.isZero()) {
                point = point.rotateAroundAxis(rotateAxis, rotateAngle);
            }
            Vector worldPoint = location.toVector().add(point);
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

    public Vector getNormal() {
        return normal;
    }

    public CircleEffect setNormal(Vector normal) {
        this.normal = normal;
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
