package com.wagologies.spigotplugin.item;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.spell.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class RPGItemBuilder {
    private ItemType type = ItemType.UNKNOWN;
    private int damage = 0;
    private DamageSource.DamageType damageType = DamageSource.DamageType.SLICING;
    private WandCoreType coreType = WandCoreType.INERT;
    private MeleeWeapon.AttackSpeed attackSpeed = MeleeWeapon.AttackSpeed.NORMAL;
    private int armorClass = 0;
    private int weight = -1;
    private SpellType spellType = SpellType.AuraOfVitality;

    private final ItemStack stack;

    public RPGItemBuilder(Material material) {
        this.stack = new ItemStack(material);
    }

    public RPGItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public RPGItemBuilder type(Material material) {
        this.stack.setType(material);
        return this;
    }

    public Material getType() {
        return this.stack.getType();
    }

    public RPGItemBuilder name(String name) {
        ItemMeta stackMeta = this.stack.getItemMeta();
        assert stackMeta != null;
        stackMeta.setDisplayName(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', name));
        this.stack.setItemMeta(stackMeta);
        return this;
    }

    public String getName() {
        return this.stack.hasItemMeta() && this.stack.getItemMeta().hasDisplayName() ? this.stack.getItemMeta().getDisplayName() : null;
    }

    public RPGItemBuilder amount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public int getAmount() {
        return this.stack.getAmount();
    }

    public RPGItemBuilder lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public RPGItemBuilder lore(List<String> lore) {
        lore.replaceAll((textToTranslate) -> {
            return ChatColor.translateAlternateColorCodes('&', textToTranslate);
        });
        ItemMeta stackMeta = this.stack.getItemMeta();
        assert stackMeta != null;
        stackMeta.setLore(lore);
        this.stack.setItemMeta(stackMeta);
        return this;
    }

    public List<String> getLore() {
        return this.stack.hasItemMeta() && this.stack.getItemMeta().hasLore() ? this.stack.getItemMeta().getLore() : null;
    }

    public RPGItemBuilder color(Color color) {
        if (this.stack.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
            leatherArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
            this.stack.setItemMeta(leatherArmorMeta);
        }
        return this;
    }

    public RPGItemBuilder data(short data) {
        return this.durability(data);
    }

    public RPGItemBuilder durability(short durability) {
        this.stack.setDurability(durability);
        return this;
    }

    public short getDurability() {
        return this.stack.getDurability();
    }

    public Color getColor() {
        if (!(this.stack.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta)) {
            return null;
        } else {
            return leatherArmorMeta.getColor();
        }
    }

    public RPGItemBuilder enchant(Enchantment enchantment, int level) {
        this.stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public RPGItemBuilder unenchant(Enchantment enchantment) {
        this.stack.removeEnchantment(enchantment);
        return this;
    }

    public RPGItemBuilder flag(ItemFlag... flag) {
        ItemMeta meta = this.stack.getItemMeta();
        assert meta != null;
        meta.addItemFlags(flag);
        this.stack.setItemMeta(meta);
        return this;
    }

    public RPGItemBuilder deflag(ItemFlag... flag) {
        ItemMeta meta = this.stack.getItemMeta();
        assert meta != null;
        meta.removeItemFlags(flag);
        this.stack.setItemMeta(meta);
        return this;
    }

    public RPGItemBuilder skullOwner(String name) {
        if (this.stack.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwner(name);
            this.stack.setItemMeta(meta);
        }
        return this;
    }

    public RPGItemBuilder ifThen(Predicate<RPGItemBuilder> ifTrue, Function<RPGItemBuilder, Object> then) {
        if (ifTrue.test(this)) {
            then.apply(this);
        }

        return this;
    }

    private ItemStack build() {
        return this.get();
    }

    public ItemStack get() {
        return this.stack;
    }

    public RPGItemBuilder customType(ItemType type) {
        this.type = type;
        return this;
    }

    public ItemType getCustomType() {
        return this.type;
    }

    public RPGItemBuilder damage(int damage) {
        if(type != ItemType.MELEE_WEAPON) {
            Bukkit.getLogger().warning("Setting damage of non weapon!");
        }
        this.damage = damage;
        return this;
    }

    public int getDamage() {
        return damage;
    }

    public RPGItemBuilder attackSpeed(MeleeWeapon.AttackSpeed speed) {
        if(type != ItemType.MELEE_WEAPON) {
            Bukkit.getLogger().warning("Setting damage of non weapon!");
        }
        this.attackSpeed = speed;
        return this;
    }

    public MeleeWeapon.AttackSpeed getAttackSpeed() {
        return attackSpeed;
    }

    public RPGItemBuilder damageType(DamageSource.DamageType damageType) {
        if(type != ItemType.MELEE_WEAPON) {
            Bukkit.getLogger().warning("Setting damage type of non weapon!");
        }
        this.damageType = damageType;
        return this;
    }

    public DamageSource.DamageType getDamageType() {
        return damageType;
    }

    public RPGItemBuilder coreType(WandCoreType coreType) {
        if(type != ItemType.WAND && type != ItemType.WAND_CORE) {
            Bukkit.getLogger().warning("Setting wand core of non wand!");
        }
        this.coreType = coreType;
        return this;
    }

    public WandCoreType getCoreType() {
        return coreType;
    }

    public RPGItemBuilder armorClass(int armorClass) {
        if(type != ItemType.ARMOR) {
            Bukkit.getLogger().warning("Setting armor class of non armor!");
        }
        this.armorClass = armorClass;
        return this;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public RPGItemBuilder weight(int weight) {
        if(type != ItemType.ARMOR) {
            Bukkit.getLogger().warning("Setting weight of non armor!");
        }
        this.weight = weight;
        return this;
    }

    public int getWeight() {
        return weight == -1 ? armorClass : weight;
    }

    public RPGItemBuilder spellType(SpellType spellType) {
        if(type != ItemType.SCROLL) {
            Bukkit.getLogger().warning("Setting spell type of non spell!");
        }
        this.spellType = spellType;
        return this;
    }

    public SpellType getSpellType() {
        return this.spellType;
    }

    public RPGItem build(SpigotPlugin plugin) {
        ItemStack itemStack = build();
        switch (type) {
            case UNKNOWN -> {
                return new RPGItem(plugin, itemStack);
            }
            case MELEE_WEAPON -> {
                MeleeWeapon meleeWeapon = new MeleeWeapon(plugin, itemStack);
                meleeWeapon.setBaseDamage(damage);
                meleeWeapon.setDamageType(damageType);
                meleeWeapon.setAttackSpeed(attackSpeed);
                return meleeWeapon;
            }
            case WAND -> {
                Wand wand = new Wand(plugin, itemStack);
                wand.setCoreType(coreType);
                return wand;
            }
            case WAND_CORE -> {
                WandCore wandCore = new WandCore(plugin, itemStack);
                wandCore.setCoreType(coreType);
                return wandCore;
            }
            case ARMOR -> {
                Armor armor = new Armor(plugin, itemStack);
                armor.setArmorClass(armorClass);
                if(weight == -1) {
                    armor.setWeight(armorClass);
                } else {
                    armor.setWeight(weight);
                }
                return armor;
            }
            case SCROLL -> {
                Scroll scroll = new Scroll(plugin, itemStack);
                scroll.setSpellType(this.spellType);
                return scroll;
            }

            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
