package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.entity.AbilityScores;
import com.wagologies.spigotplugin.spell.SpellType;
import com.wagologies.spigotplugin.utils.SerializeInventory;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("OfflinePlayer")
public class OfflinePlayer implements ConfigurationSerializable {
    private final String id;
    private final String playerId;
    private String name;
    private int coins;
    private AbilityScores abilityScores;
    private String inventoryString;
    private Location location;
    private StarterKit starterKit;
    private SpellType[] knownSpells;

    public OfflinePlayer(String playerId, String name, AbilityScores abilityScores) {
        this(StringHelper.nanoId(), playerId, name, abilityScores, SerializeInventory.itemStackArrayToBase64(new ItemStack[41]), 25, new SpellType[0]);
    }

    public OfflinePlayer(String playerId, String name, AbilityScores abilityScores, String inventoryString) {
        this(StringHelper.nanoId(), playerId, name, abilityScores, inventoryString, 25, new SpellType[0]);
    }

    public OfflinePlayer(String id, String playerId, String name, AbilityScores abilityScores, String inventoryString, int coins, SpellType[] knownSpells) {
        this.id = id;
        this.playerId = playerId;
        this.name = name;
        this.abilityScores = abilityScores;
        this.coins = coins;
        this.inventoryString = inventoryString;
        this.knownSpells = knownSpells;
    }

    public String getId() {
        return id;
    }

    public String getPlayerId() {
        return playerId;
    }
    public StarterKit getStarterKit() {
        return starterKit;
    }

    public OfflinePlayer setStarterKit(StarterKit starterKit) {
        this.starterKit = starterKit;
        return this;
    }

    public String getName() {
        return name;
    }

    public OfflinePlayer setName(String name) {
        this.name = name;
        return this;
    }

    public AbilityScores getAbilityScores() {
        return abilityScores;
    }

    public OfflinePlayer setAbilityScores(AbilityScores abilityScores) {
        this.abilityScores = abilityScores;
        return this;
    }

    public String getInventoryString() {
        return inventoryString;
    }

    public ItemStack[] getInventoryContents() {
        try {
            return SerializeInventory.itemStackArrayFromBase64(inventoryString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OfflinePlayer setInventoryString(String inventoryString) {
        this.inventoryString = inventoryString;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public OfflinePlayer setLocation(Location location) {
        this.location = location;
        return this;
    }

    public int getCoins() {
        return coins;
    }

    public OfflinePlayer setCoins(int coins) {
        this.coins = coins;
        return this;
    }

    public SpellType[] getKnownSpells() {
        return this.knownSpells;
    }

    public OfflinePlayer setKnownSpells(SpellType[] knownSpells) {
        this.knownSpells = knownSpells;
        return this;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("playerId", playerId);
        result.put("name", name);
        result.put("kit", starterKit.getName());
        result.put("strength", abilityScores.getStrength());
        result.put("dexterity", abilityScores.getDexterity());
        result.put("constitution", abilityScores.getConstitution());
        result.put("intelligence", abilityScores.getIntelligence());
        result.put("wisdom", abilityScores.getWisdom());
        result.put("charisma", abilityScores.getCharisma());
        result.put("inventory", inventoryString);
        result.put("coins", coins);
        result.put("location", location.serialize());
        result.put("knownSpells", Arrays.stream(knownSpells).map(SpellType::name).toArray());
        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        String id = (String) args.get("id");
        if (id == null) {
            throw new IllegalStateException("Could not deserialize id of offline player!");
        }
        String playerId = (String) args.get("playerId");
        if (playerId == null) {
            throw new IllegalStateException("Could not deserialize playerId of offline player!");
        }
        String name = (String) args.get("name");
        if (name == null) {
            throw new IllegalStateException("Could not deserialize name of offline player!");
        }
        String inventory = (String) args.get("inventory");
        if (inventory == null) {
            throw new IllegalStateException("Could not deserialize inventory of offline player!");
        }
        String starterKitName = (String) args.get("kit");
        if (starterKitName == null) {
            throw new IllegalStateException("Could not deserialize kit of offline player!");
        }
        StarterKit starterKit = Arrays.stream(StarterKit.StarterKits).filter(kit -> kit.name.equals(starterKitName)).findAny().orElse(null);
        if(starterKit == null) {
            throw new IllegalStateException("Could not find kit named " + starterKitName + "!");
        }
        Integer coins = (Integer) args.get("coins");
        if(coins == null) {
            throw new IllegalStateException("Could not deserialize coins of offline player!");
        }

        ArrayList<String> knownSpellNames = (ArrayList<String>) args.get("knownSpells");
        if(knownSpellNames == null) {
            throw new IllegalStateException("Could not deserialize known spells of offline player!");
        }

        SpellType[] knownSpells = new SpellType[knownSpellNames.size()];
        for (int i = 0; i < knownSpellNames.size(); i++) {
            knownSpells[i] = SpellType.valueOf(knownSpellNames.get(i));
        }

        AbilityScores abilityScores = new AbilityScores();
        abilityScores.setStrength((Integer) args.get("strength"));
        abilityScores.setDexterity((Integer) args.get("dexterity"));
        abilityScores.setConstitution((Integer) args.get("constitution"));
        abilityScores.setIntelligence((Integer) args.get("intelligence"));
        abilityScores.setWisdom((Integer) args.get("wisdom"));
        abilityScores.setCharisma((Integer) args.get("charisma"));
        OfflinePlayer loadedCharacter = new OfflinePlayer(id, playerId, name, abilityScores, inventory, coins,
                knownSpells);
        loadedCharacter.setStarterKit(starterKit);
        loadedCharacter.setLocation(Location.deserialize((Map<String, Object>) args.get("location")));
        return loadedCharacter;
    }
}
