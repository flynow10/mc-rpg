package com.wagologies.spigotplugin.dungeon;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.PointOfInterest;
import com.wagologies.spigotplugin.dungeon.generator.Generator;
import com.wagologies.spigotplugin.dungeon.generator.Room;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.event.RPGEntityDeathEvent;
import com.wagologies.spigotplugin.npc.npcs.InsideCastleGuard;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Dungeon implements Listener {
    private final SpigotPlugin plugin;
    private final DungeonManager dungeonManager;
    private DungeonState state = DungeonState.PreStart;
    private final List<RPGPlayer> players = new ArrayList<>();
    private final int floor;
    private final Generator dungeonGenerator;
    private final Campaign campaign;
    private final List<RPGEntity> entities = new ArrayList<>();
    private final BossBar bossBar;
    private InsideCastleGuard castleGuard;

    private boolean isLoaded = false;

    public Dungeon(SpigotPlugin plugin, Campaign campaign, int floor) {
        this(plugin, null, campaign, floor);
    }

    public Dungeon(SpigotPlugin plugin, DungeonManager dungeonManager, Campaign campaign, int floor) {
        this.plugin = plugin;
        this.dungeonManager = dungeonManager;
        this.floor = floor;
        this.dungeonGenerator = new Generator(plugin, 3, 3, floor);
        this.campaign = campaign;
        this.bossBar = Bukkit.createBossBar(ChatColor.RED + "Castle Raid: 0%", BarColor.BLUE, BarStyle.SOLID);
        updateBossBar();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void start() {
        if(state != DungeonState.PreStart) {
            throw new IllegalStateException("Cannot start a dungeon after it has already started");
        }
        if(!isLoaded) {
            for(RPGPlayer player : players) {
                player.getPlayer().sendMessage(ChatColor.GREEN + "The castle will open momentarily...");
            }
            setupDungeon();
        }
        spawnMobs();

        state = DungeonState.Running;

        for(RPGPlayer player : players) {
            player.getPlayer().teleport(getSpawnLocation());
            player.setInDungeon(true);
            bossBar.addPlayer(player.getPlayer());
        }
        bossBar.setVisible(true);
    }

    public void setupDungeon() {
        pasteDungeon();
        setupCastleGuard();
        isLoaded = true;
    }

    public void cleanup() {
        if(this.dungeonManager != null) {
            this.dungeonManager.removeDungeon(this);
        }
        if(isLoaded) {
            World world = campaign.getWorld();
            dungeonGenerator.cleanupDungeon(world, PointOfInterest.DUNGEON_GENERATION.toLocation(world));
            castleGuard.despawn();
        }
        for(RPGPlayer player : players) {
            player.setInDungeon(false);
        }
        bossBar.removeAll();
        bossBar.setVisible(false);
        for(RPGEntity entity : entities) {
            entity.remove(false);
        }
        state = DungeonState.CleanedUp;
    }

    public void leaveDungeon(boolean success) {
        if(success) {
            state = DungeonState.Succeeded;
        } else {
            state = DungeonState.Failed;
        }

        for (RPGPlayer player : players) {
            player.setInDungeon(false);
            player.getPlayer().teleport(PointOfInterest.DUNGEON_EXIT_POINT.toLocation(campaign.getWorld()));
        }

        cleanup();
    }

    private void pasteDungeon() {
        World world = campaign.getWorld();
        dungeonGenerator.pasteDungeon(world, PointOfInterest.DUNGEON_GENERATION.toLocation(world));
    }

    private void spawnMobs() {
        World world = campaign.getWorld();
        Location dungeonOrigin = PointOfInterest.DUNGEON_GENERATION.toLocation(world);
        Room[][] rooms = dungeonGenerator.getRooms();
        for (Room[] row : rooms) {
            for (Room room : row) {
                entities.addAll(room.spawnMobs(plugin, dungeonOrigin));
            }
        }
    }

    private void setupCastleGuard() {
        castleGuard = new InsideCastleGuard(plugin, getCampaign(), this);
        castleGuard.spawn();
    }

    private void updateBossBar() {
        int enemyCount = entities.size();
        int liveEnemyCount = getLiveEntities();
        double completion;
        if(enemyCount == 0) {
            completion = 0;
        } else {
            completion = 1 - ((double)liveEnemyCount / enemyCount);
        }

        bossBar.setTitle(ChatColor.RED + "Castle Raid: " + String.format("%.2f", completion) + "%");
        bossBar.setProgress(completion);
    }

    @EventHandler
    public void onEntityDeath(RPGEntityDeathEvent event) {
        if(entities.contains(event.getEntity())) {
            updateBossBar();
        }
    }

    public int getLiveEntities() {
        return (int) entities.stream().filter((entity) -> !entity.isDead()).count();
    }

    public boolean isDungeonComplete() {
        return this.state == DungeonState.Running && getLiveEntities() == 0;
    }

    public DungeonState getState() {
        return state;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public List<RPGPlayer> getPlayers() {
        return players;
    }

    public int getFloor() {
        return floor;
    }

    public Location getSpawnLocation() {
        World world = campaign.getWorld();
        Location spawnLocation = PointOfInterest.DUNGEON_GENERATION.toLocation(world);
        spawnLocation.add(dungeonGenerator.getWidth() * Room.ROOM_SIZE / 2d, 5, -4);
        return spawnLocation;
    }
}
