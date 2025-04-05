package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.item.RPGItemBuilder;
import com.wagologies.spigotplugin.npc.Conversation;
import com.wagologies.spigotplugin.npc.NPC;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.player.StarterKit;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

public class MaleCamper extends NPC {
    private static final Vector CAMPSITE_LOCATION = new Vector (672.5, 84, 866.5);
    private static final Vector ARENA_LOCATION = new Vector(626.5, 76, 893.5);
    public boolean nameIsKnown = false;
    public boolean hasGoneToArena = false;
    public List<String> hasGivenIntroItems = new ArrayList<>();

    public Conversation goToArena = new Conversation(
            new Conversation.Speak(
                    "Hi there. My name is " + ChatColor.AQUA + "Claudius Hoffman" + ChatColor.GREEN + ", but please call me " + ChatColor.AQUA + "Clyde" + ChatColor.GREEN + "."),
            new Conversation.Speak("Sorry if I'm a bit shy, my sister usually does all the talking."),
            new Conversation.Speak("I heard you can help us with our bandit problem?"),
            new Conversation.Speak("That's great! Let me show you to our training area."),
            new Conversation.MoveTo(ARENA_LOCATION),
            new Conversation.CustomRunnable((players, npc, conversation) -> hasGoneToArena = true)
    );

    public MaleCamper(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(CAMPSITE_LOCATION.toLocation(campaign.getWorld()));
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("MaleCamper", getSkinSignature(), getSkinTexture());
    }

    @Override
    public String getName() {
        return nameIsKnown ? "Clyde" : "Camper";
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        if(citizenNPC.getNavigator().isNavigating()) {
            return;
        }
        if(event.getRPGPlayer().isInConversation()) {
            return;
        }

        RPGPlayer rpgPlayer = event.getRPGPlayer();
        FemaleCamper sister = (FemaleCamper) getCampaign().getNpcs().stream().filter(npc -> npc instanceof FemaleCamper).findFirst().orElseThrow();
        if(!sister.hasPlayedAskForHelp) {
            speakToPlayer(event.getPlayer(), "I'm a bit shy, would you please talk to my sister instead?", 1, 1);
            return;
        }
        if(!hasGoneToArena) {
            nameIsKnown = true;
            updateName();
            goToArena.addPlayer(rpgPlayer, this, getPlugin());
            return;
        }
        if(!hasGivenIntroItems.contains(rpgPlayer.getPlayer().getUniqueId().toString())) {
            Conversation giveItems = getGiveItemsConversation(rpgPlayer);
            giveItems.addPlayer(rpgPlayer, this, getPlugin());
            return;
        }

        speakToPlayer(event.getPlayer(), "If you want some practice, just step into the arena.", 1, 1);
    }

    private Conversation getGiveItemsConversation(RPGPlayer rpgPlayer) {
        StarterKit starterKit = rpgPlayer.getStarterKit();
        return new Conversation(
                new Conversation.Speak(
                        "This is where we've been training; you're welcome to use whenever you like."),
                new Conversation.Speak(
                        "You look like a " + starterKit.getDisplayColor() + starterKit.getName() + ChatColor.GREEN + ". It's not much, but here's what we have."),
                new Conversation.CustomRunnable((players, npc, conversation) -> {
                        for (RPGItemBuilder starterItemBuilder : starterKit.getStarterItems()) {
                            rpgPlayer.getPlayer()
                                    .getInventory()
                                    .addItem(starterItemBuilder.build(getPlugin()).getItemStack());
                        }
                        if(Objects.equals(starterKit.getName(), "Wizard")) {
                            rpgPlayer.getPlayer().getInventory().addItem(rpgPlayer.getSpellBookItem());
                        }
                }),
                new Conversation.Speak("Go ahead and take your new gear for a spin in the the training arena."),
                new Conversation.Speak("When you're ready, head back to my sister she'll tell you our plan."),
                new Conversation.Speak("Thank you again for your help!"),
                new Conversation.CustomRunnable((players, npc, conversation) -> hasGivenIntroItems.addAll(players.stream().map(player -> player.getPlayer().getUniqueId().toString()).toList()))
        );
    }

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("nameIsKnown", nameIsKnown);
        data.put("hasGoneToArena", hasGoneToArena);
        data.put("hasGivenIntroItems", hasGivenIntroItems);
        return data;
    }

    @Override
    public void deserialize(@Nullable Map<String, Object> data) {
        if(data == null) {
            return;
        }

        if(data.containsKey("nameIsKnown")) {
            nameIsKnown = (boolean) data.get("nameIsKnown");
        }

        if(data.containsKey("hasGoneToArena")) {
            hasGoneToArena = (boolean) data.get("hasGoneToArena");
            setTargetLocation(ARENA_LOCATION.toLocation(getCampaign().getWorld()));
        }

        if(data.containsKey("hasGivenIntroItems")) {
            hasGivenIntroItems = (List<String>) data.get("hasGivenIntroItems");
        }
    }

    public String getSkinSignature() {
        return "iodzeE5dppWs8fCTE9eRlGHPc7cG/sgL8itL75jPpUDmgqDB1FJuXR1NOvY2B+rkFzqusOniMrUkuPbDinNy6rxk7i4uo7kWgoBUuQT4PoBjGlEc+VJR70vJHkS8i+0L4u4z5h1O84lsboGZ4mSHM4Y2zdDENq5P34we0Ta5YeXETTB55U/312XhtXKm2qebQxTi6vbVCV7ejHaSKGAoKqbnROaZ7KIAqP5vLzHJ3shYNSwMq38gPG0CkiXKDCIrwHi/5Q1XkAovPgeixcY/zw6hTFPmD0+Qy9Rx3xl2Mi/gzQZ66EHvhBg7hTBCu5j6r+c4rz0CDvYT/YXalixYe6sAUGIXhiE5TlqGfedGLbJyJlh5plvHBLSMVQrEbjQ3LM84p8/CVNWTr5nRLoOekzQJVVVNQy229iKekr+BwTzhDdQccmFHR7Bsk0+7Nu4Vm2R+t1zm7qe+p0qwA70F0ty3nEuSpFiJuk5IQ3YCMmWfMxtcZ6idg8fEALegKeciPiVGw47osyFAUGU+XQZabGeDF9+34II5zGvu5Ury9XUw8b6eklp72zt3ETBGwreK5yofNQ9lhMMykj8Lzmjs6cv0Q6l5FJy20HXT57igB7oiMEQL6ouT4BN98Rz44HwrnrOR3cIdCsE6UFit9qYqCfdt88pPHL4otk8CLpbhqdc=";
    }

    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTYzNDM1ODQzODQ2OSwKICAicHJvZmlsZUlkIiA6ICI4MmM2MDZjNWM2NTI0Yjc5OGI5MWExMmQzYTYxNjk3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3ROb3RvcmlvdXNOZW1vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JlMTcyMGE2OTIxZTZmNWI2NmUwOGUwODk0ZjhlZjQ4NmIzNDlmMjU5ZGQ5OTNkNjlhYmRlZjA1YWM0OWExNzUiCiAgICB9CiAgfQp9";
    }
}
