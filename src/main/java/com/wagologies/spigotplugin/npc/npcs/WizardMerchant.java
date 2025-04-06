package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.RPGItemBuilder;
import com.wagologies.spigotplugin.npc.Merchant;
import com.wagologies.spigotplugin.spell.SpellType;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class WizardMerchant extends Merchant {
    public WizardMerchant(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(new Location(campaign.getWorld(), 572.5, 109.2, 792.5));
    }

    @Override
    public ShopItem[] getShopItems() {
        return new ShopItem[] {
                new ShopItem(
                        new RPGItemBuilder(Material.PAPER)
                                .customType(ItemType.SCROLL)
                                .name(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + ChatColor.ITALIC + "Scroll of Wind")
                                .spellType(SpellType.GustOfWind)
                                .build(this.getPlugin()),
                        100
                )
        };
    }

    @Override
    public String getShopName() {
        return "Wizard Merchant";
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("WizardMerchant", getSkinSignature(), getSkinTexture());
    }

    @Override
    public String getName() {
        return "Old Wizard";
    }

    public String getSkinSignature() {
        return "UVR/plHPGPzsETvFJheYMT32bGZSBLtfNXXy1ACZbbM0c+eAH1AyBnYSG6sToxv1X/jaQ0IhkEZfkqPPlBFmScjsUb5yUWJNRAZ1UCxwxz0yhLaJIiTX4EMq87YkLZY1SsMOqZT7LFGWpj0QrSn3fnMk4r48Ub5fRvu/mIpgTVBIVh3mE8zpSiunI55PHYCTmjiIc6J4TwoXExrwv2uK2NWhsEGs/9bjbbyt2NSxPaYAfCXLxU4rNF3s5HWa/JvcA7k7wrDDEDSksqPPkaenxRrMX3s0Vssd+a9hqDrMuqlfodW5eqdMUjs6AqXAq+hP3jkzUlikEljQsx+cDeAfWpoQ5LACjnouPAQeG/MVx256Ih5+bb41ojr5DHR19rwfZkr6uvzAcgoDa8so3522IQb5+7pHrsZMmfvYCHEPBXHTOBo2VvnuKR3KaUQ1m8e4x7Dhbc5x3PPUiIq5M1RZ7QlZPhmAvjo/cAN5klfPUKpt0w9x3iKRHJ7htNQImAz5220Tn22UGirqf8HYsDSFT4g5Lvtpk0kAgHaOJYKcx9wNMkAYerGeE7noStX0greLtDwrXdUTSOC/bSIREjR/DEPaB7ymOpLkPyqZKI//wMB5w/WFdGM1PCXX3lzQbCukkDy8Udq7DBYh6bf8vG+JCyprc6cRGLG9y9scuE1+8ps=";
    }

    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTYwNzI1MTM2Mzg1NiwKICAicHJvZmlsZUlkIiA6ICIyMWUzNjdkNzI1Y2Y0ZTNiYjI2OTJjNGEzMDBhNGRlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZXlzZXJNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZTA2ZTI4ZWZkMjM1N2ZmMDVkNDc1MzVjNDcxYTUwNWZiYTQ1NjMwZmY3MmJmYzYwMmI5MTM5MWIwZTNiMGI0IgogICAgfQogIH0KfQ==";
    }
}
