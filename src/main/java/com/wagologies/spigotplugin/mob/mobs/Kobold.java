package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.item.Wand;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.PlayerMob;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.utils.ItemHelper;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;

public class Kobold extends PlayerMob {

    public Kobold(SpigotPlugin plugin) {
        super(plugin);
        setAbilityScores(7, 15,9,8,7,8);
    }

    @Override
    public void spawn(Location location) {
        super.spawn(location);
        int armorColorHex = 0xbbdff0;
        ItemStack chestPlate = ItemHelper.DyeArmor(Material.LEATHER_CHESTPLATE, armorColorHex);
        ItemStack leggings = ItemHelper.DyeArmor(Material.LEATHER_LEGGINGS, armorColorHex);
        ItemStack boots = ItemHelper.DyeArmor(Material.LEATHER_BOOTS, armorColorHex);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chestPlate);
        equipment.set(Equipment.EquipmentSlot.LEGGINGS, leggings);
        equipment.set(Equipment.EquipmentSlot.BOOTS, boots);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.GOLDEN_AXE));
        NavigatorParameters parameters = npc.getNavigator().getLocalParameters();
        parameters.speedModifier(1.5f);
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        MeleeWeapon dagger = new MeleeWeapon(plugin, new ItemStack(Material.IRON_SWORD));
        dagger.setBaseDamage(10);
        return dagger;
    }

    @Override
    public boolean doDamageTarget(RPGEntity target) {
        if(super.doDamageTarget(target)) {
            if(target instanceof RPGPlayer rpgPlayer) {
                RPGItem heldItem = rpgPlayer.getHeldItem();
                if(heldItem instanceof MeleeWeapon || heldItem instanceof Wand) {
                    Random random = new Random();
                    if(random.nextInt(6) == 1) {
                        Player player = rpgPlayer.getPlayer();
                        player.sendMessage(ChatColor.RED + "A Kobold stole your " + ChatColor.YELLOW + heldItem.getDisplayName() + ChatColor.RED + "!");
                        player.dropItem(true);
                        player.getInventory().setItemInMainHand(null);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[4];
    }

    @Override
    public int getArmorClass() {
        return 12;
    }

    @Override
    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwODM3MzE4ODM4NiwKICAicHJvZmlsZUlkIiA6ICJmZmU5MzczY2YyMDM0OWFhYTJlN2NiYzJkZmY2M2I5MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWxvblR1bmExIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdiYTQyOGRhZGFjMzA5NjJjODM5NTkzYTg2MWQ2ODI3NjhkYTcxN2QwMDI5MjViODc1NzgwNjI0OWM0NWEzODIiCiAgICB9CiAgfQp9";
    }

    @Override
    public String getSkinSignature() {
        return "JlK8knf5b0uZdQyDRd6oMnzEyZC7OoxOQrLSPI1lXmwmegqsRlgPKuvzl8P6+jjKxMWpS5YiQ374kbUB/uW4Boib8m8i8/r5gCdD8ESDSgxwKeh4rjDl0tV3gJwFZEsxFDFgrsA2bBd8NftWKAb9uYCwFYSmfvn7VQAkMcUt9rYYwJjGS26OIOU06da30xIBOA5P8D9PwQnAwawB3z73kKdcanG/PjO2VdyIb2Iyt4KJoFbw4Yl2zE/4HeyxA67fFVV+ti84wHNqXG2P7KWTBb1Kb0tqtxEEiD7L0FKzrm3SvgHBcxzhX5QdBC8DXOJQgcWDx1grfz4KxxVXjz9IDoHb/7cZBrjgbfGRROtEgonD0BvQHiaZXwl6kkaAsxY04Lrjg0kZt2P3FCdciA3L0ixc+hqR1cwczeJIDgBeZ7bNpN3967xE6kL4sUinKC+kgyJQSIDjFKrRXRRjNBTsGYm50o/3K+ozgsmyCKcUr9aeoJR1wugnG4Uc12XDKY6i4T6fdt2KblGcXS+V3WUln5Toh1xoanWHb9QjiCjojehsf19t+Zg+mbFv2y1s1vA+I+ul218P5s7Uc7caIkbFR3IHupRvwB0Vi23SbxUgKGGYOkyfOgjE2lkm/w8WEG7Hsp/TKjgOjEiaw4GF0z+fgCso9P/tNIoMGGOuw609Edw=";
    }

    @Override
    public MobType getType() {
        return MobType.KOBOLD;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_ZOMBIE_HURT;
    }
}
