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
        TalkToBoatCaptain("Talk to the Boat Captain"),
        MeetMayor("Meet with the Mayor"),
        PrepareForBandits("Prepare for the fight"),
        FightBandits("Fight the Bandits!"),
        ReturnToCamper("Return to the Campers"),
        StormCastle("Storm the Castle"),
        ReachThroneRoom("Reach the Throne Room"),
        DestroyBoss("Destroy the Ice Princess " + ChatColor.AQUA + "Isadora Glacia");

        private final String title;
        private final BarColor color;

        Type(String title) {
            this(title, null);
        }

        Type(String title, BarColor color) {
            this.title = title;
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public BarColor getColor() {
            return color;
        }
    }
}
