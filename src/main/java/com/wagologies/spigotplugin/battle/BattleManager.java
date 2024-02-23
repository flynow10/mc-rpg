package com.wagologies.spigotplugin.battle;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BattleManager {
    private final FileConfiguration battleConfig;
    private final File configFile;
    private final SpigotPlugin plugin;

    private final List<Battle> battles = new ArrayList<>();

    public BattleManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "battles.yml");
        battleConfig = YamlConfiguration.loadConfiguration(configFile);
        try {
            loadBattles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info("Loaded Battle Manager");
    }

    public List<Battle> getBattles() {
        return battles;
    }

    protected SpigotPlugin getPlugin() {
        return plugin;
    }

    public Battle createBattle(String name) {
        Battle battle = new Battle();
        battle.setBattleId(StringHelper.nanoId());
        battle.setName(name);
        this.battles.add(battle);
        return battle;
    }

    public Battle getBattle(String name) {
        Optional<Battle> optionalBattle =  battles.stream().filter(battle -> battle.getName().equals(name)).findAny();
        if(optionalBattle.isEmpty()) {
            throw new RuntimeException("Could not find battle with this name!");
        }
        return optionalBattle.get();
    }

    public void saveBattles() {
        for (Battle battle : battles) {
            battleConfig.set("battles." + battle.getBattleId(), battle);
        }
        try {
            battleConfig.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info("Successfully saved " + battles.size() + " battles to disk");
    }


    public void loadBattles() {
        ConfigurationSection battleSection = battleConfig.getConfigurationSection("battles");
        if(battleSection == null) {
            return;
        }
        Set<String> battleIds = battleSection.getKeys(false);
        for (String battleId : battleIds) {
            Battle battle = (Battle) battleSection.get(battleId);
            assert battle != null;
            battle.setBattleId(battleId);
            battle.setBattleManager(this);
            battles.add(battle);
        }
    }
}
