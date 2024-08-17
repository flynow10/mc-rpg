package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.npc.Conversation;
import com.wagologies.spigotplugin.npc.NPC;
import net.citizensnpcs.trait.VillagerProfession;
import net.citizensnpcs.trait.versioned.VillagerTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class BoatCaptain extends NPC {
    public BoatCaptain(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(new Location(campaign.getWorld(), 701.5, 63.5, 935.5));
    }

    @Override
    public void setupNPC() {
        citizenNPC.setBukkitEntityType(EntityType.VILLAGER);
        VillagerProfession profession = citizenNPC.getOrAddTrait(VillagerProfession.class);
        profession.setProfession(Villager.Profession.SHEPHERD);
        VillagerTrait villagerTrait = citizenNPC.getOrAddTrait(VillagerTrait.class);
        villagerTrait.setType(Villager.Type.SNOW);
    }

    @Override
    public String getName() {
        return "Boat Captain";
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        new Conversation(new Conversation.Speak("We've arrived in Avalan! I hope you enjoyed your ride."),
                new Conversation.Speak(
                        "Be careful as you head up the path. I hear there the town has been plagued by monster attacks from the mountain!"),
                new Conversation.Speak("Good luck and be safe on your travels adventurer!")).addPlayer(
                event.getRPGPlayer(), this, getPlugin());
    }
}
