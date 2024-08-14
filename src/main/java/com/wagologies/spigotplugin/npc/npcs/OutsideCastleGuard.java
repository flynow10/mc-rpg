package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.dungeon.Dungeon;
import com.wagologies.spigotplugin.dungeon.DungeonState;
import com.wagologies.spigotplugin.event.RPGClickNPCEvent;
import com.wagologies.spigotplugin.npc.Conversation;
import com.wagologies.spigotplugin.npc.NPC;
import com.wagologies.spigotplugin.player.RPGPlayer;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutsideCastleGuard extends NPC {
    public boolean hasBeenIntroduced = false;
    @Nullable
    public Dungeon dungeon = null;
    public Conversation introduction = new Conversation(
            new Conversation.Speak(
                    "Nice job getting through those courtyard monsters. We couldn't have done it without you!"),
            new Conversation.Speak("When you're ready to take on the castle, let me know."),
            new Conversation.CustomRunnable((players, npc, conversation) -> hasBeenIntroduced = true)
    );

    public OutsideCastleGuard(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(new Location(campaign.getWorld(), 682.5,205,537.5));
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("Village Guard 1", getSkinSignature(), getSkinTexture());
    }

    @Override
    public String getName() {
        return "Village Guard";
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        RPGPlayer player = event.getRPGPlayer();

        if (!hasBeenIntroduced) {
            introduction.addPlayer(player, this, getPlugin());
            return;
        }

        if(dungeon == null || dungeon.getState().isFinished()) {
            dungeon = getPlugin().getDungeonManager().createDungeon(getCampaign(), 1);
        }

        if(dungeon.getState() == DungeonState.Running) {
            new Conversation(
                    new Conversation.Speak("Be careful! A party already went into the castle."),
                    new Conversation.Speak("It's much to dangerous to enter the door while they're fighting, so you'll have to wait until they come back.")
            ).addPlayer(player, this, getPlugin());
            return;
        }

        List<RPGPlayer> dungeonPlayers = dungeon.getPlayers();

        if (dungeonPlayers.contains(player)) {
            String personPlural = dungeonPlayers.size() == 1 ? "person" : "people";
            new Conversation(
                    new Conversation.Speak(
                            "We've got a party of " + ChatColor.YELLOW + dungeonPlayers.size() + ChatColor.GREEN + " " + personPlural + " now. Are you ready to take on the castle?", 20),
                    new Conversation.YesNo(getPlugin(), (p) -> startDungeon(), (p) -> {
                    }, "Yes!", "Not yet")
            ).addPlayer(player, this, getPlugin());
        } else {
            new Conversation(
                    new Conversation.Speak("I'm putting together a party to storm this castle, can we count on you to help?", 20),
                    new Conversation.YesNo(getPlugin(), (p) -> {
                        dungeonPlayers.add(p);
                        speakToPlayer(p.getPlayer(), "That's great to hear! If you have anyone else in your party, have them talk to me before we get started.", 1, 1);
                    }, (p) -> {
                        speakToPlayer(p.getPlayer(), "Not quite ready? Come back and talk to me when you've prepared.", 1, 1);
                    }, "Yes!", "Not yet")
            ).addPlayer(player, this, getPlugin());
        }
    }

    public void startDungeon() {
        if(dungeon == null) {
            throw new RuntimeException("Cannot start dungeon because it is null!");
        }
        dungeon.start();
    }

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("hasBeenIntroduced", hasBeenIntroduced);
        return data;
    }

    @Override
    public void deserialize(@Nullable Map<String, Object> data) {
        if(data == null) {
            return;
        }

        if(data.containsKey("hasBeenIntroduced")) {
            hasBeenIntroduced = (boolean) data.get("hasBeenIntroduced");
        }
    }

    public static String getSkinSignature() {
        return "oKYkemKou5Z8QGWFKzr2bTT1N5d8La4xgZLSN/18EO6nDyvix3SIcU7RO3llSwwfd7hYehiKxdhqlbdT3s3gm9eTa5tZsU7cngLIKFLYsGevjtN3VQTVpDq1ZyXRGC84OEY+kjkuTOasOea92HR+iLiT6Zq3yls4SO5rH1FjSgCUoGMwLB0IiDszv0b6qTiCJW0CsqmivEIcMZqW6PTpfrRXxzUG1dg1u41Cmfrntc9hKTqMR7JhX9PLAvhbAYRR2RN5UVPRfnD26l/7v35ClA/IC0tmMkk6NJn7FnKzEVA3o8W/W6mhHAng1O/gbWC+lAPSyoJ+Asi4py+pLfj9PJKgVDH/DECGeUflcTCsMVYsK5vHBpN7J3KTqJD4qsFUg7wuSbzwzCf8vd9ApGN7zv/qcWwKFlawAgJAi50ZAw+MOn6ZBq6mB4Bb8D3Bsqbhezf7LO1+SlEjQIFhpLq5zZHYvHQedtO+mkgksv8Pkb/TOIv/8QDjJap4F5iRNcMhEG6cY+uuwr7ukRnitU7KEbUwjiBjx7CcvdWsGaDUUvjEvGJDW2vrP4R28iASQT4Gzsc3yGOSxjwVmxmI+Z/s59AZS3ykwBo0hLZAIluvz/jRGNVHJKfvEyC0aYzF2WmKy6Fa5oT7iZIoqNeI8hNMDcteRDAfXHXmFePvQhUkmzI=";
    }

    public static String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY0OTYxMTczNjk0MCwKICAicHJvZmlsZUlkIiA6ICJlZThjNWMzMGY3NWU0N2QxOTBmOTllNjI5NDgyOGZjMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcGFya19QaGFudG9tIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FiMDljNzkzYjg5MmNkODExMWEzZTI3OWFmNTlhMjcxZmJlZTk0OGIzZjNkM2Y2YTkyMWY4MzhiNGQ4YTllODkiCiAgICB9CiAgfQp9";
    }
}
