package com.wagologies.spigotplugin.dungeon;

import com.wagologies.spigotplugin.mob.MobType;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class SpawnerInfo {
    private int x;
    private int y;
    private int z;
    private MobType mobType;
    private int mobCount;

    public SpawnerInfo(Vector vector, MobType mobType, int mobCount) {
        this(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), mobType, mobCount);
    }

    public SpawnerInfo(Location location, MobType mobType, int mobCount) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ(), mobType, mobCount);
    }

    public SpawnerInfo(int x, int y, int z, MobType mobType, int mobCount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mobType = mobType;
        this.mobCount = mobCount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Vector getLocation() {
        return new Vector(x, y, z);
    }

    public MobType getMobType() {
        return mobType;
    }

    public int getMobCount() {
        return mobCount;
    }

    @Override
    public String toString() {
        return "SpawnerInfo { x=" + x + ", y=" + y + ", z=" + z + ", mobType=" + mobType + ", mobCount=" + mobCount + "}";
    }
}
