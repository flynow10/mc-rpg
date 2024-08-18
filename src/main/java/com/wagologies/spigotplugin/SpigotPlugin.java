package com.wagologies.spigotplugin;

import com.samjakob.spigui.SpiGUI;
import com.wagologies.spigotplugin.battle.BattleInfo;
import com.wagologies.spigotplugin.battle.BattleManager;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.CampaignManager;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.dungeon.DungeonManager;
import com.wagologies.spigotplugin.entity.EntityManager;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.item.ItemManager;
import com.wagologies.spigotplugin.lobby.LobbyManager;
import com.wagologies.spigotplugin.player.PlayerManager;
import com.wagologies.spigotplugin.spell.SpellManager;
import com.wagologies.spigotplugin.utils.ActionBar;
import com.wagologies.spigotplugin.utils.WeatherManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;

public class SpigotPlugin extends JavaPlugin {

    private PlayerManager playerManager;
    private EntityManager entityManager;
    private ActionBar actionBar;
    private BattleManager battleManager;
    private CampaignManager campaignManager;
    private WeatherManager weatherManager;
    private ItemManager itemManager;
    private SpellManager spellManager;
    private LobbyManager lobbyManager;
    private DungeonManager dungeonManager;
    private SpiGUI guiManager;
    private NPCRegistry npcRegistry;
    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        BattleInfo.RegisterConfiguration(this);
        Campaign.RegisterConfiguration(this);
        npcRegistry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
        battleManager = new BattleManager(this);
        campaignManager = new CampaignManager(this);
        lobbyManager = new LobbyManager(this);
        dungeonManager = new DungeonManager(this);
        playerManager = new PlayerManager(this);
        entityManager = new EntityManager(this);
        itemManager = new ItemManager(this);
        spellManager = new SpellManager(this);
        actionBar = new ActionBar(this);
        weatherManager = new WeatherManager(this);
        guiManager = new SpiGUI(this);
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
        for (RPGEntity entity : new ArrayList<>(entityManager.getEntities())) {
            entity.remove(false);
        }
        dungeonManager.cleanupDungeons();
        campaignManager.saveCampaigns();
        battleManager.saveBattles();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public EntityManager getEntityManager() {
        return entityManager;
    }
    public ActionBar getActionBar() {
        return actionBar;
    }
    public BattleManager getBattleManager() {
        return battleManager;
    }
    public CampaignManager getCampaignManager() {
        return campaignManager;
    }
    public ItemManager getItemManager() {
        return itemManager;
    }
    public SpellManager getSpellManager() {
        return spellManager;
    }
    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }
    public SpiGUI getGuiManager() {
        return guiManager;
    }
    public NPCRegistry getNPCRegistry() {
        return npcRegistry;
    }
}
