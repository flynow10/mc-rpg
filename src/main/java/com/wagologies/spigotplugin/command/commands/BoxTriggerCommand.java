package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.triggers.BoxTrigger;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.command.SubCommands;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class BoxTriggerCommand extends SubCommands {
    private static final String[] SUB_COMMANDS = {"pos1", "pos2", "visualize", "copy"};
    public BoxTriggerCommand(SpigotPlugin plugin) {
        super(plugin, "boxtrigger");
    }

    public Location first = null;
    public BoxTrigger boxTrigger = null;

    @Override
    public boolean subCommandExecutor(Player player, String s, String[] subCommands) {
        if (subCommands.length != 1) {
            player.sendMessage(ChatColor.RED + "Missing args");
            return true;
        }

        switch (subCommands[0]) {
            case "pos1": {
                first = player.getLocation();
                break;
            }
            case "pos2": {
                if (first != null) {
                    if(boxTrigger != null) {
                        boxTrigger.disable();
                    }
                    boxTrigger = new BoxTrigger(plugin, plugin.getCampaignManager().getCampaigns()
                            .getFirst()).withBoxSize(first, player.getLocation())
                            .withActivateMultiple(true)
                            .withCallback((rpgPlayer) -> rpgPlayer.getPlayer()
                                    .sendMessage("You entered a box trigger!"));
                }
                break;
            }
            case "visualize": {
                if (boxTrigger != null) {
                    boxTrigger.visualize();
                }
                break;
            }
            case "copy": {
                if(boxTrigger != null) {
                    BoundingBox box = boxTrigger.getBox();
                    String boxData = String.format("%.2f, %.2f, %.2f, %.2f, %.2f, %.2f", box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
                    player.sendMessage("Box trigger data:");
                    TextComponent clickableText = new TextComponent(boxData);
                    clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, boxData));
                    player.spigot().sendMessage(clickableText);
                }
                break;
            }
        }

        return true;
    }

    @Override
    public String[] getSubCommands(int argNumber, Player player, String[] args) {
        if(argNumber == 1) {
            return SUB_COMMANDS;
        }
        return new String[0];
    }
}
