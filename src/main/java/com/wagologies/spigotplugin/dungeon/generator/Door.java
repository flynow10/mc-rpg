package com.wagologies.spigotplugin.dungeon.generator;

import java.util.EnumSet;

public enum Door {
    NORTH, EAST, SOUTH, WEST;

    public static final EnumSet<Door> ALL_DOORS = EnumSet.allOf(Door.class);
    public static final EnumSet<Door> NO_DOORS = EnumSet.noneOf(Door.class);

    public static EnumSet<Door> RotateDoors90(EnumSet<Door> doors) {
        EnumSet<Door> result = EnumSet.noneOf(Door.class);
        for (Door door : doors) {
            if(door == NORTH) {
                result.add(EAST);
            }
            if(door == EAST) {
                result.add(SOUTH);
            }
            if(door == SOUTH) {
                result.add(WEST);
            }
            if(door == WEST) {
                result.add(NORTH);
            }
        }
        return result;
    }

    public static Room.Rotation RotationFromDoors(EnumSet<Door> roomDoors, EnumSet<Door> mazeRequirement) {
        if(roomDoors.equals(mazeRequirement)) {
            return Room.Rotation.NONE;
        }
        EnumSet<Door> doors = EnumSet.copyOf(roomDoors);
        for(Room.Rotation rotation : new Room.Rotation[] {Room.Rotation.DEG_90, Room.Rotation.DEG_180, Room.Rotation.DEG_270}) {
            doors = RotateDoors90(doors);
            if(doors.equals(mazeRequirement)) {
                return rotation;
            }
        }

        throw new RuntimeException("Room doors do not match the maze requirement");
    }

    public enum Configuration {
        ONE,
        TWO_TRANS,
        TWO_CIS,
        THREE,
        FOUR;

        public static Configuration ConfigurationFromDoors(EnumSet<Door> doors) {
            if(doors.size() == 1) {
                return Configuration.ONE;
            }
            if(doors.size() == 2) {
                if((doors.contains(Door.NORTH) && doors.contains(Door.SOUTH)) || (doors.contains(Door.EAST) && doors.contains(
                        Door.WEST))) {
                    return Configuration.TWO_TRANS;
                } else {
                    return Configuration.TWO_CIS;
                }
            }
            if(doors.size() == 3) {
                return Configuration.THREE;
            }
            return Configuration.FOUR;
        }
    }
}
