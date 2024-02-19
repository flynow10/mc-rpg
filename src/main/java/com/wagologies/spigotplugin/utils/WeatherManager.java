package com.wagologies.spigotplugin.utils;

import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherManager implements Listener {

    private final SpigotPlugin plugin;

    public WeatherManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        plugin.getLogger().info("Loaded Weather Manager");
    }
    @EventHandler
    public void onChangeWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
        plugin.getServer().getWorlds().forEach(world -> {world.setWeatherDuration(0); world.setThundering(false);});
    }
}
