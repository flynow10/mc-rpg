package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.Wand;
import com.wagologies.spigotplugin.item.WandCoreType;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.PlayerMob;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class IceMage extends PlayerMob {
    public IceMage(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(0, 0,0,0,0,0);
    }

    @Override
    public MobType getType() {
        return MobType.ICE_MAGE;
    }

    @Override
    public int getMaxHealth() {
        return 100;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        Wand wand = new Wand(plugin, new ItemStack(Material.STICK));
        wand.setCoreType(WandCoreType.ENCHANTED_STRING);
        return wand;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[0];
    }

    @Override
    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY4ODk5OTM1Nzc5NSwKICAicHJvZmlsZUlkIiA6ICJiMTRiMjY2NzgxOTU0ZGM1OTUzYTRkYWQ5MjRiZGRjNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHOWxfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FiMTZlYmU3NzBlNTkyMzI0MWFlN2YzYzA5YjFkNzJlZWFkMTMwY2U0YjI0NzA5MmM4OTJkOGNjNGIwMGI2ZSIKICAgIH0KICB9Cn0=";
    }

    @Override
    public String getSkinSignature() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY4ODk5OTM1Nzc5NSwKICAicHJvZmlsZUlkIiA6ICJiMTRiMjY2NzgxOTU0ZGM1OTUzYTRkYWQ5MjRiZGRjNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHOWxfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FiMTZlYmU3NzBlNTkyMzI0MWFlN2YzYzA5YjFkNzJlZWFkMTMwY2U0YjI0NzA5MmM4OTJkOGNjNGIwMGI2ZSIKICAgIH0KICB9Cn0=";
    }
}
