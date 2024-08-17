package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.campaign.QuestManager;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.npc.Conversation;
import com.wagologies.spigotplugin.npc.DialogTree;
import com.wagologies.spigotplugin.npc.NPC;
import net.citizensnpcs.trait.SitTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Mayor extends NPC {


    public Mayor(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(new Location(campaign.getWorld(), 561.5,113.5,825.5, 180, 0));
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("Mayor", getSkinSignature(), getSkinTexture());
        SitTrait sitting = citizenNPC.getOrAddTrait(SitTrait.class);
        sitting.setSitting(getTargetLocation());
    }

    @Override
    public String getName() {
        return "Mayor";
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {
        getIntroConversation().addPlayer(event.getRPGPlayer(), this, getPlugin());
    }

    private Conversation getIntroConversation() {
        Conversation.ConversationStep[] bandits = new Conversation.ConversationStep[]{
                new Conversation.Speak("I'm very sorry for any trouble they caused you."),
                new Conversation.Speak("We aren't a very large town, so we can't afford to employ many guards."),
                new Conversation.Speak("Ever since the castle on the mountain was built, it seems like we've had more bandit attacks than ever.")
        };
        return new Conversation(
                new DialogTree(getPlugin(),
                        new DialogTree.ConversationNode("welcome", "questions", new Conversation.Speak("How can I help you today?")),
                        new DialogTree.OptionNode("questions", false,
                                new DialogTree.Option("Where am I?", "about-town"),
                                new DialogTree.Option("You should do something about your bandit problem.", "bandits"),
                                new DialogTree.Option("Never mind", DialogTree.EXIT_NODE, ChatColor.GRAY)
                        ),
                        new DialogTree.OptionNode("questions+help", false,
                                new DialogTree.Option("Where am I?", "about-town"),
                                new DialogTree.Option("Do you need help with the castle?", "castle-help"),
                                new DialogTree.Option("You should do something about your bandit problem.", "bandits+help"),
                                new DialogTree.Option("Never mind", DialogTree.EXIT_NODE, ChatColor.GRAY)
                        ),
                        new DialogTree.ConversationNode("about-town", "questions+help",
                                new Conversation.Speak("This is the town of Avalan, located in the northern provinces."),
                                new Conversation.Speak("We are a humble town of around 300 people, but as a rest stop without much defense, we don't get many friendly visitors."),
                                new Conversation.Speak("Unfortunately 3 years ago a mysterious icy figure began building a castle on the mountain, and monster attacks on our town have been getting worse ever since.")
                        ),
                        new DialogTree.ConversationNode("bandits", "questions", bandits),
                        new DialogTree.ConversationNode("bandits+help", "questions+help", bandits),
                        new DialogTree.ConversationNode("castle-help", DialogTree.EXIT_NODE,
                                new Conversation.Speak("We would greatly appreciate any assistance you can give."),
                                new Conversation.Speak("Alas, I fear we won't be able to compensate you for your work."),
                                new Conversation.Speak("If you can free our town from the attacks of the castle, we will give you anything in our power to give."),
                                new Conversation.CustomRunnable((rpgPlayers, npc, conversation) -> this.getCampaign().getQuestManager().setCurrentQuest(QuestManager.Type.StormCastle))
                        )
                )
        );
    }

    public String getSkinSignature() {
        return "keYLnh3ch6eIngwS9mPSYvhlpBqVIVhX01dVcH64AaDYDTL/QWYp8jStS/AcwfXeP8ygodMZ1lgg4e3K8i/JXER1mQt8+7Z4Sq64BWmid+MaRtLRC+glCcoscsdPEo3VgMfgemtCgYNqZWaurrzkKUPD4hmuZsk4nd+XBx1FmPdQPFcuFT3XWP9vXfPtMgais2zYMhTIQAXaledRcYQKPCNkF1WtISz+7rmCvKmSIFg1Q4/5nC+J2YD/eeIFMMIiasZjorYE8WWlut6zxA2RQSqucCJGB4LI3aX+r6Rkmb2/z04LI3jcd4AqCz02WrjTVXdY3XyYi5iOtKKTUUm+glXEMetQfMBH6bB4hl2sqg0tkP5mQKfDBNbKFgrTaRYKQnM4qMMVcb13FAbPK6YVfx3WqL7XvPE2Q1vjL/MzhzmmsPzn28j5JgHKUWeiOseL5NP2p7XF+HF6x33/KHrTko0wVqHKuqfAXDD67BXmvAUN7esB8CZgE2eG6U4ZrtOe9OJcvWS/sbyx4zsHhA0O0uzAlGOgSGaffzbyX4zj+YaUb73e+WzQDbwb+FpFMLmi2/1nU/pfcyhzrbFCy+8nhaQ4yQk6x1ugd0j/rzUa19wd6XjMd95mzpq0GPKVTmA07z/oXIOh2m3LqKxEAt3sv8rBDfDyKkGISeRdieoe4Gk=";
    }

    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwODkwNDA2NTEyNCwKICAicHJvZmlsZUlkIiA6ICIxOWY1YzkwMWEzMjQ0YzVmYTM4NThjZGVhNDk5ZWMwYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2RpdW16aXAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJhNzA5MTE4ZTM4NjFjM2FhMmM1NjEyNzhkNWY2YTk5NWI3YTYxNDEwNjQ5MjM3YjA5ODAxNzhiZmZhNThiYyIKICAgIH0KICB9Cn0=";
    }
}
