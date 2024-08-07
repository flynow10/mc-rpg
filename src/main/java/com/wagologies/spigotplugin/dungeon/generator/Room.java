package com.wagologies.spigotplugin.dungeon.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Room {
    public static final int ROOM_SIZE = 50;
    private final int x;
    private final int y;
    private final RoomType type;
    private final Rotation rotation;

    public Room(int x, int y, RoomType type, Rotation rotation) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.rotation = rotation;
    }

    public void pasteRoom(Location origin, EditSession editSession) throws WorldEditException {
        Location roomLocation = origin.clone().add(this.x * Room.ROOM_SIZE, 0, this.y * Room.ROOM_SIZE);
        Clipboard clipboard = this.type.loadSchematic();
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        int x = roomLocation.getBlockX();
        int z = roomLocation.getBlockZ();
        if(this.rotation == Rotation.DEG_90) {
            x += ROOM_SIZE - 1;
        } else if(this.rotation == Rotation.DEG_180) {
            x += ROOM_SIZE - 1;
            z += ROOM_SIZE - 1;
        } else if(this.rotation == Rotation.DEG_270) {
            z += ROOM_SIZE - 1;
        }
        clipboardHolder.setTransform(new AffineTransform().rotateY(360 - this.rotation.getDegrees()));
        System.out.println(this + ", x: " + x + ", z: " + z);
        Operation operation = clipboardHolder
                .createPaste(editSession)
                .to(BlockVector3.at(x, roomLocation.getBlockY(), z))
                .copyEntities(true)
                .build();
        Operations.complete(operation);
    }

    public static Room CreateRoomFromDoors(int x, int y, EnumSet<Door> doors) {
        return CreateRoomFromDoors(x, y, doors, new Random());
    }

    public static Room CreateRoomFromDoors(int x, int y, EnumSet<Door> doors, Random random) {
        Door.Configuration configuration = Door.Configuration.ConfigurationFromDoors(doors);
        List<Room> rooms = new ArrayList<>();
        for (RoomType roomType : RoomType.values()) {
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
