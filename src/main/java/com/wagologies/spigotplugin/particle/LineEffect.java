package com.wagologies.spigotplugin.particle;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Location;
import org.bukkit.World;

public class LineEffect extends ParticleEffect {
    private final Location point1;
    private final Location point2;
    private final double distance;
    private final int pointsPerBlock;

    public LineEffect(SpigotPlugin plugin, Location point1, Location point2) {
        this(plugin, point1, point2, 15);
    }
    public LineEffect(SpigotPlugin plugin, Location point1, Location point2, int pointsPerBlock) {
        super(plugin);
        this.point1 = point1;
        this.point2 = point2;
        this.distance = point1.distance(point2);
        this.pointsPerBlock = pointsPerBlock;
    }

    @Override
    public void draw(Particle<?> particle, Location location) {
        World world = location.getWorld();
        assert world != null;
        int pointCount = (int) Math.round(distance * pointsPerBlock);
        for (int i = 0; i < pointCount; i++) {
            Location point = lerp(((float)i)/pointCount);
            spawnParticle(particle, point, world);
        }
    }

    private Location lerp(float inBetween) {
        return point1.clone().add(point2.clone().subtract(point1).multiply(inBetween));
    }
}
