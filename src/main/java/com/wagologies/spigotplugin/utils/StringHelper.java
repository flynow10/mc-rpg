package com.wagologies.spigotplugin.utils;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringHelper {
    public static String nanoId() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public static String locationToString(Location location) {
        return String.format("%.2f %.2f %.2f", location.getX(), location.getY(), location.getZ());
    }

    public static List<String> wrapItemLore(String lore) {
        return wrapItemLore(lore, 25);
    }

    public static List<String> wrapItemLore(String lore, int wrapLength) {
        return Arrays.stream(WordUtils.wrap(lore, wrapLength, "\n", true).split("\n")).toList();
    }

    public static List<String> prependWithReset(List<String> lore) {
        return prependWithColor(lore, "", true);
    }
    public static List<String> prependWithColor(List<String> lore, String color) {
        return prependWithColor(lore, color, true);
    }

    public static List<String> prependWithColor(List<String> lore, String color, boolean addReset) {
        String fullColor = (addReset ? ChatColor.RESET.toString() : "") + color;
        List<String> prependedLore = new ArrayList<>();
        for (String line : lore) {
            prependedLore.add(fullColor + line);
        }
        return prependedLore;
    }

    public static String enumToHumanName(Enum<?> enumType) {
        String humanName = enumType.name().toLowerCase();
        humanName = humanName.substring(0, 1).toUpperCase() + humanName.substring(1);
        humanName = humanName.replace("_", " ");
        return humanName;
    }
}
