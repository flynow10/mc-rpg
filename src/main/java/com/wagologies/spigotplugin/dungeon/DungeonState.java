package com.wagologies.spigotplugin.dungeon;

public enum DungeonState {
    PreStart,
    AwaitingPaste,
    FinishedSetup,
    Running,
    Failed,
    Succeeded,
    CleanedUp;

    public boolean isFinished() {
        return this == DungeonState.Failed || this == DungeonState.Succeeded || this == DungeonState.CleanedUp;
    }

    public boolean pastSetup() {
        return this.isFinished() || this == DungeonState.FinishedSetup || this == DungeonState.Running;
    }
}
