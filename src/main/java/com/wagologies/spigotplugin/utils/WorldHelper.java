package com.wagologies.spigotplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class WorldHelper {
    public static String CampaignTemplateName = "campaign";
    public static File getTemplatesFile() { return new File(Bukkit.getWorldContainer(), "templates");}
    public static World loadWorld(String worldName) {
        World loaded = Bukkit.getWorld(worldName);
        if(loaded != null) {
            return loaded;
        }
        File[] worldFiles = Bukkit.getServer().getWorldContainer().listFiles();
        if(worldFiles == null) {
            throw new RuntimeException("World files are missing!");
        }
        for (File worldFile : worldFiles) {
            if(worldFile.isDirectory() && worldFile.getName().equals(worldName)) {
                World newWorld = Bukkit.createWorld(new WorldCreator(worldName));
                if(newWorld == null) {
                    throw new RuntimeException("Failed to load world!");
                }
                return newWorld;
            }
        }
        throw new RuntimeException("World does not exist!");
    }

    public static World createCampaignWorld(String name) {
        File templateFile = new File(getTemplatesFile(), CampaignTemplateName);
        File newWorldFolderPath = new File(Bukkit.getWorldContainer(), name);
        copyFolder(templateFile.toPath(), newWorldFolderPath.toPath());
        World world = Bukkit.createWorld(new WorldCreator(name));
        if(world == null) {
            throw new RuntimeException("Failed to load world after copy!");
        }
        return world;
    }

    private static void copyFolder(Path from, Path to) {
        try (Stream<Path> stream = Files.walk(from)) {
            stream.forEach(source -> copyFile(source, to.resolve(from.relativize(source))));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void copyFile(Path from, Path to) {
        try {
            Files.copy(from, to);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
