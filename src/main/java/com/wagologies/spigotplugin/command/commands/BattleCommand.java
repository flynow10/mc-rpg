package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.battle.Battle;
import com.wagologies.spigotplugin.battle.BattleManager;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BattleCommand extends PlayerCommand {
    public BattleCommand(SpigotPlugin plugin) {
        super(plugin, "battle");
    }

    private static final String[] SUB_COMMANDS = {"create", "addspawn", "list", "show", "start"};
    private static final String[] REQUIRE_BATTLE = {"addspawn", "show", "start"};

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length < 1) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            return true;
        }
        if(Arrays.stream(SUB_COMMANDS).noneMatch(cmd -> Objects.equals(strings[0], cmd))) {
            player.sendMessage(ChatColor.RED + "Invalid sub command!");
            return true;
        }
        Battle battle = null;
        if(Arrays.stream(REQUIRE_BATTLE).anyMatch(cmd -> cmd.equals(strings[0]))) {
            if(strings.length < 2) {
                player.sendMessage(ChatColor.RED + "Missing battle identifier!");
                return true;
            }
            try {
                battle = getBattleManager().getBattle(strings[1]);
            } catch (RuntimeException e) {
                player.sendMessage(ChatColor.RED + "That battle does not exist!");
                return true;
            }
        }

        switch (strings[0]) {
            case "create": {
                createBattle(player, strings);
                break;
            }
            case "addspawn": {
                addSpawn(player, strings, battle);
                break;
            }
            case "show": {
                showSpawns(player, strings, battle);
                break;
            }
            case "list": {
                list(player, strings);
                break;
            }
            case "start": {
                start(player, strings, battle);
                break;
            }
            default: {
                player.sendMessage(ChatColor.RED + "This method has not been implemented yet!");
            }
        }
        return true;
    }
    public void list(Player player, String[] strings) {
        List<String> output = new ArrayList<>();
        output.add(ChatColor.GRAY + "==== Battles ====");
        List<Battle> battles = getBattleManager().getBattles();
        for(Battle battle : battles) {
            output.add(ChatColor.GREEN + battle.getName() + ChatColor.GRAY + ": \"" + battle.getBattleId() + "\"; " + ChatColor.YELLOW + battle.getSpawnLocations().size() + " locations");
        }
        for (String line : output) {
            player.sendMessage(line);
        }

    }

    public void createBattle(Player player, String[] strings) {
        if(strings.length < 2) {
            player.sendMessage(ChatColor.RED + "Missing name for battle!");
            return;
        }
        String name = strings[1];
        getBattleManager().createBattle(name);
        player.sendMessage(ChatColor.GREEN + "Successfully created a new battle!");
    }

    public void addSpawn(Player player, String[] strings, Battle battle) {
        Location loc = player.getLocation();
        battle.addSpawnLocation(loc);
        player.sendMessage(ChatColor.GREEN + "Added new spawn location to " + ChatColor.YELLOW + battle.getName() + ChatColor.GREEN + " at " + StringHelper.locationToString(loc));
    }

    public void start(Player player, String[] strings, Battle battle) {
        battle.start();
        player.sendMessage(ChatColor.GREEN + "Successfully started battle " + battle.getName());
    }

    public void showSpawns(Player player, String[] strings, Battle battle) {
        List<Battle.SpawnLocation> spawnLocations = battle.getSpawnLocations();
        List<EnderSignal> signals = new ArrayList<>();
        for (Battle.SpawnLocation spawnLocation : spawnLocations) {
            Location location = spawnLocation.getLocation();
            World world = location.getWorld();
            if(world == null) {
                world = Bukkit.getWorlds().getFirst();
            }
            EnderSignal signal = (EnderSignal) world.spawnEntity(location, EntityType.ENDER_SIGNAL);
            signal.setTargetLocation(location);
            signal.setDespawnTimer(-10000);
            signal.setDropItem(false);
            signals.add(signal);
        }
        player.sendMessage(ChatColor.GREEN + "Displaying spawn locations for 20 seconds");
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (EnderSignal signal : signals) {
                signal.remove();
            }
            player.sendMessage(ChatColor.GREEN + "Spawn location are now hidden");
        }, 400);
    }



    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of(SUB_COMMANDS), completions);
        }
        if(args.length == 2 && Arrays.stream(REQUIRE_BATTLE).anyMatch(cmd -> cmd.equals(args[0]))) {
            List<Battle> battles = getBattleManager().getBattles();
            StringUtil.copyPartialMatches(args[1], battles.stream().map(Battle::getName).toList(), completions);
        }
        return completions;
    }

    private BattleManager getBattleManager() {
        return this.plugin.getBattleManager();
    }
}
