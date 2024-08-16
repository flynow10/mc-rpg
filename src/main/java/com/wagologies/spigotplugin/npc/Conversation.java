package com.wagologies.spigotplugin.npc;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.event.ConversationInteractionEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Conversation {
    private final List<ConversationStep> steps;
    private final List<RPGPlayer> listeningPlayers = new ArrayList<>();
    private int currentStep = -1;
    private boolean isRunning = false;

    public Conversation(ConversationStep... steps) {
        this.steps = List.of(steps);
    }

    private void runConversation(NPC npc, SpigotPlugin plugin) {
        this.currentStep = -1;
        this.isRunning = true;
        runConversationPart(npc, plugin, 0);
    }

    private void runConversationPart(NPC npc, SpigotPlugin plugin, int startFrom) {
        int totalTime = 0;
        for (int i = startFrom; i < steps.size(); i++) {
            ConversationStep step = steps.get(i);
            if (step instanceof InteractionStep interactionStep) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    currentStep += 1;
                    interactionStep.addAfterInteractionListener(() -> runConversationPart(npc, plugin, currentStep + 1));
                    interactionStep.run(listeningPlayers, npc, this);
                }, totalTime);
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                currentStep += 1;
                step.run(listeningPlayers, npc, this);
            }, totalTime);
            totalTime += step.getDuration();
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (RPGPlayer rpgPlayer : listeningPlayers) {
                rpgPlayer.setInConversation(false);
            }
            listeningPlayers.clear();
            this.isRunning = false;
            this.currentStep = -1;
        }, totalTime);
    }

    public void addPlayer(RPGPlayer player, NPC npc, SpigotPlugin plugin) {
        if (listeningPlayers.contains(player)) {
            return;
        }
        listeningPlayers.add(player);
        player.setInConversation(true);
        if (isRunning) {
            for (int i = 0; i <= currentStep; i++) {
                ConversationStep step = getSteps().get(i);
                if (step instanceof Speak speak) {
                    speak.speakToPlayer(player, npc, this);
                }
                if (step instanceof PlayerLookAtNPC lookAtNPC) {
                    lookAtNPC.playerLook(player, npc);
                }
            }
        } else {
            runConversation(npc, plugin);
        }
    }

    public List<ConversationStep> getSteps() {
        return steps;
    }

    public static abstract class ConversationStep {
        public abstract void run(List<RPGPlayer> players, NPC npc, Conversation conversation);

        public abstract int getDuration();
    }

    public static abstract class InteractionStep extends ConversationStep implements Listener {
        protected final SpigotPlugin plugin;
        protected final String interactionId;
        private final List<Runnable> listeners = new ArrayList<>();

        public InteractionStep(SpigotPlugin plugin) {
            this.plugin = plugin;
            interactionId = UUID.randomUUID().toString();
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        protected abstract void sendInteraction(RPGPlayer player, NPC npc, Conversation conversation);

        protected abstract void onReceiveInteraction(ConversationInteractionEvent event);

        protected void finishedInteraction() {
            for (Runnable runnable : listeners) {
                runnable.run();
            }
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void _internalReceiveInteraction(ConversationInteractionEvent event) {
            if (event.getInteractionId().equals(interactionId)) {
                onReceiveInteraction(event);
            }
        }

        public void addAfterInteractionListener(Runnable runnable) {
            listeners.add(runnable);
        }

        public String getCommandBase() {
            return "/conversation " + this.interactionId + " ";
        }

        @Override
        public void run(List<RPGPlayer> players, NPC npc, Conversation conversation) {
            if (players.size() != 1) {
                throw new RuntimeException("Interactions can only have one player!");
            }

            sendInteraction(players.getFirst(), npc, conversation);
        }

        @Override
        public int getDuration() {
            return -1;
        }
    }

    public static abstract class AbstractSpeak extends ConversationStep {
        @Nullable
        private String customName;
        @Nullable
        private Integer customDuration;

        public AbstractSpeak() {
            this(null, null);
        }

        public AbstractSpeak(@Nullable String customName) {
            this(customName, null);
        }

        public AbstractSpeak(@Nullable Integer customDuration) {
            this(null, customDuration);
        }

        public AbstractSpeak(@Nullable String customName, @Nullable Integer customDuration) {
            this.customName = customName;
            this.customDuration = customDuration;
        }

        @Override
        public void run(List<RPGPlayer> players, NPC npc, Conversation conversation) {
            for (RPGPlayer player : players) {
                speakToPlayer(player, npc, conversation);
            }
        }

        public void speakToPlayer(RPGPlayer player, NPC npc, Conversation conversation) {
            boolean shouldUseMessageCount = conversation.getSteps()
                    .stream()
                    .noneMatch(step -> step instanceof DialogTree);

            String formattedMessage = formatText(player, npc, conversation);

            if(!shouldUseMessageCount) {
                player.getPlayer().sendMessage("");
                if (customName == null) {
                    npc.speakToPlayer(player.getPlayer(), formattedMessage);
                } else {
                    npc.speakToPlayer(player.getPlayer(), formattedMessage, customName);
                }
                return;
            }

            List<AbstractSpeak> speakSteps = conversation.getSteps()
                    .stream()
                    .filter(step -> step instanceof AbstractSpeak)
                    .map(step -> (AbstractSpeak) step)
                    .toList();
            int index = speakSteps.indexOf(this);

            if (index != 0) {
                player.getPlayer().sendMessage("");
            }
            if (customName == null) {
                npc.speakToPlayer(player.getPlayer(), formattedMessage, index + 1, speakSteps.size());
            } else {
                npc.speakToPlayer(player.getPlayer(), formattedMessage, index + 1, speakSteps.size(), customName);
            }
        }

        public String formatText(RPGPlayer player, NPC npc, Conversation conversation) {
            record Replacer(@RegEx String regex, String replacement) {
            }
            Replacer[] replacers = new Replacer[]{
                    new Replacer("\\{player}", player.getName())
            };
            String formatted = getText();
            for (Replacer replacer : replacers) {
                formatted = formatted.replaceAll(replacer.regex, replacer.replacement);
            }
            return formatted;
        }

        @Override
        public int getDuration() {
            if (customDuration != null) {
                return customDuration;
            }
            int wordCount = getText().length() - getText().replaceAll(" ", "").length() + 1;
            return Math.round((wordCount / ((float) 1 / 6))) + 10;
        }

        public abstract String getText();

        @Nullable
        public String getCustomName() {
            return customName;
        }

        public void setCustomName(@Nullable String customName) {
            this.customName = customName;
        }

        @Nullable
        public Integer getCustomDuration() {
            return customDuration;
        }

        public void setCustomDuration(@Nullable Integer customDuration) {
            this.customDuration = customDuration;
        }
    }

    public static class Speak extends AbstractSpeak {
        private final String text;

        public Speak(String text) {
            this(text, null, null);
        }

        public Speak(String text, @Nullable Integer customDuration) {
            this(text, null, customDuration);
        }

        public Speak(String text, @Nullable String customName) {
            this(text, customName, null);
        }

        public Speak(String text, @Nullable String customName, @Nullable Integer customDuration) {
            super(customName, customDuration);
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    public static class SuspendedSpeak extends AbstractSpeak {
        private final Supplier<String> speechProvider;

        public SuspendedSpeak(Supplier<String> speechProvider) {
            this(speechProvider, null, null);
        }

        public SuspendedSpeak(Supplier<String> speechProvider, @Nullable Integer customDuration) {
            this(speechProvider, null, customDuration);
        }

        public SuspendedSpeak(Supplier<String> speechProvider, @Nullable String customName) {
            this(speechProvider, customName, null);
        }

        public SuspendedSpeak(Supplier<String> speechProvider, @Nullable String customName, @Nullable Integer customDuration) {
            super(customName, customDuration);
            this.speechProvider = speechProvider;
        }

        @Override
        public String getText() {
            return speechProvider.get();
        }
    }

    public static class MoveTo extends ConversationStep {
        private Vector toLocation;
        private int duration;

        public MoveTo(Vector toLocation) {
            this(toLocation, 0);
        }

        public MoveTo(Vector toLocation, int duration) {
            this.toLocation = toLocation;
            this.duration = duration;
        }

        @Override
        public void run(List<RPGPlayer> players, NPC npc, Conversation conversation) {
            npc.setTargetLocation(toLocation.toLocation(npc.getCampaign().getWorld()));
        }

        @Override
        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public Vector getToLocation() {
            return toLocation;
        }

        public void setToLocation(Vector toLocation) {
            this.toLocation = toLocation;
        }
    }

    public static class MoveToPlayer extends ConversationStep {
        private int duration;

        public MoveToPlayer() {
            this(0);
        }

        public MoveToPlayer(int duration) {
            this.duration = duration;
        }

        @Override
        public void run(List<RPGPlayer> players, NPC npc, Conversation conversation) {
            Vector averageLocation = new Vector();
            for (RPGPlayer player : players) {
                averageLocation.add(player.getLocation().toVector());
            }
            averageLocation.multiply(1 / players.size());
            World world = npc.getLocation().getWorld();
            assert world != null;
            Vector toPlayer = averageLocation.subtract(npc.getLocation().toVector());
            double length = toPlayer.length();
            Location target = npc.getLocation().add(toPlayer.multiply(Math.max(0, length - 3) / length));
            int highestY = world.getHighestBlockYAt(target);
            target.setY(highestY + 1);

            npc.setTargetLocation(target);
        }

        @Override
        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    public static class PlayerLookAtNPC extends ConversationStep {
        @Override
        public void run(List<RPGPlayer> players, NPC npc, Conversation conversation) {
            for (RPGPlayer player : players) {
                playerLook(player, npc);
            }
        }

        public void playerLook(RPGPlayer player, NPC npc) {
            //Clone the loc to prevent applied changes to the input loc
            Location lookAt = npc.getLocation().clone();
            Location playerLoc = player.getLocation();

            // Values of change in distance (make it relative)
            double dx = lookAt.getX() - playerLoc.getX();
            double dy = lookAt.getY() - playerLoc.getY();
            double dz = lookAt.getZ() - playerLoc.getZ();

            // Set yaw
            if (dx != 0) {
                // Set yaw start value based on dx
                if (dx < 0) {
                    playerLoc.setYaw((float) (1.5 * Math.PI));
                } else {
                    playerLoc.setYaw((float) (0.5 * Math.PI));
                }
                playerLoc.setYaw(playerLoc.getYaw() - (float) Math.atan(dz / dx));
            } else if (dz < 0) {
                playerLoc.setYaw((float) Math.PI);
            }

            // Get the distance from dx/dz
            double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

            // Set pitch
            playerLoc.setPitch((float) -Math.atan(dy / dxz));

            // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
            playerLoc.setYaw(-playerLoc.getYaw() * 180f / (float) Math.PI);
            playerLoc.setPitch(playerLoc.getPitch() * 180f / (float) Math.PI);

            player.getPlayer().teleport(playerLoc);
        }

        @Override
        public int getDuration() {
            return 0;
        }
    }

    public static class YesNo extends InteractionStep {

        public final Consumer<RPGPlayer> onAccept;
        public final Consumer<RPGPlayer> onDeny;
        public final String acceptText;
        public final String denyText;

        public YesNo(SpigotPlugin plugin, Consumer<RPGPlayer> onAccept, Consumer<RPGPlayer> onDeny) {
            this(plugin, onAccept, onDeny, "Accept", "Deny");
        }

        public YesNo(SpigotPlugin plugin, Consumer<RPGPlayer> onAccept, Consumer<RPGPlayer> onDeny, String acceptText, String denyText) {
            super(plugin);
            this.onAccept = onAccept;
            this.onDeny = onDeny;
            this.acceptText = acceptText;
            this.denyText = denyText;
        }

        @Override
        protected void sendInteraction(RPGPlayer player, NPC npc, Conversation conversation) {
            String acceptCommand = getCommandBase() + "yes";
            String denyCommand = getCommandBase() + "no";
            player.getPlayer().sendMessage("");
            BaseComponent chatQuestion = new ComponentBuilder()
                    .append("   ")
                    .append("[" + acceptText + "]")
                    .color(net.md_5.bungee.api.ChatColor.GREEN).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, acceptCommand))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(acceptText)))
                    .append("   ")
                    .append("[" + denyText + "]")
                    .color(net.md_5.bungee.api.ChatColor.RED).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, denyCommand))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(denyText)))
                    .build();
            player.getPlayer().spigot().sendMessage(chatQuestion);
            player.getPlayer().sendMessage("");
        }

        @Override
        protected void onReceiveInteraction(ConversationInteractionEvent event) {
            String response = event.getResponse();
            if(response.equals("yes")) {
                onAccept.accept(event.getPlayer());
            } else if(response.equals("no")) {
                onDeny.accept(event.getPlayer());
            } else {
                event.getPlayer().getPlayer().sendMessage(ChatColor.RED + "Invalid response for this interaction!");
                return;
            }
            finishedInteraction();
        }
    }

    public static class CustomRunnable extends ConversationStep {

        public final TriConsumer<List<RPGPlayer>, NPC, Conversation> runnableLambda;
        public final int duration;

        public CustomRunnable(TriConsumer<List<RPGPlayer>, NPC, Conversation> runnableLambda) {
            this(runnableLambda, 0);
        }

        public CustomRunnable(TriConsumer<List<RPGPlayer>, NPC, Conversation> runnableLambda, int duration) {
            this.runnableLambda = runnableLambda;
            this.duration = duration;
        }

        @Override
        public void run(List<RPGPlayer> players, NPC npc, Conversation conversation) {
            this.runnableLambda.accept(players, npc, conversation);
        }

        @Override
        public int getDuration() {
            return duration;
        }
    }
}
