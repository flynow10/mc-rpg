package com.wagologies.spigotplugin.dungeon.generator;

public record RoomTypeInfo(int floor, int maxPerDungeon) {
    public RoomTypeInfo(int floor) {
        this(floor, -1);
    }
}
