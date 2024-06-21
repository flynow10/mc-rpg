package com.wagologies.spigotplugin.campaign;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.arena.Arena;
import com.wagologies.spigotplugin.npc.NPC;
import com.wagologies.spigotplugin.npc.npcs.*;
import com.wagologies.spigotplugin.player.OfflinePlayer;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.utils.WorldHelper;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Campaign")
public class Campaign implements ConfigurationSerializable {
    private CampaignManager campaignManager = null;
    private final String name;
    private final World world;
    private final List<OfflinePlayer> players = new ArrayList<>();
    private final List<NPC> npcs = new ArrayList<>();
    private final Map<String, Object> npcData;
    private Arena arena;

    public Campaign(String name, List<OfflinePlayer> players) {
        this(name, players, null);
    }

    public Campaign(String name, List<OfflinePlayer> players, Map<String, Object> npcConfiguration) {
        this(name, WorldHelper.loadWorld(name), players, npcConfiguration);
    }

    public Campaign(String name, World world, List<OfflinePlayer> players, Map<String, Object> npcData) {
        this.name = name;
        this.world = world;
        this.players.addAll(players);
        this.npcData = npcData;
    }

    public void initialize() {
        arena = new Arena(this);
        spawnNPCs();
    }

    private void spawnNPCs() {
        BoatCaptain captain = new BoatCaptain(getPlugin(), this);
        FemaleCamper femaleCamper = new FemaleCamper(getPlugin(), this);
        MaleCamper maleCamper = new MaleCamper(getPlugin(), this);
        Mayor mayor = new Mayor(getPlugin(), this);
        WeaponsMerchant weaponsMerchant = new WeaponsMerchant(getPlugin(), this);
        WizardMerchant wizardMerchant = new WizardMerchant(getPlugin(), this);
        VillageStatue villageStatue = new VillageStatue(getPlugin(), this);

        npcs.add(captain);
        npcs.add(femaleCamper);
        npcs.add(maleCamper);
        npcs.add(mayor);
        npcs.add(weaponsMerchant);
        npcs.add(wizardMerchant);
        npcs.add(villageStatue);

        this.deserializeNPCs(this.npcData);

        for (NPC npc : npcs) {
            npc.spawn();
        }
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public List<OfflinePlayer> getPlayers() {
        return players;
    }

    public List<NPC> getNpcs() {
        return npcs;
    }

    public Arena getArena() {
        return arena;
    }

    public SpigotPlugin getPlugin() {
        return campaignManager.getPlugin();
    }

    public CampaignManager getCampaignManager() {
        if(campaignManager == null) {
            throw new IllegalStateException("Something went very wrong! Campaign was created without reference to campaign manager!");
        }
        return campaignManager;
    }

    protected void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public OfflinePlayer getCharacter(Player player) {
        return players.stream().filter(p -> p.getPlayerId().equals(player.getUniqueId().toString())).findFirst().orElse(null);
    }

    public void joinPlayer(Player player) {
        OfflinePlayer character = players.stream().filter(p -> p.getPlayerId().equals(player.getUniqueId().toString())).findFirst().orElseThrow();
        SpigotPlugin plugin = getCampaignManager().getPlugin();
        RPGPlayer rpgPlayer = plugin.getPlayerManager().joinPlayer(player, this);
        rpgPlayer.loadFromOffline(character);
    }

    private Map<String, Object> serializeNPCs() {
        Map<String, Object> map = new HashMap<>();

        for (NPC npc : npcs) {
            String serializeName = npc.getClass().getSimpleName();
            Object data = npc.serialize();
            map.put(serializeName, data);
        }

        return map;
    }

    private void deserializeNPCs(Map<String, Object> map) {
        if(map == null) {
            return;
        }
        for (NPC npc : npcs) {
            String deserializeName = npc.getClass().getSimpleName();
            if(map.containsKey(deserializeName)) {
                npc.deserialize((Map<String, Object>) map.get(deserializeName));
            } else {
                npc.deserialize(null);
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("players", players.stream().map(OfflinePlayer::serialize).toArray());
        map.put("npcs", serializeNPCs());
        return map;
    }

    public static Campaign deserialize(Map<String, Object> map) {
        String campaignName = (String) map.get("name");
        WorldHelper.loadWorld(campaignName);
        ArrayList<Map<String, Object>> playerObjects = (ArrayList<Map<String, Object>>) map.get("players");
        List<OfflinePlayer> players = new ArrayList<>();
        for (Map<String, Object> playerObject : playerObjects) {
            players.add(OfflinePlayer.deserialize(playerObject));
        }
        return new Campaign(campaignName, players, (Map<String, Object>) map.get("npcs"));
    }

    public static void RegisterConfiguration(SpigotPlugin plugin) {
        ConfigurationSerialization.registerClass(Campaign.class);
        ConfigurationSerialization.registerClass(OfflinePlayer.class);
        plugin.getLogger().info("Registered campaign classes for serialization");
    }
}
