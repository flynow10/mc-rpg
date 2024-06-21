package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.item.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemCommand extends PlayerCommand {
    public ItemCommand(SpigotPlugin plugin) {
        super(plugin, "item");
    }
    private static final String[] SUB_COMMANDS = {"give", "identify"};
    private static final String[] ITEM_TYPES = {"wand", "sword", "armor"};

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        if(strings.length == 0) {
            player.sendMessage(ChatColor.RED + "Missing subcommand");
            return true;
        }

        switch (strings[0]) {
            case "give": {
                if (strings[1].equals("wand")) {
                    Wand wand = new Wand(plugin, new ItemStack(Material.STICK));
                    wand.setCoreType(WandCoreType.ENCHANTED_STRING);
                    player.getInventory().addItem(wand.getItemStack());
                }
                if(strings[1].equals("sword")) {
                    MeleeWeapon meleeWeapon = new MeleeWeapon(plugin, new ItemStack(Material.IRON_SWORD));
                    int baseDamage = 10;
                    try {
                        if(strings.length >= 3) {
                            baseDamage = Integer.parseInt(strings[2]);
                        }
                    } catch (NumberFormatException ignored) {}
                    meleeWeapon.setBaseDamage(baseDamage);
                    player.getInventory().addItem(meleeWeapon.getItemStack());
                }
                if(strings[1].equals("armor")) {
                    Armor armor = new Armor(plugin, new ItemStack(Material.IRON_CHESTPLATE));
                    int armorClass = 1;
                    try {
                        if(strings.length >= 3) {
                            armorClass = Integer.parseInt(strings[2]);
                        }
                    } catch (NumberFormatException ignored) {}
                    armor.setArmorClass(armorClass);
                    armor.setWeight(armorClass*3);
                    player.getInventory().addItem(armor.getItemStack());
                }
                break;
            }
            case "identify": {
                ItemStack holdingItem = player.getInventory().getItemInMainHand();
                if(holdingItem.getType().isAir()) {
                    player.sendMessage(ChatColor.RED + "You aren't holding anything!");
                    return true;
                }
                RPGItem rpgItem = RPGItem.ConvertToCustomItem(plugin, holdingItem);
                assert rpgItem != null;
                ItemType itemType = rpgItem.getItemType();
                player.sendMessage("You are holding a " + itemType);
                break;
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of(SUB_COMMANDS), completions);
        }
        if(args.length == 2) {
            StringUtil.copyPartialMatches(args[1], List.of(ITEM_TYPES), completions);
        }
        return completions;
    }
}
