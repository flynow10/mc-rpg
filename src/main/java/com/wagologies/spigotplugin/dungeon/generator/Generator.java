package com.wagologies.spigotplugin.dungeon.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.utils.Schematics;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Generator {
    public static String ENTRY_ROOM_SCHEMATIC_NAME = "entry_room";
    private final SpigotPlugin plugin;
    private final int width, height, floor;
    private final Maze maze;
    private final Room[][] rooms;
    private final HashMap<RoomType, Integer> roomUses = new HashMap<>();

    public Generator(SpigotPlugin plugin, int width, int height, int floor) {
        this.plugin = plugin;
        this.width = width;
        this.height = height;
        this.floor = floor;
        this.maze = new Maze(width, height);
        rooms = new Room[height][width];
        populateRoomArray();
    }

    private void populateRoomArray() {
        Random random = new Random();
        for(int y = 0; y < this.height; y++) {
            for(int x = 0; x < this.width; x++) {
                EnumSet<Door> doors = this.maze.getDoorsAt(x, y);
                Room room = Room.CreateRoomFromDoors(x, y, doors, roomUses, floor, random);
                int numRoomUses = 0;
                if(roomUses.containsKey(room.getType())) {
                    numRoomUses = roomUses.get(room.getType());
                }
                roomUses.put(room.getType(), numRoomUses + 1);
                this.rooms[y][x] = room;
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

        pasteEntrySchematic(world, origin);
    }

    public void cleanupDungeon(World world, Location origin) {
        com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(world);
        BlockState airBlock = Objects.requireNonNull(BlockTypes.AIR).getDefaultState();
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldEditWorld)) {
            for(int z = 0; z < this.height; z++) {
                for(int x = 0; x < this.width; x++) {
                    BlockVector3 pos1 = BlockVector3.at(origin.getBlockX() + x * Room.ROOM_SIZE, origin.getBlockY(), origin.getBlockZ() + z * Room.ROOM_SIZE);
                    BlockVector3 pos2 = pos1.add(Room.ROOM_SIZE - 1, Room.ROOM_SIZE - 1, Room.ROOM_SIZE - 1);
                    Region deleteRegion = new CuboidRegion(pos1, pos2);
                    try {
                        editSession.setBlocks(deleteRegion, airBlock);
                    } catch (WorldEditException e) {
                        plugin.getLogger().warning("Failed to delete room at (" + x + ", " + z + "): " + e.getMessage());
                    }
                }
            }
        }
    }

    private void pasteEntrySchematic(World world, Location origin) {
        Clipboard clipboard = Schematics.LoadSchematic(Generator.ENTRY_ROOM_SCHEMATIC_NAME);
        BlockVector3 dimensions = clipboard.getDimensions();
        int x = origin.getBlockX();
        int z = origin.getBlockZ();
        x += width * Room.ROOM_SIZE / 2 - dimensions.getX() / 2;
        z -= dimensions.getZ();
        try {
            Schematics.PasteSchematic(new Vector(x, origin.getBlockY(), z), clipboard, world);
        } catch (WorldEditException e) {
            plugin.getLogger().warning("Failed to paste entry room at (" + x + ", " + z + "): " + e.getMessage());
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
