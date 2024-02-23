package com.wagologies.spigotplugin.battle;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.Mob;
import com.wagologies.spigotplugin.mob.MobType;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import java.util.*;

public class Battle implements ConfigurationSerializable {
    private BattleManager battleManager;
    private String name;
    private String battleId;
    private List<SpawnLocation> spawnLocations = new ArrayList<>();

    public void start() {
        for (SpawnLocation spawnLocation : spawnLocations) {
            for (MobType mobType : spawnLocation.mobTypes) {
                Mob mob = this.battleManager.getPlugin().getMobManager().spawn(mobType, spawnLocation.location);
                Vector random = Vector.getRandom();
                random = random.subtract(new Vector(0.5,0.5,0.5));
                random = random.setY(0.5);
                mob.setVelocity(random);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<SpawnLocation> getSpawnLocations() {
        return spawnLocations;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    protected void setBattleManager(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    public String getBattleId() {
        return battleId;
    }

    public SpawnLocation addSpawnLocation(Location location) {
        SpawnLocation spawnLocation = new SpawnLocation(location);
        this.spawnLocations.add(spawnLocation);
        return spawnLocation;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        Map<String, Object> locations = new HashMap<>();
        for (int i = 0; i < spawnLocations.size(); i++) {
            SpawnLocation spawnLocation = spawnLocations.get(i);
            locations.put(String.valueOf(i), spawnLocation.serialize());
        }
        map.put("locations", locations);
        return map;
    }

    public static Battle deserialize(Map<String, Object> map) {
        Battle battle = new Battle();
        battle.name = (String) map.get("name");
        Map<String, Object> locationsData = (Map<String, Object>) map.get("locations");
        for (String locationKey : locationsData.keySet()) {
            SpawnLocation location = SpawnLocation.deserialize((Map<String, Object>) locationsData.get(locationKey));
            battle.spawnLocations.add(location);
        }
        return battle;
    }

    public static void RegisterConfiguration(SpigotPlugin plugin) {
        ConfigurationSerialization.registerClass(Battle.class);
        ConfigurationSerialization.registerClass(SpawnLocation.class);
        plugin.getLogger().info("Registered battle classes for serialization");
    }

    public static class SpawnLocation implements ConfigurationSerializable {
        private final Location location;
        private final List<MobType> mobTypes;

        public SpawnLocation(Location location) {
            this(location, new ArrayList<>());
        }
        public SpawnLocation(Location location, List<MobType> mobTypes) {
            this.location = location;
            this.mobTypes = mobTypes;
        }

        public Location getLocation() {
            return location;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("location", this.location.serialize());
            serialized.put("mobs", this.mobTypes.stream().map(Enum::name).toArray());
            return serialized;
        }

        public static SpawnLocation deserialize(Map<String, Object> map) {
            Object locationData = map.get("location");
            if(!(locationData instanceof Map)) {
                throw new RuntimeException("Failed to deserialize a spawn location!");
            }
            Location location = Location.deserialize((Map<String, Object>) locationData);
            List<String> enumNames = (ArrayList<String>) map.get("mobs");
            return new SpawnLocation(location, enumNames.stream().map(MobType::valueOf).toList());
        }
    }
}
