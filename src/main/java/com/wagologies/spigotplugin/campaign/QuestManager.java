package com.wagologies.spigotplugin.campaign;

import com.wagologies.spigotplugin.event.player.RPGPlayerJoinEvent;
import com.wagologies.spigotplugin.event.player.RPGPlayerLeaveEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestManager implements Listener {
    private final Campaign campaign;
    private final BossBar questBar;
    private Type currentQuest;

    public QuestManager(Campaign campaign, Type currentQuest) {
        this.campaign = campaign;
        this.questBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        this.currentQuest = currentQuest;
        updateQuestBar();
        Bukkit.getPluginManager().registerEvents(this, campaign.getPlugin());
    }

    public void updateQuestBar() {
        questBar.setProgress(1);
        if(currentQuest.getColor() != null) {
            questBar.setColor(currentQuest.getColor());
        } else {
            questBar.setColor(BarColor.RED);
        }
        questBar.setTitle(ChatColor.YELLOW + currentQuest.getTitle());
        questBar.setVisible(true);
    }

    public Type getCurrentQuest() {
        return currentQuest;
    }

    public void triggerNewQuest(Type newQuest) {
        setCurrentQuest(newQuest);
        for (RPGPlayer rpgPlayer : campaign.getOnlinePlayers()) {
            Player player = rpgPlayer.getPlayer();
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "[New Quest]" + ChatColor.GRAY + ": " + ChatColor.YELLOW + newQuest.getTitle());
            player.sendMessage("");
        }
    }

    public void setCurrentQuest(Type currentQuest) {
        this.currentQuest = currentQuest;
        updateQuestBar();
        for (RPGPlayer onlinePlayer : campaign.getOnlinePlayers()) {
            onlinePlayer.updateScoreboard();
        }
    }

    public void hideBar(RPGPlayer player) {
        if(player.getCampaign().equals(campaign)) {
            questBar.removePlayer(player.getPlayer());
        }
    }

    public void showBar(RPGPlayer player) {
        if(player.getCampaign().equals(campaign)) {
            questBar.addPlayer(player.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(RPGPlayerJoinEvent event) {
        RPGPlayer player = event.getRPGPlayer();
        if(player.getCampaign().equals(campaign)) {
            questBar.addPlayer(player.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(RPGPlayerLeaveEvent event) {
        Player player = event.getPlayer();
        if(event.getCampaign().equals(campaign)) {
            questBar.removePlayer(player);
        }
    }

    public enum Type {
        TalkToBoatCaptain("Talk to the Boat Captain", PointOfInterest.NEW_CAMPAIGN),
        MeetMayor("Meet with the Mayor", PointOfInterest.CAMP_RESPAWN),
        PrepareForBandits("Prepare for the fight", PointOfInterest.CAMP_RESPAWN),
        FightBandits("Fight the Bandits!", PointOfInterest.CAMP_RESPAWN),
        ReturnToCamper("Return to the Campers", PointOfInterest.CAMP_RESPAWN),
        StormCastle("Storm the Castle", PointOfInterest.HOUSE_RESPAWN),
        ReachThroneRoom("Reach the Throne Room", PointOfInterest.HOUSE_RESPAWN),
        DestroyBoss("Defeat the Ice Princess " + ChatColor.AQUA + "Isadora Glacia", PointOfInterest.HOUSE_RESPAWN);

        private final String title;
        private final BarColor color;
        private final PointOfInterest respawnLocation;

        Type(String title, PointOfInterest respawnLocation) {
            this(title, null, respawnLocation);
        }

        Type(String title, BarColor color, PointOfInterest respawnLocation) {
            this.title = title;
            this.color = color;
            this.respawnLocation = respawnLocation;
        }

        public String getTitle() {
            return title;
        }

        public BarColor getColor() {
            return color;
        }

        public PointOfInterest getRespawnLocation() {
            return respawnLocation;
        }
    }
}
