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

    private final List<BattleInfo> battleInfos = new ArrayList<>();

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

    public List<BattleInfo> getBattles() {
        return battleInfos;
    }

    protected SpigotPlugin getPlugin() {
        return plugin;
    }

    public BattleInfo createBattle(String name) {
        BattleInfo battleInfo = new BattleInfo();
        battleInfo.setBattleId(StringHelper.nanoId());
        battleInfo.setName(name);
        this.battleInfos.add(battleInfo);
        return battleInfo;
    }

    public BattleInfo getBattle(String name) {
        Optional<BattleInfo> optionalBattle =  battleInfos.stream().filter(battle -> battle.getName().equals(name)).findAny();
        if(optionalBattle.isEmpty()) {
            throw new RuntimeException("Could not find battle with this name!");
        }
        return optionalBattle.get();
    }

    public void saveBattles() {
        for (BattleInfo battleInfo : battleInfos) {
            battleConfig.set("battles." + battleInfo.getBattleId(), battleInfo);
        }
        try {
            battleConfig.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info("Successfully saved " + battleInfos.size() + " battles to disk");
    }


    public void loadBattles() {
        ConfigurationSection battleSection = battleConfig.getConfigurationSection("battles");
        if(battleSection == null) {
            return;
        }
        Set<String> battleIds = battleSection.getKeys(false);
        for (String battleId : battleIds) {
            BattleInfo battleInfo = (BattleInfo) battleSection.get(battleId);
            assert battleInfo != null;
            battleInfo.setBattleId(battleId);
            battleInfos.add(battleInfo);
        }
    }
}
