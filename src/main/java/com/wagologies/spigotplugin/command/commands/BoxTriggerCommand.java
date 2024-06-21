package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.triggers.BoxTrigger;
import com.wagologies.spigotplugin.command.PlayerCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class BoxTriggerCommand extends PlayerCommand {
    public BoxTriggerCommand(SpigotPlugin plugin) {
        super(plugin, "boxtrigger");
    }

    public Location first = null;
    public BoxTrigger boxTrigger = null;

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if (strings.length != 1) {
            player.sendMessage(ChatColor.RED + "Missing args");
            return true;
        }

        if (strings[0].equals("0")) {
            if (boxTrigger != null) {
                boxTrigger.visualize();
            }
            return true;
        }

        if (strings[0].equals("1")) {
            first = player.getLocation();
            return true;
        }

        if (strings[0].equals("2")) {
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
            return true;
        }

        if(strings[0].equals("3")) {
            if(boxTrigger != null) {
                BoundingBox box = boxTrigger.getBox();
                String boxData = String.format("%.2f, %.2f, %.2f, %.2f, %.2f, %.2f", box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
                player.sendMessage("Box trigger data:");
                TextComponent clickableText = new TextComponent(boxData);
                clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, boxData));
                player.spigot().sendMessage(clickableText);
            }
            return true;
        }

        return true;
    }
}
