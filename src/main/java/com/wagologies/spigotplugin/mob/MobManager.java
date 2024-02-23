package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private final SpigotPlugin plugin;
    private List<Mob> mobs = new ArrayList<>();

    public MobManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loaded Mob Manager");
    }

    public Mob spawn(MobType mobType, Location location) {
        return spawn(mobType.getMobClass(), location);
    }

    public Mob spawn(Class<? extends Mob> mobClass, Location location) {
        try {
            Mob mob = mobClass.getDeclaredConstructor().newInstance();
            spawn(mob, location);
            return mob;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
        for (Mob mob : new ArrayList<>(mobs)) {
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
