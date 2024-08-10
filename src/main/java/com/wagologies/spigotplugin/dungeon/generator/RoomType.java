package com.wagologies.spigotplugin.dungeon.generator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.wagologies.spigotplugin.utils.Schematics;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.EnumSet;

public enum RoomType {
    BANQUET_HALL("banquet_hall", EnumSet.of(Door.WEST), new RoomTypeInfo(1)),
    BED_CHAMBERS("bed_chambers", EnumSet.of(Door.EAST), new RoomTypeInfo(1)),
    CORNER_HALLWAY("corner_hallway", EnumSet.of(Door.WEST, Door.NORTH), new RoomTypeInfo(1)),
    ICE_BRIDGES("ice_bridges", EnumSet.of(Door.NORTH, Door.SOUTH), new RoomTypeInfo(1, 2)),
    MAZE_HALLWAYS("maze_hallways", EnumSet.of(Door.EAST, Door.SOUTH, Door.WEST), new RoomTypeInfo(1)),
    STRAIGHT_HALLWAY("straight_hallway", EnumSet.of(Door.NORTH, Door.SOUTH), new RoomTypeInfo(1)),
    THREE_HALLWAYS("three_hallways", EnumSet.of(Door.NORTH, Door.EAST, Door.WEST), new RoomTypeInfo(1)),
    FOUR_HALLWAYS("four_hallways", Door.ALL_DOORS, new RoomTypeInfo(1));

    private final String schematicName;
    private final EnumSet<Door> doorDirections;
    private final RoomTypeInfo info;
    private final Door.Configuration configuration;

    RoomType(String schematicName, RoomTypeInfo info) {
        this(schematicName, Door.NO_DOORS, info);
    }

    RoomType(String schematicName, EnumSet<Door> doorDirections, RoomTypeInfo info) {
        this.schematicName = schematicName;
        this.doorDirections = doorDirections;
        this.info = info;
        this.configuration = Door.Configuration.ConfigurationFromDoors(doorDirections);
    }

    public String getSchematicName() {
        return schematicName;
    }

    public EnumSet<Door> getDoorDirections() {
        return doorDirections;
    }

    public RoomTypeInfo getInfo() {
        return info;
    }

    public Door.Configuration getConfiguration() {
        return configuration;
    }

    public Clipboard loadSchematic() {
        return Schematics.LoadSchematic(getSchematicName());
    }

    public static void saveSchematic(World world, Region region, String schematicName) {
        if(region.getHeight() != Room.ROOM_SIZE || region.getWidth() != Room.ROOM_SIZE || region.getLength() != Room.ROOM_SIZE) {
            throw new RuntimeException("Selected region was not the correct size for room.");
        }
        Schematics.SaveSchematic(world, region, schematicName);
    }

    private static File getSchematicsFolder() {
        return new File(Bukkit.getWorldContainer(), "schematics");
    }
}
