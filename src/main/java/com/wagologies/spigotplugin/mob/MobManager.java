package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private final SpigotPlugin plugin;
    private List<Mob> mobs = new ArrayList<>();

    public MobManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loaded Mob Manager");
    }
    public void spawn(Mob mob, Location location) {
        if(mobs.contains(mob)) {
            return;
        }
        mob.setMobManager(this);
        mob.spawn(location);
        if(mob instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) mob, this.plugin);
        }
        mobs.add(mob);
        plugin.getLogger().info("Created new " + mob.getClass().getSimpleName() + " at " + location.toString());
    }

    public int killAll() {
        int mobCount = mobs.size();
        for (Mob mob : mobs) {
            mob.remove();
        }
        mobs.clear();
        plugin.getLogger().info(String.format("Removed %d mobs", mobCount));
        return mobCount;
    }

    public List<Mob> getMobs() {
        return mobs;
    }

    public void onMobRemove(Mob mob) {
        mobs.remove(mob);
    }

    public SpigotPlugin getPlugin() {
        return plugin;
    }
}
