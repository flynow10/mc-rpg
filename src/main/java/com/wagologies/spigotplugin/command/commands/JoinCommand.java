package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.command.BaseCommand;
import com.wagologies.spigotplugin.command.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class JoinCommand extends PlayerCommand {
    public JoinCommand(SpigotPlugin plugin) {
        super(plugin, "join");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length != 1) {
            player.sendMessage(ChatColor.RED + "Missing campaign name!");
            return true;
        }
        Campaign campaign = plugin.getCampaignManager().getCampaigns().stream().filter(c -> c.getName().equals(strings[0])).findFirst().orElse(null);
        if(campaign == null) {
            player.sendMessage(ChatColor.RED + "Campaign does not exist!");
            return true;
        }
        campaign.joinPlayer(player);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], plugin.getCampaignManager().getCampaigns().stream().map(Campaign::getName).toList(), completions);
        }
        return completions;
    }
}
