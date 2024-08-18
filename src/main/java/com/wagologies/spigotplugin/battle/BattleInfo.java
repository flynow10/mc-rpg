package com.wagologies.spigotplugin.battle;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.MobType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import java.util.*;

public class BattleInfo implements ConfigurationSerializable {
    private String name;
    private String battleId;
    private List<SpawnLocation> spawnLocations = new ArrayList<>();
    private BattleBorder battleBorder = null;
    private Vector boxPos1 = null;
    private Vector boxPos2 = null;

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

    public String getBattleId() {
        return battleId;
    }

    public BattleBorder getBattleBorder() {
        return battleBorder;
    }

    public Vector getBoxPos1() {
        return boxPos1;
    }

    public Vector getBoxPos2() {
        return boxPos2;
    }

    public SpawnLocation addSpawnLocation(Location location) {
        SpawnLocation spawnLocation = new SpawnLocation(location.toVector());
        this.spawnLocations.add(spawnLocation);
        return spawnLocation;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        if(boxPos1 != null && boxPos2 != null) {
            map.put("boxPos1", boxPos1.serialize());
            map.put("boxPos2", boxPos2.serialize());
        }
        if(battleBorder != null) {
            map.put("borderX", battleBorder.borderX);
            map.put("borderZ", battleBorder.borderZ);
            map.put("borderWidth", battleBorder.borderWidth);
        }
        Map<String, Object> locations = new HashMap<>();
        for (int i = 0; i < spawnLocations.size(); i++) {
            SpawnLocation spawnLocation = spawnLocations.get(i);
            locations.put(String.valueOf(i), spawnLocation.serialize());
        }
        map.put("locations", locations);
        return map;
    }

    public static BattleInfo deserialize(Map<String, Object> map) {
        BattleInfo battleInfo = new BattleInfo();
        battleInfo.name = (String) map.get("name");

        if(map.containsKey("boxPos1") && map.containsKey("boxPos2")) {
            battleInfo.boxPos1 = Vector.deserialize((Map<String, Object>) map.get("boxPos1"));
            battleInfo.boxPos2 = Vector.deserialize((Map<String, Object>) map.get("boxPos2"));
        }

        if (map.containsKey("borderX") && map.containsKey("borderZ") && map.containsKey("borderWidth")) {
            double borderX = (double) map.get("borderX");
            double borderZ = (double) map.get("borderZ");
            double borderWidth = (double) map.get("borderWidth");

            battleInfo.battleBorder = new BattleBorder(borderX, borderZ, borderWidth);
        }
        Map<String, Object> locationsData = (Map<String, Object>) map.get("locations");
        for (String locationKey : locationsData.keySet()) {
            SpawnLocation location = SpawnLocation.deserialize((Map<String, Object>) locationsData.get(locationKey));
            battleInfo.spawnLocations.add(location);
        }
        return battleInfo;
    }

    public static void RegisterConfiguration(SpigotPlugin plugin) {
        ConfigurationSerialization.registerClass(BattleInfo.class);
        ConfigurationSerialization.registerClass(SpawnLocation.class);
        plugin.getLogger().info("Registered battle classes for serialization");
    }

    public static class BattleBorder {
        private double borderX;
        private double borderZ;
        private double borderWidth;

        public BattleBorder(double borderX, double borderZ, double borderWidth) {
            this.borderX = borderX;
            this.borderZ = borderZ;
            this.borderWidth = borderWidth;
        }

        public double getBorderX() {
            return borderX;
        }

        public BattleBorder setBorderX(double borderX) {
            this.borderX = borderX;
            return this;
        }

        public double getBorderZ() {
            return borderZ;
        }

        public BattleBorder setBorderZ(double borderZ) {
            this.borderZ = borderZ;
            return this;
        }

        public double getBorderWidth() {
            return borderWidth;
        }

        public BattleBorder setBorderWidth(double borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public void addToWorld(World world) {
            WorldBorder border = world.getWorldBorder();
            border.setCenter(borderX, borderZ);
            border.setSize(borderWidth);
        }

        public static void RemoveFromWorld(World world) {
            world.getWorldBorder().reset();
        }
    }

    public static class SpawnLocation implements ConfigurationSerializable {
        private final Vector location;
        private final List<MobType> mobTypes;

        public SpawnLocation(Vector location) {
            this(location, new ArrayList<>());
        }
        public SpawnLocation(Vector location, List<MobType> mobTypes) {
            this.location = location;
            this.mobTypes = mobTypes;
        }

        public Vector getLocation() {
            return location;
        }

        public List<MobType> getMobTypes() {
            return mobTypes;
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
            Vector location = Vector.deserialize((Map<String, Object>) locationData);
            List<String> enumNames = (ArrayList<String>) map.get("mobs");
            return new SpawnLocation(location, enumNames.stream().map(MobType::valueOf).toList());
        }
    }
}
