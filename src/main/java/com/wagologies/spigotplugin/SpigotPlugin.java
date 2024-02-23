package com.wagologies.spigotplugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.wagologies.spigotplugin.battle.Battle;
import com.wagologies.spigotplugin.battle.BattleManager;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.item.ItemManager;
import com.wagologies.spigotplugin.mob.MobManager;
import com.wagologies.spigotplugin.player.PlayerManager;
import com.wagologies.spigotplugin.spell.SpellManager;
import com.wagologies.spigotplugin.utils.ActionBar;
import com.wagologies.spigotplugin.utils.WeatherManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SpigotPlugin extends JavaPlugin {

    private PlayerManager playerManager;
    private MobManager mobManager;
    private ActionBar actionBar;
    private BattleManager battleManager;
    private WeatherManager weatherManager;
    private ItemManager itemManager;
    private SpellManager spellManager;
    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        Battle.RegisterConfiguration(this);
        playerManager = new PlayerManager(this);
        mobManager = new MobManager(this);
        actionBar = new ActionBar(this);
        battleManager = new BattleManager(this);
        weatherManager = new WeatherManager(this);
        itemManager = new ItemManager(this);
        spellManager = new SpellManager(this);

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
        battleManager.saveBattles();
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
    public BattleManager getBattleManager() {
        return battleManager;
    }
    public ItemManager getItemManager() {
        return itemManager;
    }
    public SpellManager getSpellManager() {
        return spellManager;
    }
}
