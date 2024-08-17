package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.RPGItemBuilder;
import com.wagologies.spigotplugin.npc.Merchant;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class WeaponsMerchant extends Merchant {
    public WeaponsMerchant(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(new Location(campaign.getWorld(), 552.5, 109.2, 788.5));
    }

    @Override
    public ShopItem[] getShopItems() {
        return new ShopItem[] {
                new ShopItem(
                        new RPGItemBuilder(Material.IRON_SWORD)
                                .customType(ItemType.MELEE_WEAPON)
                                .damage(18)
                                .damageType(DamageSource.DamageType.SLICING)
                                .name("Traveler's Blade")
                                .build(this.getPlugin()),
                        50
                ),
                new ShopItem(
                        new RPGItemBuilder(Material.STONE_SWORD)
                                .customType(ItemType.MELEE_WEAPON)
                                .damage(12)
                                .attackSpeed(MeleeWeapon.AttackSpeed.FAST)
                                .damageType(DamageSource.DamageType.SLICING)
                                .name("Small Dagger")
                                .build(this.getPlugin()),
                        75
                ),
                new ShopItem(
                        new RPGItemBuilder(Material.WOODEN_SHOVEL)
                                .customType(ItemType.MELEE_WEAPON)
                                .attackSpeed(MeleeWeapon.AttackSpeed.SLOW)
                                .name("Bludgeoning Club")
                                .damage(25)
                                .damageType(DamageSource.DamageType.BLUNT)
                                .build(this.getPlugin()),
                        35
                )
        };
    }

    @Override
    public String getShopName() {
        return "Weapons Merchant";
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("WeaponMerchant", getSkinSignature(), getSkinTexture());
    }

    @Override
    public String getName() {
        return "Weapons Merchant";
    }

    public String getSkinSignature() {
        return "W4C95fy0hh2y4aeWA9gY72TaWo3RKcoRvzZreVDp5xvX06DnJ0Hd0AmresEBiFUAbeNQcF/MdfYqx33jafb4pNxQnfjy9g9Z3UmExO1nDlNyq1zzXoumSVHKXyUPO/gSbiFz/rg9d/Dfn/kxkozlmJANxzDJbBtbbKdyr6l6DFBp1V8tHdh2b4hJ2BJRUIfKJgWNhfrrCn4MxRkV1bbFDo3sozanz0pMalaEYwFeqnSKKNFcKelYt//BxYe5EV8TgSve+u/eSBmCDMnz5nX+X4ZrWVVMG7ro8o1WHg0fputQG3keONsDS9zN/T+g33Ywd3VMDQP6y+6PpurbZIhz2UYPOf6vBM6XA4+G2GUuSO+liaZXkVZu/Kq0fysyFzO/rh9y4S+OFM+iKbL6GuA5CfswofWnyoRcLa8fOhT8Pjf3pu+LmQK5VuBNqqJDoaXdjnYgVYGcun7fazzhr9IGErUNIrTqPnbgqwoOhQ8WRJnj4m1iRIEJAdq0Yve7mt752XbQIKZ0AGuDIxzjgTKCnYF+2I4nZzCmpZ4+098d5cqYsFlj18byAzvnVh1KWBHhCt09k+PaBSm6h/zlkpkEI+IxeXI+q7yVoWim0k27QZ9hYw+xyp9nnbQMEA2P17kZPiuUosWTLplepw0EBu+b9t93awYYmzOmsBe7pT2kWvg=";
    }

    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwODgyOTE0NzM2MCwKICAicHJvZmlsZUlkIiA6ICJhNWZlYWViNDdhYjA0ZDZiYTk2ZjMyOGJjMDQ3MDZjMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJYeW5kcmEyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzczOGIyYjE1MDkzOWE3OGQ3OTBkOGE3ZTZlZTlmNzQ2N2NmZmEzNzY0NWM4NTU2ZmMzYTA4NTMyZGJmZjM1OGYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==";
    }
}
