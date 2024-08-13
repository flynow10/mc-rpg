package com.wagologies.spigotplugin.dungeon;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DungeonManager {
    private final SpigotPlugin plugin;
    private final List<Dungeon> dungeons = new ArrayList<>();

    public DungeonManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loaded Dungeon Manager");
    }

    public Dungeon getDungeon(Campaign campaign) {
        Optional<Dungeon> dungeon = dungeons.stream().filter(d -> d.getCampaign().equals(campaign)).findFirst();
        return dungeon.orElse(null);
    }

    public Dungeon createDungeon(Campaign campaign, int floor) {
        if(getDungeon(campaign) != null) {
            throw new RuntimeException("Cannot create a dungeon with the same campaign");
        }
        Dungeon dungeon = new Dungeon(plugin, this, campaign, floor);
        dungeons.add(dungeon);
        return dungeon;
    }

    protected void removeDungeon(Dungeon dungeon) {
        dungeons.remove(dungeon);
    }

    public void cleanupDungeons() {
        for (Dungeon dungeon : new ArrayList<>(dungeons)) {
            dungeon.cleanup();
        }
    }
}
