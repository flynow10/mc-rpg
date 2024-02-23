package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.utils.ItemHelper;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Random;

public class Kobold extends EntityMob {

    static Material[] StealableItems = new Material[] {Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.BOW, Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE};

    @Override
    public Entity createBaseEntity(World world, Location location) {
        Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
        zombie.setAdult();
        zombie.setPersistent(true);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false ,false));
        ItemStack skull = ItemHelper.getCustomSkull("http://textures.minecraft.net/texture/7ba428dadac30962c839593a861d682768da717d002925b8757806249c45a382");

        int armorColorHex = 0xbbdff0;
        ItemStack chestPlate = ItemHelper.DyeArmor(Material.LEATHER_CHESTPLATE, armorColorHex);
        ItemStack leggings = ItemHelper.DyeArmor(Material.LEATHER_LEGGINGS, armorColorHex);
        ItemStack boots = ItemHelper.DyeArmor(Material.LEATHER_BOOTS, armorColorHex);
        EntityEquipment entityEquipment = zombie.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setArmorContents(new ItemStack[]{boots, leggings, chestPlate, skull});
        entityEquipment.setItemInMainHand(new ItemStack(Material.GOLDEN_AXE));
        return zombie;
    }

    @Override
    public String getName() {
        return "Kobold";
    }

    @Override
    public int getMaxHealth() {
        return 50;
    }
    @EventHandler
    public void PlayerDropWeapon(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getDamager().equals(this.baseEntity)) {
                Random random = new Random();
                if(random.nextInt(6) == 1) {
                    Player player = (Player) event.getEntity();
                    ItemStack heldItem = player.getItemInHand();
                    if(Arrays.stream(StealableItems).anyMatch(mat -> mat == heldItem.getType())) {
                        player.sendMessage(ChatColor.RED + "A Kobold stole your " + ChatColor.YELLOW + heldItem.getType().name().replace("_", " ").toLowerCase() + ChatColor.RED + "!");
                        player.dropItem(true);
                        player.getInventory().setItemInMainHand(null);
                    }
                }
            }
        }
    }
}
