package com.wagologies.spigotplugin.command;

import com.google.common.reflect.ClassPath;
import com.wagologies.spigotplugin.SpigotPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

import java.io.IOException;
import java.lang.reflect.Constructor;

public abstract class BaseCommand extends Command {

    protected final SpigotPlugin plugin;
    public BaseCommand(SpigotPlugin plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    public static void registerAllCommands(SpigotPlugin plugin) throws IOException {
        ClassPath cp = ClassPath.from(plugin.getClass().getClassLoader());
        cp.getTopLevelClassesRecursive("com.wagologies.spigotplugin.command.commands").forEach(classInfo -> {
            try {
                Constructor<?> c = Class.forName(classInfo.getName()).getDeclaredConstructor(SpigotPlugin.class);
                Object obj = c.newInstance(plugin);
                if (obj instanceof Command) {
                    Command cmd = (Command) obj;
                    registerCommand(cmd, plugin);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to register " + classInfo.getName());
                throw new RuntimeException(e);
            }
        });
    }

    static void registerCommand(Command cmd, SpigotPlugin plugin) {
        SimpleCommandMap simpleCommandMap = ((CraftServer) plugin.getServer()).getCommandMap();
        simpleCommandMap.register(plugin.getDescription().getName(), cmd);
    }
}
