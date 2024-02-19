package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.mob.custom.CustomEntityKobold;
import com.wagologies.spigotplugin.utils.ItemHelper;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Random;

public class Kobold extends EntityMob {

    static Material[] StealableItems = new Material[] {Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD, Material.BOW, Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE};

    @Override
    public Entity createBaseEntity(World world, Location location) {
        CustomEntityKobold kobold = new CustomEntityKobold(((CraftWorld)world).getHandle());
        kobold.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        kobold.setBaby(false);
        kobold.setVillager(false);
        ((CraftWorld) world).addEntity(kobold, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) kobold.getBukkitEntity();
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false ,false), true);
        ItemStack skull = ItemHelper.getCustomSkull("http://textures.minecraft.net/texture/4274dad66474258c2454696617d5ab859e57e27639e9c03943cb9472481e3c74");

        int armorColorHex = 0xd6b83f;
        ItemStack chestPlate = ItemHelper.DyeArmor(Material.LEATHER_CHESTPLATE, armorColorHex);
        ItemStack leggings = ItemHelper.DyeArmor(Material.LEATHER_LEGGINGS, armorColorHex);
        ItemStack boots = ItemHelper.DyeArmor(Material.LEATHER_BOOTS, armorColorHex);

        EntityEquipment entityEquipment = zombie.getEquipment();
        entityEquipment.setArmorContents(new ItemStack[]{boots, leggings, chestPlate, skull});
        entityEquipment.setItemInHand(new ItemStack(Material.GOLD_AXE));
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
                        EntityHuman human = ((CraftPlayer) player).getHandle();
                        human.a(true);
                    }
                }
            }
        }
    }
}
