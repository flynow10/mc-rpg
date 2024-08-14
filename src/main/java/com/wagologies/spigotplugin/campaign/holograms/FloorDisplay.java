package com.wagologies.spigotplugin.campaign.holograms;

import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.PointOfInterest;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class FloorDisplay {
    private final Campaign campaign;
    private final Hologram hologram;

    public FloorDisplay(Campaign campaign) {
        this.campaign = campaign;
        hologram = DHAPI.createHologram("floor-display-" + campaign.getName(), PointOfInterest.FLOOR_DISPLAY_HOLOGRAM.toLocation(campaign.getWorld()));
        updateHologram();
    }

    public void updateHologram() {
        hologram.setDownOrigin(true);
        int lastCompleted = campaign.getLastCompletedFloor();
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.GREEN + "Castle Clearing Progress");
        lines.add((lastCompleted > Campaign.CASTLE_FLOOR_COUNT ? ChatColor.GREEN : ChatColor.RED) + "The Throne Room");
        for (int i = Campaign.CASTLE_FLOOR_COUNT; i >= 1; i--) {
            ChatColor color = i > lastCompleted ? ChatColor.GRAY : ChatColor.GREEN;
            lines.add(color + "Floor " + i);
        }
        DHAPI.setHologramLines(hologram, lines);
    }
}
