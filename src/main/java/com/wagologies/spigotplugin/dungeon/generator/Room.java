package com.wagologies.spigotplugin.dungeon.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.dungeon.SpawnerInfo;
import com.wagologies.spigotplugin.entity.EntityManager;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.utils.Schematics;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;

public class Room {
    public static final int ROOM_SIZE = 50;
    private final int x;
    private final int y;
    private final RoomType type;
    private final Rotation rotation;
    private List<SpawnerInfo> spawners = new ArrayList<>();

    public Room(int x, int y, RoomType type, Rotation rotation) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.rotation = rotation;
    }

    public void pasteRoom(Location origin, EditSession editSession) throws WorldEditException {
        List<SpawnerInfo> spawnerInfos = new ArrayList<>();
        try {
             spawnerInfos = Schematics.ReadSchematic(this.type.getSchematicName());
        } catch (IOException e) {
            System.out.println("Failed to read schematic: " + Arrays.toString(e.getStackTrace()));
        }
        Location roomLocation = getRoomLocation(origin);
        Clipboard clipboard = this.type.loadSchematic();

        AffineTransform transform = new AffineTransform().rotateY(360 - this.rotation.getDegrees());
        for (SpawnerInfo info : spawnerInfos) {
            clipboard.setBlock(clipboard.getMinimumPoint().add(BlockVector3.at(info.getX(), info.getY(), info.getZ())), Objects.requireNonNull(
                    BlockTypes.AIR).getDefaultState());
            Vector3 worldEditVector = transform.apply(Vector3.at(info.getX(), info.getY(), info.getZ()));
            spawners.add(new SpawnerInfo((int) worldEditVector.x(), (int) worldEditVector.y(),
                    (int) worldEditVector.z(), info.getMobType(), info.getMobCount()));
        }
        Schematics.PasteSchematic(new Vector(roomLocation.getBlockX(), roomLocation.getBlockY(), roomLocation.getBlockZ()), clipboard, transform, editSession);
    }

    public List<RPGEntity> spawnMobs(SpigotPlugin plugin, Location origin) {
        EntityManager em = plugin.getEntityManager();
        List<RPGEntity> entities = new ArrayList<>();
        for (SpawnerInfo info : spawners) {
            Location worldPosition = getRoomLocation(origin).add(info.getLocation());
            for (int i = 0; i < info.getMobCount(); i++) {
                entities.add(em.spawn(info.getMobType(), worldPosition));
            }
        }
        return entities;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public RoomType getType() {
        return type;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Location getRoomLocation(Location origin) {
        Location roomLocation = origin.clone().add(this.x * Room.ROOM_SIZE, 0, this.y * Room.ROOM_SIZE);
        if(this.rotation == Rotation.DEG_90) {
            roomLocation.add(ROOM_SIZE - 1, 0 ,0);
        } else if(this.rotation == Rotation.DEG_180) {
            roomLocation.add(ROOM_SIZE - 1, 0 ,ROOM_SIZE - 1);

        } else if(this.rotation == Rotation.DEG_270) {
            roomLocation.add(0, 0, ROOM_SIZE - 1);
        }
        return roomLocation;
    }

    public static Room CreateRoomFromDoors(int x, int y, EnumSet<Door> doors, HashMap<RoomType, Integer> roomUses, int floor) {
        return CreateRoomFromDoors(x, y, doors, roomUses, floor, new Random());
    }

    public static Room CreateRoomFromDoors(int x, int y, EnumSet<Door> doors, HashMap<RoomType, Integer> roomUses, int floor, Random random) {
        Door.Configuration configuration = Door.Configuration.ConfigurationFromDoors(doors);
        List<Room> rooms = new ArrayList<>();
        for (RoomType roomType : RoomType.values()) {
            RoomTypeInfo roomInfo = roomType.getInfo();
            if(roomInfo.floor() != -1 && roomInfo.floor() != floor) {
                continue;
            }
            if(roomUses.containsKey(roomType) && roomInfo.maxPerDungeon() != -1) {
                if(roomUses.get(roomType) >= roomInfo.maxPerDungeon()) {
                    continue;
                }
            }
            if(roomType.getConfiguration().equals(configuration)) {
                Rotation rotation = Door.RotationFromDoors(roomType.getDoorDirections(), doors);
                rooms.add(new Room(x, y, roomType, rotation));
            }
        }

        if(rooms.isEmpty()) {
            throw new IllegalArgumentException("There is no room type to match this door configuration");
        }

        return rooms.get(random.nextInt(rooms.size()));
    }

    @Override
    public String toString() {
        return "Room{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                ", rotation=" + rotation +
                '}';
    }

    public enum Rotation {
        NONE(0),
        DEG_90(90),
        DEG_180(180),
        DEG_270(270);

        private final int degrees;

        Rotation(int degrees) {
            this.degrees = degrees;
        }

        public int getDegrees() {
            return degrees;
        }
    }
}
