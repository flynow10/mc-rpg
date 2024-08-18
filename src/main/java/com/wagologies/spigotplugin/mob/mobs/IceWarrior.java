package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.RPGItemBuilder;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.PlayerMob;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class IceWarrior extends PlayerMob {
    public IceWarrior(SpigotPlugin plugin) {
        super(plugin);
    }

    @Override
    public void spawn(Location location) {
        super.spawn(location);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwMzkwNjY4MTUwNywKICAicHJvZmlsZUlkIiA6ICIzZTY1NWNiNGJiYTY0ZWE5YTdmZTBmNDYzMGVjNzhiNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYWdoemlmeSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80YTIzNDVmNzhjZGZjYzEzZTk0MjI2ZTU0Y2M1MTUxOGQ3NDM0NDc5MTIxYTA3ZmM0ODY1M2U4MGQyNDZmMGE4IgogICAgfQogIH0KfQ==";
    }

    @Override
    public String getSkinSignature() {
        return "VMkf90UAnjtM3F5+u/gnHsqhCl+OXWYox3J7c/ItmV3zatH+yXLTXWLal3tzHaM7BG9PV6lKwFK/Z4K3sqT3blbzX2rs6H11tspFZ59GBIv/c5nrM7Ta69kwAXarR/kfWlKiRpb/VteuDeAFYAdaOPnlapn6cwlKDApseu36jCxNVovdw2NUsy1zmxW5MWNU8Y5WYpT+LW3oIpCiJbFc51Kfma+hsCFYO+wy17nRZvc1/P30zqnnQsSI/JqBCRCDuo00kIz33+nzOGDDFkXCwuP3jcYcnVcSBbC7d7oxHQt022JQVNKDUJlGZoUnDmbF/LJiAPIDtj2xs5Van6pgbNolCAFlO8476t4Bn5iyL/Q8jI+NIja4V0HnIZhXw8tUC29TmDDq2NXRRjbyPMGnFKApHciN83BEhPKswtvF/STNHIQhWOcwZvhrPnHPCRMnwNFDescJkoAFc+0QtN6/zg/T6xu0u+d8+M+dx2BIfhIxZ0jxf0kkGuE7kP3okWQN+R+WFHTWhzP8lmq9q/cqUiVVZKv+4LwccxzYap9WDEr2lKYBAndXUuJ2dLYTA7zBp5R/U/WLj/KanbvykNG3xjwcG4gCGEwr8Lqf58/oG+b/fYKstSPGpIVZ5IQwd3xPQs3bdeX1rj/6CqaF/kMwyh2oUMLYTkD09S0SUph0Sfo=";
    }

    @Override
    public MobType getType() {
        return MobType.ICE_WARRIOR;
    }

    @Override
    public int getMaxHealth() {
        return 150;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        return new RPGItemBuilder(Material.DIAMOND_SWORD).customType(ItemType.MELEE_WEAPON).damageType(
                DamageSource.DamageType.SLICING).damage(10).build(plugin);
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[0];
    }
}
