package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.triggers.BoxTrigger;
import com.wagologies.spigotplugin.event.RPGClickNPCEvent;
import com.wagologies.spigotplugin.npc.Conversation;
import com.wagologies.spigotplugin.npc.NPC;
import com.wagologies.spigotplugin.player.RPGPlayer;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FemaleCamper extends NPC {
    public final BoxTrigger introTrigger;
    public boolean nameIsKnown = false;
    public boolean hasPlayedIntro = false;
    public boolean hasPlayedAskForHelp = false;
    public Conversation intro = new Conversation(
            new Conversation.PlayerLookAtNPC(),
            new Conversation.Speak("Hey there!", 0),
            new Conversation.MoveToPlayer(80),
            new Conversation.Speak("It's not a good idea to go that way without a weapon!"), new Conversation.Speak(
            "If you come over to our campsite, my brother and I might be able to help you."),
            new Conversation.MoveTo(new Vector(675.5, 84, 876.5)),
            new Conversation.CustomRunnable((players, npc, conversation) -> hasPlayedIntro = true)
    );
    public Conversation askForHelp = new Conversation(
            new Conversation.Speak("Nice to meet you "+ ChatColor.AQUA +"{player}" + ChatColor.GREEN + "! My name is " + ChatColor.AQUA + "Jessie" + ChatColor.GREEN + "."),
            new Conversation.Speak("My brother and I just arrived here a few days ago for a backpacking trip."),
            new Conversation.Speak("Unfortunately, we ran into some \"unsavory\" characters on the path up to the town."),
            new Conversation.Speak("They stole all the money we had for our trip, and we had no choice but to camp here until we came up with a plan."),
            new Conversation.Speak("We've been preparing weapons to get our money back, but we're just not strong enough!"),
            new Conversation.Speak("My brother can show you where we've been stashing our weapons and practicing."),
            new Conversation.Speak("If you can help us get our money back, we'd be happy to give you some of the items we've been collecting."),
            new Conversation.CustomRunnable((players, npc, conversation) -> hasPlayedAskForHelp = true)
    );

    public FemaleCamper(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        introTrigger = new BoxTrigger(plugin, campaign).withCallback(this::playIntroCutscene)
                .withBoxSize(687.68, 83.00, 861.59, 715.07, 92.22, 865.35);
        setTargetLocation(new Location(campaign.getWorld(), 675.5, 84, 876.5));
    }

    @Override
    public void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("FemaleCamper", getSkinSignature(), getSkinTexture());
    }

    @Override
    public String getName() {
        return nameIsKnown ? "Jessie" : "Camper";
    }

    public void playIntroCutscene(RPGPlayer player) {
        if(!hasPlayedIntro) {
            intro.addPlayer(player, this, getPlugin());
        }
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        if (!hasPlayedIntro) {
            return;
        }
        if(event.getRPGPlayer().isInConversation()) {
            return;
        }
        if(citizenNPC.getNavigator().isNavigating()) {
            return;
        }
        if(!hasPlayedAskForHelp) {
            nameIsKnown = true;
            updateName();
            askForHelp.addPlayer(event.getRPGPlayer(), this, getPlugin());
            return;
        }
        MaleCamper brother = getCampaign().getNpcs().stream().filter(npc -> npc instanceof MaleCamper).map(npc -> (MaleCamper)npc).findAny().orElseThrow();
        if(!brother.hasGoneToArena) {
            speakToPlayer(event.getPlayer(), "Go talk to my brother! He'll show you where we've been training.", 1, 1);
            return;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("nameIsKnown", nameIsKnown);
        data.put("hasPlayedIntro", hasPlayedIntro);
        data.put("hasPlayedAskForHelp", hasPlayedAskForHelp);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if(data == null) {
            return;
        }

        if(data.containsKey("nameIsKnown")) {
            nameIsKnown = (boolean) data.get("nameIsKnown");
        }

        if(data.containsKey("hasPlayedIntro")) {
            hasPlayedIntro = (boolean) data.get("hasPlayedIntro");
        }

        if(data.containsKey("hasPlayedAskForHelp")) {
            hasPlayedAskForHelp = (boolean) data.get("hasPlayedAskForHelp");
        }
    }

    public String getSkinSignature() {
        return "QJN7zfnliZ81S2xMS8BzUDyYleBrThXqpTlWd3vFJ0dEUKgu3SppZ7Wq+zKw1YxOy/EFitFP9+LRydACUGQRsZKxc8sqnew8noeBWIuHWgDqAzaYwDTgY2ObC1LR1mUaarIk8tIOGArRAciMNCpc/EG7/sACdOX92LsordCgZg4JMF9+hETCeb6DuiQE0+YemxVWzRt2URwDpJF2Den6fNGnBdO3WtojWwvkZNPCU1IlvVGE9nTKOvbw/9z4hm2UYg2+dryhqgih4+jdHwVf0B2WgtGkEWz7TkkfCBrMZ993HaeP8FyqLi91Roj/Bn4Y3zbc3jqV7ye8/6/RLGxfjCwNOUiwWAS98jvrhdn2BH8Fab2IaTkaOod2hcxG9Yfymq1NCl7V2TslHlcC2b4lXvlXx640T0gNuBgGmjtAL1Rl1gbbIXpm4iYRE7E0VYTiVqt8pUAp7fztUnoSJwffpoS0noonRvIdY0x5FUL1FM8SOyJdaSD/Pju1moOHBj2el8glMeRjhOnnTDKEPkeFyxyV2XaqIikbNQf0v0ck6aPJHb8IyvxFumuIpmb04D4+dr/xLsSaytcNLjqpic9COU/vWDzexFaoDegcRcixolvlfFVPRJx99/QhgccIi6CbNpmM/BShB5uY6Vmd93/0TGFj5cGz3MYkW1y41A2lsWw=";
    }

    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY4MzEyMjk3MjQ4NCwKICAicHJvZmlsZUlkIiA6ICIyMDZlMWZkYjI5Yzk0NGYxOTQ5OTg4NzAwNTQxMGQ2NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJoNHlsMzMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIxNDFlZTc4NTZkYjBiZmI3ZGI0Y2Y5NTZmNGQ0NDY3Yjc0YWFlZWZjNmRhYzM2ZDRlNzIwZjI3ZTlhZGUyOSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    }
}
