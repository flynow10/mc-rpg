package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.PlayerMob;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Bandit extends PlayerMob {
    public Bandit(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(11, 12, 12, 10, 10, 10);
    }

    @Override
    public void spawn(Location location) {
        super.spawn(location);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.GOLDEN_CHESTPLATE));
    }

    @Override
    public int getMaxHealth() {
        return 110;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        MeleeWeapon scimitar = new MeleeWeapon(plugin, new ItemStack(Material.IRON_SWORD));
        scimitar.setBaseDamage(4);
        scimitar.setDamageType(DamageSource.DamageType.PIERCING);
        return scimitar;
    }

    @Override
    public int getArmorClass() {
        return 12;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[0];
    }

    @Override
    public MobType getType() {
        return MobType.BANDIT;
    }

    @Override
    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwOTMzMTYzMzIxNiwKICAicHJvZmlsZUlkIiA6ICIzZTY1NWNiNGJiYTY0ZWE5YTdmZTBmNDYzMGVjNzhiNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYWdoemlmeSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lMGEwNmE3MjU4MmI3N2E3YTQzNTE5NDQ3MjgxMDVhOGViMjljNDQ5YzgzOWU0OTNjNzE1Y2JiMGVlY2ZiN2I4IgogICAgfQogIH0KfQ==";
    }

    @Override
    public String getSkinSignature() {
        return "Zbb+IHYW3hyowD7tJvua1B+0uIYKtInrGrNs+mCTnLfNtk0VC2qRGnBsy8/471RFvhxz2Omlw74tTKggFZWW9OP/oI6qYfiEHQWdqnWw8az5VAEL7QlTsC7FPzfEkueX+gBbwuUWLwDznLlde/KyAzSEBA7G1qFJIm4ENZ1t0caS7Ty8XptO7pK1KdA4ldlTcpNpTf47ebRpLLWO0BKMdw1xYZIkXO5LB5UMlvMkmlwQxGfDxpbT73J3a5abeeaIw6a5drRsAD75Sf82A0jgBGM/PNqQKUvdkI9d85Fwo987C8cySy2geFdm9irDN4O/0FSoSC3AF6FzTW3mmSXyAPvRX94FmV8IuNW+sf3ncR9Zb7kQdS/HKweiZ6t533UAQJcCAu2qiHHL8vKfDX/TElirm+89NnhszJfHWBFlJjSzpbbA30zLIkVZkFVn2z+sdKUrpKcwb8AgkuxpmBy0Gt+h6bbUft332/t8LW4DLyy5Zs5g8cTHOJeprEYqoSp/n2kc3Pm9e5h/pM1GO0QhF+kAxe5v/F1363C7UCkZ6mkz2JMGRaPdONKj+q9Co2UiNmweTQc38ilwFk2UbhgfZrgWe6PaXhJnNHSEJ/haSosMdmJM2/JZ1IFBncaTaTCo8agDrCuWKNL+HKgGapUCRJsIEEIUMzrvRbAgDMtYhwQ=";
    }
}
