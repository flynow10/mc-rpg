package com.wagologies.spigotplugin.battle;

import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.triggers.BoxTrigger;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.event.RPGEntityDeathEvent;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CampaignBattle implements Listener {
    private final Campaign campaign;
    private final BattleInfo battleInfo;
    private boolean hasStarted = false;
    private List<RPGEntity> entities = new ArrayList<>();
    private List<Consumer<Boolean>> onBattleEnd = new ArrayList<>();

    public CampaignBattle(Campaign campaign, BattleInfo battleInfo) {
        this.campaign = campaign;
        this.battleInfo = battleInfo;
        setupBoxTrigger();
        Bukkit.getPluginManager().registerEvents(this, campaign.getPlugin());
    }

    private void setupBoxTrigger() {
        if(battleInfo.getBoxPos1() != null && battleInfo.getBoxPos2() != null) {
            World campaignWorld = campaign.getWorld();
            new BoxTrigger(campaign.getPlugin(), campaign)
                    .withBoxSize(
                            battleInfo.getBoxPos1().toLocation(campaignWorld),
                            battleInfo.getBoxPos2().toLocation(campaignWorld)
                    )
                    .withCallback(player -> start())
                    .withActivateMultiple(true);
        }
    }

    public void addBattleEndListener(Consumer<Boolean> onBattleEnd) {
        this.onBattleEnd.add(onBattleEnd);
    }

    public void start() {
        if(hasStarted) {
            return;
        }
        hasStarted = true;
        World world = campaign.getWorld();
        battleInfo.getBattleBorder().addToWorld(world);
        for (BattleInfo.SpawnLocation spawnLocation : battleInfo.getSpawnLocations()) {
            int mobCount = spawnLocation.getMobTypes().size();
            for (MobType mobType : spawnLocation.getMobTypes()) {
                RPGEntity mob = campaign.getPlugin().getEntityManager().spawn(mobType, spawnLocation.getLocation().toLocation(world));
                entities.add(mob);
                if(mobCount > 1) {
                    Vector random = Vector.getRandom();
                    random = random.subtract(new Vector(0.5,0.5,0.5));
                    random = random.setY(0.5);
                    mob.getMainEntity().setVelocity(random);
                }
            }
        }
    }

    public boolean isBattleOver() {
        return entities.stream().allMatch(RPGEntity::isDead);
    }

    public void endBattle(boolean success) {
        for(RPGEntity mob : entities) {
            if(!mob.isDead() && !mob.isRemoved()) {
                mob.remove(false);
            }
        }
        World campaignWorld = campaign.getWorld();
        campaignWorld.getWorldBorder().reset();

        List<RPGPlayer> players = campaign.getOnlinePlayers();

        for (RPGPlayer player : players) {
            if(success) {
                player.getPlayer().sendMessage(ChatColor.GREEN + "You completed the battle!");
            } else {
                player.getPlayer().sendMessage(ChatColor.RED + "You failed the battle!");
            }
        }

        if(!success) {
            hasStarted = false;
        }

        for (Consumer<Boolean> onBattleEnd : onBattleEnd) {
            onBattleEnd.accept(success);
        }
    }

    @EventHandler
    public void onEntityDeath(RPGEntityDeathEvent event) {
        if(entities.contains(event.getEntity())) {
            if(isBattleOver()) {
                endBattle(true);
            }
        }
    }
}
