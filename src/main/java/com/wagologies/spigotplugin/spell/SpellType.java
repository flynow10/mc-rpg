package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.spell.SpellCast.SpellLine;
import com.wagologies.spigotplugin.spell.spells.*;
import com.wagologies.spigotplugin.utils.StringHelper;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SpellType {
    EldritchBlast("Eldritch Blast", new SpellLine[]{SpellLine.UP, SpellLine.LEFT}, EldritchBlast.class, 15, "A beam of crackling energy streaks toward a creature within range."),
    Darkness("Darkness", new SpellLine[]{SpellLine.RIGHT, SpellLine.UP}, Darkness.class, 24, "Magical darkness spreads from a point you choose within range to fill a 15-foot radius sphere for the duration. The darkness spreads around corners."),
    Jump("Jump", new SpellLine[]{SpellLine.UP_RIGHT, SpellLine.UP_LEFT}, Jump.class, 18, "Your jump height is tripled for the duration."),
    GustOfWind("Gust of Wind", new SpellLine[]{ SpellLine.RIGHT }, GustOfWind.class, 28, "A line of strong wind blasts from you in a direction you choose for the spell's duration. Each creature in the line is pushed back."),
    AuraOfVitality("Aura of Vitality", new SpellLine[]{SpellLine.DOWN, SpellLine.LEFT, SpellLine.UP}, AuraOfVitality.class, 65, "Healing energy radiates from you in an aura. Until the spell ends, friendly creatures within it are healed. This spell requires concentration."),
    MagicMissile("Magic Missile", new SpellLine[] {SpellLine.UP, SpellLine.DOWN}, MagicMissile.class, 42, "You create three glowing darts of magical force. Each dart homes on to a nearby creature and deals damage on impact."),
    MoldEarth("Wall of Stone", new SpellLine[] {SpellLine.UP}, MoldEarth.class, 54, "A nonmagical wall of solid stone springs into existence in front of you.");

    private final String name;
    private final List<SpellLine> incantation;
    private final Class<? extends BaseSpell> spellClass;
    private final int manaCost;
    private final String description;

    SpellType(String name, SpellLine[] incantation, Class<? extends BaseSpell> spellClass, int manaCost, String description) {
        this.name = name;
        this.incantation = Arrays.asList(incantation);
        this.spellClass = spellClass;
        this.manaCost = manaCost;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getColoredName() {
        return ChatColor.LIGHT_PURPLE + name;
    }

    public String getColoredNameAndCost() {
        return getColoredName() + ChatColor.GRAY + " [" + ChatColor.BLUE + "-" + this.getManaCost() + ChatColor.GRAY + "]";
    }

    public List<SpellCast.SpellLine> getIncantation() {
        return incantation;
    }

    public Class<? extends BaseSpell> getSpellClass() {
        return spellClass;
    }

    public int getManaCost() {
        return manaCost;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getLoreText(boolean wrapLore) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + this.getName());
        lore.add("");
        lore.add(ChatColor.BLUE + "Mana Cost: " + ChatColor.BOLD + this.getManaCost());
        lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "Description:");

        if(wrapLore) {
            lore.addAll(StringHelper.prependWithColor(StringHelper.wrapItemLore(this.getDescription()), ChatColor.GRAY.toString()));
        } else {
            lore.add(ChatColor.GRAY + this.getDescription());
        }
        lore.add(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Incantation:");

        StringBuilder incantationString = new StringBuilder();
        List<SpellCast.SpellLine> spellLines = this.getIncantation();
        for (SpellCast.SpellLine spellLine : spellLines) {
            incantationString.append(spellLine.getColor())
                    .append(spellLine.getLineName()).append(", ");
        }
        incantationString.delete(incantationString.length() - 2, incantationString.length());
        if(wrapLore) {
            lore.addAll(StringHelper.wrapItemLore(incantationString.toString()));
        } else {
            lore.add(incantationString.toString());
        }
        return lore;
    }

    @Nullable
    public static SpellType fromString(@Nullable String spellType) {
        if(spellType == null) {
            return null;
        } else {
            try {
                return SpellType.valueOf(spellType);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
    @Nullable
    public static SpellType fromLines(List<SpellLine> spellLines) {
        for (SpellType spellType : values()) {
            List<SpellLine> incantation = spellType.getIncantation();
            if(incantation.equals(spellLines)) {
                return spellType;
            }
        }
        return null;
    }
}
