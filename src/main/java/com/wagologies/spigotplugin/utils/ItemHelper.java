package com.wagologies.spigotplugin.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

public class ItemHelper {

    static final Material[] leatherTypes = new Material[] {Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET};

    public static ItemStack DyeArmor(Material material, int color) {
        return DyeArmor(material, Color.fromRGB(color));
    }

    public static void DyeArmor(ItemStack itemStack, int color) {
        DyeArmor(itemStack, Color.fromRGB(color));
    }
    public static ItemStack DyeArmor(Material material, Color color) {
        ItemStack armor = new ItemStack(material);
        DyeArmor(armor, color);
        return armor;
    }
    public static void DyeArmor(ItemStack itemStack, Color color) {
        Material itemType = itemStack.getType();
        if(Arrays.stream(leatherTypes).noneMatch(mat -> mat.equals(itemType))) {
            throw new RuntimeException("Cannot dye non leather armor!");
        }
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        armorMeta.setColor(color);
        itemStack.setItemMeta(armorMeta);
    }

    public static ItemStack getCustomSkull(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        byte[] encodedData = new Base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile);
        head.setItemMeta(headMeta);
        return head;
    }

    public static void AddGlow(ItemStack itemStack) {
        if(itemStack.getType().isAir()) {
            return;
        }
        itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
    }
}
