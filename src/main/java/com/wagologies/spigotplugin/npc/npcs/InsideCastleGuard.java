package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.dungeon.Dungeon;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.npc.Conversation;
import com.wagologies.spigotplugin.npc.NPC;
import com.wagologies.spigotplugin.player.RPGPlayer;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InsideCastleGuard extends NPC {

    private Dungeon dungeon;
    private List<RPGPlayer> readyToLeave = new ArrayList<>();

    public InsideCastleGuard(SpigotPlugin plugin, Campaign campaign, Dungeon dungeon) {
        super(plugin, campaign);
        this.dungeon = dungeon;
        Location spawnLocation = dungeon.getSpawnLocation().add(1.5, 0, 2.5);
        setTargetLocation(spawnLocation);
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("Village Guard 1", OutsideCastleGuard.getSkinSignature(), OutsideCastleGuard.getSkinTexture());
    }

    @Override
    public String getName() {
        return "Village Guard";
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        RPGPlayer player = event.getRPGPlayer();
        Player bukkitPlayer = event.getPlayer();
        if(!dungeon.getPlayers().contains(player)) {
            return;
        }

        if(player.isInCombat()) {
            speakToPlayer(bukkitPlayer, "Don't talk now! There's more enemies behind you!", 1, 1);
            return;
        }

        if(!dungeon.isDungeonComplete()) {
           speakToPlayer(bukkitPlayer, "I think I hear more enemies in the dungeon. Make sure to clear this entire floor before we leave!", 1, 1);
           return;
        }

        new Conversation(
                new Conversation.Speak("Are you ready to leave this floor of the castle?", 0),
                new Conversation.YesNo(getPlugin(), rpgPlayer -> {if(!readyToLeave.contains(rpgPlayer)) readyToLeave.add(rpgPlayer);}, rpgPlayer -> readyToLeave.remove(rpgPlayer)),
                new Conversation.SuspendedSpeak(() -> {
                    if(readyToLeave.contains(player)) {
                        return "Great! When the rest of your party is ready as well we'll head out.";
                    }
                    return "Just let me know when you're ready to leave!";
                }, 20),
                new Conversation.CustomRunnable((rpgPlayers, npc, conversation) -> {
                    if(readyToLeave.size() == dungeon.getPlayers().size()) {
                        dungeon.leaveDungeon(true);
                    }
                })
        ).addPlayer(player, this, getPlugin());
    }
}
