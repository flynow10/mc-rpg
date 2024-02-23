package com.wagologies.spigotplugin.particle;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CircleEffect extends ParticleEffect {

    private final int radius;
    private final Vector normal;
    private final int particleCount;
    public CircleEffect(SpigotPlugin plugin, int radius, Vector normal) {
        this(plugin, radius, normal, 200);
    }

    public CircleEffect(SpigotPlugin plugin, int radius, Vector normal, int particleCount) {
        super(plugin);
        this.radius = radius;
        this.normal = normal.normalize();
        this.particleCount = particleCount;
    }

    @Override
    public void draw(Particle<?> particle, Location location) {
        World world = location.getWorld();
        assert world != null;
        Vector yAxis = new Vector(0, 1, 0);
        Vector rotateAxis = normal.getCrossProduct(yAxis);
        double rotateAngle = normal.angle(yAxis);
        for (double angle = 0; angle <= Math.PI * 2; angle += (Math.PI*2)/particleCount) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Vector point = location.toVector().add(new Vector(x, 0, z)).rotateAroundAxis(rotateAxis, rotateAngle);
            spawnParticle(particle, point.toLocation(world), world);
        }
    }
}
