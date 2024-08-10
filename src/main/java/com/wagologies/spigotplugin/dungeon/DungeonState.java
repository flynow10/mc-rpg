package com.wagologies.spigotplugin.dungeon;

public enum DungeonState {
    PreStart,
    Running,
    Failed,
    Succeeded;

    public boolean isFinished() {
        return this == DungeonState.Failed || this == DungeonState.Succeeded;
    }
}
