package com.wagologies.spigotplugin.npc;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Conversation {
    private final List<ConversationStep> steps;
    private final List<RPGPlayer> listeningPlayers = new ArrayList<>();
    private int currentStep = -1;
    private boolean isRunning = false;
    public Conversation(ConversationStep ...steps) {
        this.steps = List.of(steps);
    }

    private void runConversation(NPC npc, SpigotPlugin plugin) {
        int totalTime = 0;
        this.currentStep = -1;
        this.isRunning = true;
        for (Conversation.ConversationStep step : getSteps()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                currentStep += 1;
                step.run(listeningPlayers, npc, this);
            }, totalTime);
            totalTime += 10; //step.getDuration();
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
        if(listeningPlayers.contains(player)) {
            return;
        }
        listeningPlayers.add(player);
        player.setInConversation(true);
        if(isRunning) {
            for (int i = 0; i <= currentStep; i++) {
                ConversationStep step = getSteps().get(i);
                if(step instanceof Speak speak) {
                        speak.speakToPlayer(player, npc, this);
                }
                if(step instanceof PlayerLookAtNPC lookAtNPC) {
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

    public static class Speak extends ConversationStep {
        private String text;
        @Nullable
        private String customName;
        @Nullable
        private Integer customDuration;

        public Speak(String text) {
            this(text, null, null);
        }

        public Speak(String text, @Nullable String customName) {
            this(text, customName, null);
        }
        public Speak(String text, @Nullable Integer customDuration) {
            this(text, null, customDuration);
        }

        public Speak(String text, @Nullable String customName, @Nullable Integer customDuration) {
            this.text = text;
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
            List<Speak> speakSteps = conversation.getSteps().stream().filter(step -> step instanceof Speak).map(step -> (Speak)step).toList();
            int index = speakSteps.indexOf(this);
            String formattedMessage = formatText(player, npc, conversation);
            if(index != 0) {
                player.getPlayer().sendMessage("");
            }
            if(customName == null) {
                npc.speakToPlayer(player.getPlayer(), formattedMessage, index + 1, speakSteps.size());
            } else {
                npc.speakToPlayer(player.getPlayer(), formattedMessage, index + 1, speakSteps.size(), customName);
            }
        }

        public String formatText(RPGPlayer player, NPC npc, Conversation conversation) {
            record Replacer(@RegEx String regex, String replacement) {}
            Replacer[] replacers = new Replacer[] {
                    new Replacer("\\{player}", player.getName())
            };
            String formatted = text;
            for (Replacer replacer : replacers) {
                formatted = formatted.replaceAll(replacer.regex, replacer.replacement);
            }
            return formatted;
        }

        @Override
        public int getDuration() {
            if(customDuration != null) {
                return customDuration;
            }
            int wordCount = text.length() - text.replaceAll(" ", "").length() + 1;
            return Math.round((wordCount / ((float)1/6))) + 10;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

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
            averageLocation.multiply(1/players.size());
            World world = npc.getLocation().getWorld();
            assert world != null;
            Vector toPlayer = averageLocation.subtract(npc.getLocation().toVector());
            double length = toPlayer.length();
            Location target = npc.getLocation().add(toPlayer.multiply(Math.max(0,length - 3)/length));
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
                playerLoc.setYaw((float) playerLoc.getYaw() - (float) Math.atan(dz / dx));
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
