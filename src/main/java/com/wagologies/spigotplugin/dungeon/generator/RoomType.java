package com.wagologies.spigotplugin.dungeon.generator;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;

public enum RoomType {
    BANQUET_HALL("banquet_hall", EnumSet.of(Door.WEST), 1),
    CORNER_HALLWAY("corner_hallway", EnumSet.of(Door.WEST, Door.NORTH), 1),
    ICE_BRIDGES("ice_bridges", EnumSet.of(Door.NORTH, Door.SOUTH), 1),
    MAZE_HALLWAYS("maze_hallways", EnumSet.of(Door.EAST, Door.SOUTH, Door.WEST), 1);

    private final String schematicName;
    private final EnumSet<Door> doorDirections;
    private final int floor;
    private final Door.Configuration configuration;

    RoomType(String schematicName, int floor) {
        this(schematicName, Door.NO_DOORS, floor);
    }

    RoomType(String schematicName, EnumSet<Door> doorDirections, int floor) {
        this.schematicName = schematicName;
        this.doorDirections = doorDirections;
        this.floor = floor;
        this.configuration = Door.Configuration.ConfigurationFromDoors(doorDirections);
    }

    public String getSchematicName() {
        return schematicName;
    }

    public EnumSet<Door> getDoorDirections() {
        return doorDirections;
    }

    public int getFloor() {
        return floor;
    }

    public Door.Configuration getConfiguration() {
        return configuration;
    }

    public Clipboard loadSchematic() {
        File schematicFile = new File(getSchematicsFolder(), getSchematicName() + ".schem");
        if(!schematicFile.exists()) {
            throw new IllegalStateException("Schematic file " + schematicFile + " does not exist");
        }
        if(!schematicFile.canRead()) {
            throw new IllegalStateException("Schematic file " + schematicFile + " is not readable");
        }

        Clipboard clipboard;

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
            clipboard = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return clipboard;
    }

    public static void saveSchematic(World world, Region region, String schematicName) {
        if(region.getHeight() != Room.ROOM_SIZE || region.getWidth() != Room.ROOM_SIZE || region.getLength() != Room.ROOM_SIZE) {
            throw new RuntimeException("Selected region was not the correct size for room.");
        }
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(world);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(worldEditWorld, region, clipboard, region.getMinimumPoint());
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        File schematicFile = new File(getSchematicsFolder(), schematicName + ".schem");
        try(ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicFile))) {
            writer.write(clipboard);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File getSchematicsFolder() {
        return new File(Bukkit.getWorldContainer(), "schematics");
    }
}
