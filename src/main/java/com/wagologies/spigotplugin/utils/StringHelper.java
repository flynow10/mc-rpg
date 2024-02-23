package com.wagologies.spigotplugin.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Location;

public class StringHelper {
    public static String nanoId() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public static String locationToString(Location location) {
        return String.format("%.2f %.2f %.2f", location.getX(), location.getY(), location.getZ());
    }
}
