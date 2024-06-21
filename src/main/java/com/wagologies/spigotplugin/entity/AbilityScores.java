package com.wagologies.spigotplugin.entity;

import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Supplier;

public class AbilityScores {
    private int dexterity = 0;
    private int strength = 0;
    private int constitution = 0;
    private int intelligence = 0;
    private int wisdom = 0;
    private int charisma = 0;


    public int getDexterity() {
        return dexterity;
    }

    public int getStrength() {
        return strength;
    }

    public int getConstitution() {
        return constitution;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public int getScore(AbilityScore score) {
        switch (score) {
            case STRENGTH -> {
                return getStrength();
            }
            case DEXTERITY -> {
                return getDexterity();
            }
            case CONSTITUTION -> {
                return getConstitution();
            }
            case INTELLIGENCE -> {
                return getIntelligence();
            }
            case WISDOM -> {
                return getWisdom();
            }
            case CHARISMA -> {
                return getCharisma();
            }
            default -> throw new IllegalStateException("Unexpected value: " + score);
        }
    }

    public void setScore(AbilityScore type, int score) {
        switch (type) {
            case STRENGTH -> setStrength(score);
            case DEXTERITY -> setDexterity(score);
            case CONSTITUTION -> setConstitution(score);
            case INTELLIGENCE -> setIntelligence(score);
            case WISDOM -> setWisdom(score);
            case CHARISMA -> setCharisma(score);
            default -> throw new IllegalStateException("Unexpected value: " + score);
        }
    }

    public int getModifier(AbilityScore score) {
        return getModifier(getScore(score));
    }

    public int getModifier(int score) {
        return Math.floorDiv(score - 10, 2);
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public void setScores(int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this.setStrength(strength);
        this.setDexterity(dexterity);
        this.setConstitution(constitution);
        this.setIntelligence(intelligence);
        this.setWisdom(wisdom);
        this.setCharisma(charisma);
    }

    public enum AbilityScore {
        STRENGTH(ChatColor.RED, () -> {
            ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.RED + "Strength");
            meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nStrength measures bodily power, athletic training, and the extent to which you can exert raw physical force."), ChatColor.GRAY.toString()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
            return itemStack;
        }),
        DEXTERITY(ChatColor.WHITE, () -> {
            ItemStack itemStack = new ItemStack(Material.IRON_BOOTS);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + "Dexterity");
            meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nDexterity measures agility, reflexes, and balance."), ChatColor.GRAY.toString()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
            return itemStack;
        }),
        CONSTITUTION(ChatColor.DARK_RED, () -> {
            ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.DARK_RED + "Constitution");
            meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nConstitution measures health, stamina, and vital force."), ChatColor.GRAY.toString()));
            itemStack.setItemMeta(meta);
            return itemStack;
        }),
        INTELLIGENCE(ChatColor.AQUA, () -> {
            ItemStack itemStack = new ItemStack(Material.BOOK);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.AQUA + "Intelligence");
            meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nIntelligence measures mental acuity, accuracy of recall, and the ability to reason."), ChatColor.GRAY.toString()));
            itemStack.setItemMeta(meta);
            return itemStack;
        }),
        WISDOM(ChatColor.BLUE, () -> {
            ItemStack itemStack = new ItemStack(Material.ENDER_EYE);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.BLUE + "Wisdom");
            meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nWisdom reflects how attuned you are to the world around you and represents perceptiveness and intuition."), ChatColor.GRAY.toString()));
            itemStack.setItemMeta(meta);
            return itemStack;
        }),
        CHARISMA(ChatColor.GOLD, () -> {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GOLD + "Charisma");
            meta.setLore(StringHelper.prependWithColor(StringHelper.wrapItemLore("\nCharisma measures your ability to interact effectively with others. It includes such factors as confidence and eloquence, and it can represent a charming or commanding personality."), ChatColor.GRAY.toString()));
            itemStack.setItemMeta(meta);
            return itemStack;
        });

        private final Supplier<ItemStack> itemSupplier;
        private final ChatColor color;

        AbilityScore(ChatColor color, Supplier<ItemStack> itemSupplier) {
            this.color = color;
            this.itemSupplier = itemSupplier;
        }

        public ChatColor getColor() {
            return color;
        }

        public ItemStack getItemStack() {
            return itemSupplier.get();
        }
    }
}
