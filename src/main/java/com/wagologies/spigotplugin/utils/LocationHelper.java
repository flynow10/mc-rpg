package com.wagologies.spigotplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class LocationHelper {
    public static List<Location> getNearbySpawnLocations(Location center, float radius, int minSpawnHeight, int maxHeightDiff) {
        World world = center.getWorld();
        assert world != null;
        int middleX = center.getBlockX();
        int middleZ = center.getBlockZ();
        int middleY = center.getBlockY();

        List<Location> validLocations = new ArrayList<>();

        for (int x = Math.round(middleX - radius); x <= Math.round(middleX + radius); x++) {
            for (int z = Math.round(middleZ - radius); z <= Math.round(middleZ + radius); z++) {
                if((Math.pow(x - middleX,2) + Math.pow(z - middleZ, 2)) < radius * radius) {
                    for (int y = middleY - maxHeightDiff; y <= middleY + maxHeightDiff; y++) {
                        if(isValidSpawnLocation(world, x, y, z, minSpawnHeight)) {
                            validLocations.add(new Location(world, x, y, z));
                            break;
                        }
                    }
                }
            }
        }

        return validLocations;
    }

    public static boolean isValidSpawnLocation(World world, int x, int y, int z, int minSpawnHeight) {
        for (int blockY = y - 1; blockY < y + minSpawnHeight; blockY++) {
            Block block = world.getBlockAt(x, blockY, z);
            if(block.isPassable() == (blockY == y - 1)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static <T> T getClosest(double x, double y, double z, List<T> entities, Function<T, Vector> converter) {
        return getClosest(new Vector(x, y, z), entities, converter, null);
    }

    @Nullable
    public static <T> T getClosest(double x, double y, double z, List<T> entities, Function<T, Vector> converter, @Nullable Predicate<T> targetConditions) {
        return getClosest(new Vector(x, y, z), entities, converter, targetConditions);
    }

    @Nullable
    public static <T> T getClosest(Vector point, List<T> entities, Function<T, Vector> converter, @Nullable Predicate<T> targetConditions) {
        if(entities.isEmpty()) {
            return null;
        }

        T closest = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            if(targetConditions != null && !targetConditions.test(entity)) {
                continue;
            }
            double distanceSq = converter.apply(entity).distanceSquared(point);
            if(distanceSq < closestDistanceSq) {
                closest = entity;
                closestDistanceSq = distanceSq;
            }
        }

        return closest;
    }
}
