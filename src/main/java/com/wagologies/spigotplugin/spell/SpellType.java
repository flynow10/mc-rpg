package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.item.WandCoreType;
import com.wagologies.spigotplugin.spell.SpellCast.SpellLine;
import com.wagologies.spigotplugin.spell.spells.Darkness;
import com.wagologies.spigotplugin.spell.spells.EldritchBlast;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum SpellType {
    EldritchBlast("Eldritch Blast", new SpellLine[]{SpellLine.UP, SpellLine.LEFT}, EldritchBlast.class, 15),
    Darkness("Darkness", new SpellLine[]{SpellLine.RIGHT, SpellLine.UP}, Darkness.class, 20);

    private final String name;
    private final List<SpellLine> incantation;
    private final Class<? extends BaseSpell> spellClass;
    private final int manaCost;
    SpellType(String name, SpellLine[] incantation, Class<? extends BaseSpell> spellClass, int manaCost) {
        this.name = name;
        this.incantation = Arrays.asList(incantation);
        this.spellClass = spellClass;
        this.manaCost = manaCost;
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
