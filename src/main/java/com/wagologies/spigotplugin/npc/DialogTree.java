package com.wagologies.spigotplugin.npc;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.event.ConversationInteractionEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DialogTree extends Conversation.InteractionStep {

    public static String EXIT_NODE = "__exit";

    private final List<Node> nodes;
    private String currentNodeId;
    private RPGPlayer player;
    private NPC npc;
    private Conversation parentConversation;

    public DialogTree(SpigotPlugin plugin, Node...nodes) {
        super(plugin);
        this.nodes = List.of(nodes);
        this.currentNodeId = this.nodes.getFirst().nodeId;
    }

    @Override
    protected void sendInteraction(RPGPlayer player, NPC npc, Conversation conversation) {
        this.player = player;
        this.npc = npc;
        this.parentConversation = conversation;
        runNode(getCurrentNode());
    }

    protected void runNode(String nodeId) {
        if(nodeId.equals(DialogTree.EXIT_NODE)) {
            this.finishedInteraction();
            return;
        }
        runNode(getNodeFromId(nodeId));
    }

    protected void runNode(Node node) {
        this.currentNodeId = node.nodeId;
        node.run(plugin, this, player, npc, parentConversation);
    }

    @Override
    protected void onReceiveInteraction(ConversationInteractionEvent event) {
        Node currentNode = getCurrentNode();
        if(currentNode instanceof OptionNode optionNode) {
            boolean didAccept = optionNode.acceptOption(event.getResponse(), this, event.getPlayer());
            if(!didAccept) {
                event.getPlayer().getPlayer().sendMessage(ChatColor.RED + "This option has already been used or has expired!");
            }
        }
    }

    protected Node getNodeFromId(String nodeId) {
        return this.nodes.stream().filter(node -> node.nodeId.equals(nodeId)).findFirst().orElseThrow();
    }

    protected Node getCurrentNode() {
        return getNodeFromId(this.currentNodeId);
    }

    public static abstract class Node {
        public final String nodeId;

        public Node(String nodeId) {
            this.nodeId = nodeId;
        }

        public abstract void run(SpigotPlugin plugin, DialogTree dialogTree, RPGPlayer player, NPC npc, Conversation conversation);
    }

    public static class ConversationNode extends Node {
        protected final List<Conversation.ConversationStep> steps;
        protected final String nextNode;

        public ConversationNode(String nodeId, String nextNode, Conversation.ConversationStep... steps) {
            super(nodeId);
            this.steps = List.of(steps);
            this.nextNode = nextNode;
            for (Conversation.ConversationStep step : steps) {
                if(step instanceof Conversation.InteractionStep) {
                    throw new IllegalStateException("Cannot include nest dialog tree within itself");
                }
            }
        }

        @Override
        public void run(SpigotPlugin plugin, DialogTree dialogTree, RPGPlayer player, NPC npc, Conversation conversation) {
            int totalTime = 0;
            for (Conversation.ConversationStep step : steps) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    step.run(List.of(player), npc, conversation);
                }, totalTime);
                totalTime += step.getDuration();
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dialogTree.runNode(nextNode);
            }, totalTime);
        }
    }

    public static class OptionNode extends Node {
        protected final String prompt;
        protected final boolean numbered;
        protected final List<Option> options;

        public OptionNode(String nodeId, Option... steps) {
            this(nodeId, true, steps);
        }

        public OptionNode(String nodeId, boolean numbered, Option... steps) {
            this(nodeId, "Options:", numbered, steps);
        }

        public OptionNode(String nodeId, String prompt, boolean numbered, Option... options) {
            super(nodeId);
            this.prompt = prompt;
            this.numbered = numbered;
            this.options = List.of(options);
        }

        @Override
        public void run(SpigotPlugin plugin, DialogTree dialogTree, RPGPlayer player, NPC npc, Conversation conversation) {
            Player bukkitPlayer = player.getPlayer();
            bukkitPlayer.sendMessage("");

            if(prompt != null && !prompt.isEmpty()) {
                bukkitPlayer.sendMessage(this.prompt);
            }

            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                BaseComponent optionMessage = getOptionChatMessage(option, i + 1, dialogTree.getCommandBase());
                bukkitPlayer.spigot().sendMessage(optionMessage);
            }
            bukkitPlayer.sendMessage("");
        }

        public BaseComponent getOptionChatMessage(Option option, int optionNumber, String baseCommand) {
            ComponentBuilder optionBuilder = new ComponentBuilder();
            if(numbered) {
                optionBuilder.append(optionNumber + ". ");
            } else {
                optionBuilder.append(" - ");
            }
            optionBuilder.color(net.md_5.bungee.api.ChatColor.GREEN);
            String optionCommand = baseCommand + option.optionId.toString();
            optionBuilder
                    .append("[" + option.optionName + "]")
                    .color(option.getColor())
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, optionCommand))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(option.optionName)));
            return optionBuilder.build();
        }

        public boolean acceptOption(String optionId, DialogTree dialogTree, RPGPlayer player) {
            for(Option option : options) {
                if(option.optionId.toString().equals(optionId)) {
                    if(!option.nextNodeId.equals(DialogTree.EXIT_NODE)) {
                        player.getPlayer().sendMessage(ChatColor.GRAY + "[" + player.getName() +"]: " + ChatColor.YELLOW + option.optionName);
                    }
                    dialogTree.runNode(option.nextNodeId);
                    return true;
                }
            }
            return false;
        }
    }

    public static class Option {
        private final UUID optionId;
        private final String optionName;
        private final String nextNodeId;
        private final ChatColor color;

        public Option(String optionName, String nextNodeId) {
            this(optionName, nextNodeId, ChatColor.GREEN);
        }

        public Option(String optionName, String nextNodeId, ChatColor color) {
            this.optionId = UUID.randomUUID();
            this.optionName = optionName;
            this.nextNodeId = nextNodeId;
            this.color = color;
        }

        protected net.md_5.bungee.api.ChatColor getColor() {
            return net.md_5.bungee.api.ChatColor.getByChar(color.getChar());
        }
    }
}