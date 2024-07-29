package com.wagologies.spigotplugin.campaign;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public enum PointOfInterest {
    ARENA_RESPAWN(626.5, 76, 895.5, 90, 0),
    NEW_CAMPAIGN(693.5, 65, 939.5, 180, 26);

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    PointOfInterest(double x, double y, double z) {
        this(x, y, z, 0.0f, 0.0f);
    }

    PointOfInterest(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }
}
