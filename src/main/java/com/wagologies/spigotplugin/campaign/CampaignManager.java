package com.wagologies.spigotplugin.campaign;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.utils.StringHelper;
import com.wagologies.spigotplugin.utils.WorldHelper;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CampaignManager {
    private final File campaignDataDir;
    private final SpigotPlugin plugin;
    private final List<Campaign> campaigns = new ArrayList<>();
    public CampaignManager(SpigotPlugin plugin) {
        this.plugin = plugin;
        campaignDataDir = new File(plugin.getDataFolder(), "campaigns");
        checkDirExists();
        try {
            loadCampaigns();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info("Loaded Campaign Manager");
    }

    public void checkDirExists() {
        if(!campaignDataDir.exists()) {
            if(!campaignDataDir.mkdir()) {
                throw new RuntimeException("Failed to create campaigns directory");
            }
        }
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public Campaign createNewCampaign() {
        String name = StringHelper.nanoId();
        World campaignWorld = WorldHelper.createCampaignWorld(name);
        Campaign campaign = new Campaign(name, campaignWorld, new ArrayList<>(), null);
        campaign.setCampaignManager(this);
        campaign.initialize();
        campaigns.add(campaign);
        return campaign;
    }

    public SpigotPlugin getPlugin() {
        return plugin;
    }

    public void loadCampaigns() throws IOException {
        File[] campaignConfigFiles = campaignDataDir.listFiles();
        if (campaignConfigFiles == null) {
            throw new IOException("Failed to find campaign data files");
        }

        for (File file : campaignConfigFiles) {
            if(file.exists() && file.isFile() && file.getName().endsWith(".yml")) {
                YamlConfiguration campaignConfig = YamlConfiguration.loadConfiguration(file);
                Campaign deserialized = (Campaign) campaignConfig.get("data");
                if(deserialized == null) {
                    throw new IllegalStateException("Campaign file was malformed!");
                }
                deserialized.setCampaignManager(this);
                deserialized.initialize();
                campaigns.add(deserialized);
            }
        }
    }

    public void saveCampaigns() {
        checkDirExists();
        for (Campaign campaign : campaigns) {
            try {
                File dataFile = new File(campaignDataDir, campaign.getName() + ".yml");
                if(!dataFile.exists()) {
                    dataFile.createNewFile();
                }
                YamlConfiguration configuration = new YamlConfiguration();
                configuration.set("data", campaign);
                configuration.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().severe(e.getMessage());
            }
        }
    }
}
