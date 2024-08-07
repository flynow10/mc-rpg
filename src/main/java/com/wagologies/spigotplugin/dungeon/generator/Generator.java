package com.wagologies.spigotplugin.dungeon.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.EnumSet;
import java.util.Random;

public class Generator {
    private final SpigotPlugin plugin;
    private final int width, height;
    private final Maze maze;
    private final Room[][] rooms;

    public Generator(SpigotPlugin plugin, int width, int height) {
        this.plugin = plugin;
        this.width = width;
        this.height = height;
        this.maze = new Maze(width, height);
        rooms = new Room[height][width];
        populateRoomArray();
    }

    private void populateRoomArray() {
        Random random = new Random();
        for(int y = 0; y < this.height; y++) {
            for(int x = 0; x < this.width; x++) {
                EnumSet<Door> doors = this.maze.getDoorsAt(x, y);
                this.rooms[y][x] = Room.CreateRoomFromDoors(x, y, doors, random);
            }
        }
    }

    public void pasteDungeon(World world, Location origin) {
        com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(world);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldEditWorld)) {
            for(int y = 0; y < this.height; y++) {
                for(int x = 0; x < this.width; x++) {
                    Room room = this.rooms[y][x];
                    try {
                        room.pasteRoom(origin, editSession);
                    } catch (WorldEditException e) {
                        plugin.getLogger().warning("Failed to paste room at (" + x + ", " + y +"): " + e.getMessage());
                    }
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Maze getMaze() {
        return maze;
    }

    public Room[][] getRooms() {
        return rooms;
    }
}
