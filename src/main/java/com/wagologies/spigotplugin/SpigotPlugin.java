package com.wagologies.spigotplugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.reflect.ClassPath;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.mob.MobManager;
import com.wagologies.spigotplugin.mob.utils.CustomEntityType;
import com.wagologies.spigotplugin.player.PlayerManager;
import com.wagologies.spigotplugin.utils.ActionBar;
import com.wagologies.spigotplugin.utils.WeatherManager;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class SpigotPlugin extends JavaPlugin {

    private PlayerManager playerManager;
    private MobManager mobManager;
    private ActionBar actionBar;
    private WeatherManager weatherManager;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        playerManager = new PlayerManager(this);
        mobManager = new MobManager(this);
        actionBar = new ActionBar(this);
        weatherManager = new WeatherManager(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        CustomEntityType.registerEntities();

        try {
            BaseCommand.registerAllCommands(this);
        } catch (IOException e) {
            getLogger().warning("Failed to register commands!");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
        mobManager.killAll();

        CustomEntityType.unregisterEntities();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public MobManager getMobManager() {
        return mobManager;
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
