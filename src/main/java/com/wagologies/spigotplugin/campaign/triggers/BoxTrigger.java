package com.wagologies.spigotplugin.campaign.triggers;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.particle.LineEffect;
import com.wagologies.spigotplugin.particle.Particle;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BoxTrigger extends Trigger {
    private final BoundingBox box = new BoundingBox();

    public BoxTrigger(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
    }

    public void visualize() {
        Vector[] corners = new Vector[] {
                new Vector(box.getMaxX(), box.getMaxY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMinY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMinY(), box.getMinZ()),
                new Vector(box.getMaxX(), box.getMaxY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMaxY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMinY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMinY(), box.getMaxZ()),
                new Vector(box.getMinX(), box.getMaxY(), box.getMaxZ()),
        };
        List<LineEffect> lineEffects = new ArrayList<>();
        World campaignWorld = getCampaign().getWorld();
        for (int i = 0; i < corners.length; i++) {
            Vector corner1 = corners[i];
            Vector corner2 = corners[(i + 1) % corners.length];
            lineEffects.add(new LineEffect(getPlugin(), corner1.toLocation(campaignWorld), corner2.toLocation(campaignWorld), 3));
        }
        Vector[] missingLines = new Vector[] {
                new Vector(box.getMaxX(), box.getMaxY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMaxY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMaxY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMaxY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMinY(), box.getMaxZ()),
                new Vector(box.getMinX(), box.getMinY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMinY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMinY(), box.getMinZ()),

        };
        for (int i = 0; i < missingLines.length; i+=2) {
            Vector corner1 = missingLines[i];
            Vector corner2 = missingLines[(i + 1) % missingLines.length];
            lineEffects.add(new LineEffect(getPlugin(), corner1.toLocation(campaignWorld), corner2.toLocation(campaignWorld), 3));
        }
        org.bukkit.Particle.DustOptions dustOptions = new org.bukkit.Particle.DustOptions(Color.RED, 1f);
        Particle<org.bukkit.Particle.DustOptions> particle = new Particle<>(org.bukkit.Particle.REDSTONE, 1, dustOptions);
        for (LineEffect lineEffect : lineEffects) {
            lineEffect.drawForTicks(particle, campaignWorld.getSpawnLocation(), 100);
        }
    }

    @Override
    public boolean didEnter(Location from, Location to, RPGPlayer player) {
        return !box.contains(from.toVector()) && box.contains(to.toVector());
    }

    @Override
    public boolean didLeave(Location from, Location to, RPGPlayer player) {
        return box.contains(from.toVector()) && !box.contains(to.toVector());
    }

    public void setBoxSize(Location first, Location second) {
        setBoxSize(first.getX(), first.getY(), first.getZ(), second.getX(), second.getY(), second.getZ());
    }

    public void setBoxSize(double x1, double y1, double z1, double x2, double y2, double z2) {
        box.resize(x1, y1, z1, x2, y2, z2);
    }

    public BoundingBox getBox() {
        return box;
    }

    public BoxTrigger withBoxSize(Location first, Location second) {
        setBoxSize(first, second);
        return this;
    }
    public BoxTrigger withBoxSize(double x1, double y1, double z1, double x2, double y2, double z2) {
        setBoxSize(x1, y1, z1, x2, y2, z2);
        return this;
    }

    public BoxTrigger withActivateMultiple(boolean activateMultiple) {
        super.withActivateMultiple(activateMultiple);
        return this;
    }

    public BoxTrigger withCallback(Consumer<RPGPlayer> callback) {
        super.withCallback(callback);
        return this;
    }
}