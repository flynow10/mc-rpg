package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.event.CastSpellEvent;
import com.wagologies.spigotplugin.event.DamageMobByPlayer;
import com.wagologies.spigotplugin.event.SpellHitEntityEvent;
import com.wagologies.spigotplugin.item.CustomItem;
import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.item.Wand;
import com.wagologies.spigotplugin.spell.*;
import com.wagologies.spigotplugin.spell.spells.EldritchBlast;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class RPGPlayer implements Listener, SpellCaster, MagicAffectable {
    private final Player player;
    private final SpigotPlugin plugin;
    private SpellCast spellCast = null;
    private int mana = 0;
    private int health;
    private int secondTicks = 0;
    public RPGPlayer(Player player, SpigotPlugin plugin) {
        this.player = player;
        this.plugin = plugin;
        health = getMaxHealth();
        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void leavePlayer() {
        HandlerList.unregisterAll(this);
    }

    public Player getPlayer() {
        return player;
    }

    public void tick() {
        secondTicks++;
        if(secondTicks >= 20) {
            secondTicks = 0;
        }
        if(secondTicks % 4 == 0) {
            setHealth(health + 1);
            mana = Math.clamp(mana + 1, 0, getMaxMana());
            if(secondTicks != 0) {
                sendActionBar();
            }
        }
        if(secondTicks == 0) {
            sendActionBar();
        }
        if(spellCast != null) {
            spellCast.tick();
        }
    }

    public void sendActionBar() {
        String actionBarString = ChatColor.RED.toString() + health + " / " + getMaxHealth() + "   " + ChatColor.BLUE + mana + " / " + getMaxMana();
        plugin.getActionBar().sendActionBar(player, actionBarString);
    }

    public int getMaxMana() {
        return 100;
    }
    public int getMaxHealth() { return 100; }

    public void setHealth(int newHealth) {
        health = Math.clamp(newHealth, 0, getMaxHealth());
        player.setHealth(((double) health / getMaxHealth()) * 20);
    }

    public boolean canChargeSpell() {
        if(player.isFlying()) {
            return false;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType().isAir() || !itemStack.getType().isItem()) {
            return false;
        }
        return CustomItem.GetItemType(itemStack).equals(ItemType.WAND);
    }

    public void beginChargingSpell() {
        this.spellCast = new SpellCast(this, this.player.getLocation());
    }

    public void cancelChargingSpell() {
        if(this.spellCast != null) {
            this.spellCast = null;
        }
    }

    public void chargeSpell() {
        if(spellCast != null) {
            List<SpellCast.SpellLine> spellLines = spellCast.getPatternLines();
            ItemStack holdingItem = player.getInventory().getItemInMainHand();
            if(!spellLines.isEmpty() && CustomItem.GetItemType(holdingItem) == ItemType.WAND) {
                Wand wand = (Wand) CustomItem.ConvertToCustomItem(plugin, holdingItem);
                SpellType spellType = SpellType.fromLines(spellLines);
                boolean success = spellType != null;
                List<Location> castingPoints = spellCast.getCastingPoints();
                Particle.DustOptions dustOptions = new Particle.DustOptions(success ? Color.fromRGB(0x00aa14) : Color.fromRGB(0xe02626), 1.0F);
                for (Location castingPoint : castingPoints) {
                    player.getWorld().spawnParticle(Particle.REDSTONE, castingPoint, 1, dustOptions);
                }
                if(success) {
                    wand.setLoadedSpell(spellType);
                    player.playSound(player, Sound.ENTITY_BREEZE_INHALE, 10, 1);
                    player.sendMessage(ChatColor.GREEN + "Your wand is now charged with " + ChatColor.LIGHT_PURPLE + spellType.getName());
                }
            }
            cancelChargingSpell();
        }
    }

    private void clearSpell(ItemStack itemStack) {
        if(itemStack.getType().isAir() || CustomItem.GetItemType(itemStack) != ItemType.WAND) {
           return;
        }

        Wand wand = ((Wand) CustomItem.ConvertToCustomItem(plugin, itemStack));
        if(wand.isSpellLoaded()) {
            wand.setLoadedSpell(null);
            player.sendMessage(ChatColor.RED + "You lost focused and discharged your spell!");
        }
    }

    private void castSpell(SpellType spellType, Wand wand) {
        CastSpellEvent event = new CastSpellEvent(this, spellType);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }

        plugin.getSpellManager().castSpell(this, spellType);
        player.sendMessage(ChatColor.GREEN + "You cast " + ChatColor.LIGHT_PURPLE + spellType.getName());
        wand.setLoadedSpell(null);
    }

    @Override
    public boolean damage(int damage) {
        setHealth(health - damage);
        return health == 0;
    }

    @Override
    public Entity getEntity() {
        return player;
    }

    @EventHandler
    public void onDamageMob(DamageMobByPlayer event) {
        if(event.getPlayer().equals(this)) {
            event.setDamage(1);
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if(heldItem.getType().isAir()) {
                return;
            }
            if(CustomItem.GetItemType(heldItem) == ItemType.MELEE_WEAPON) {
                MeleeWeapon meleeWeapon = (MeleeWeapon) CustomItem.ConvertToCustomItem(plugin, heldItem);
                event.setDamage(meleeWeapon.getBaseDamage());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getPlayer().equals(this.player)) {
            ItemStack itemInHand = event.getItem();
            if(itemInHand != null && !itemInHand.getType().isAir() && CustomItem.GetItemType(itemInHand) == ItemType.WAND) {
                Wand wand = (Wand) CustomItem.ConvertToCustomItem(plugin, itemInHand);
                if(!wand.isSpellLoaded()) {
                    return;
                }
                SpellType spellType = wand.getLoadedSpell();
                castSpell(spellType, wand);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if(event.getPlayer().equals(this.player)) {
            if(event.isSneaking() && canChargeSpell()) {
                Bukkit.getScheduler().runTaskLater(plugin, this::beginChargingSpell, 1);
            } else {
                chargeSpell();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterPlace(PlayerBucketEmptyEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterPickup(PlayerBucketFillEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onActivateBlock(PlayerInteractEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeaponDamage(PlayerItemDamageEvent event) {
        if(event.getPlayer().equals(this.player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.getPlayer().equals(this.player)) {
            if(spellCast != null) {
                Location to = event.getTo();
                if(to != null && spellCast.getSpellCastLocation().distance(to) > 0.2) {
                    cancelChargingSpell();
                    player.sendMessage(ChatColor.RED + "You moved! Canceling spell cast...");
                }
            }
        }
    }

    @EventHandler
    public void onChangeItem(PlayerItemHeldEvent event) {
        if(event.getPlayer().equals(this.player)) {
            if(event.getNewSlot() != event.getPreviousSlot()) {
                ItemStack originalItem = player.getInventory().getItem(event.getPreviousSlot());
                if(originalItem != null) {
                    clearSpell(originalItem);
                }
                cancelChargingSpell();
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if(event.getPlayer().equals(this.player)) {
            clearSpell(event.getItemDrop().getItemStack());
        }
    }

    @EventHandler
    public void onMoveItem(InventoryClickEvent event) {
        if(event.getWhoClicked().equals(player)) {
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem != null) {
                clearSpell(clickedItem);
            }
            ItemStack cursorItem = event.getCursor();
            if(cursorItem != null) {
                clearSpell(cursorItem);
            }
        }
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Location getEyeLocation() {
        return player.getEyeLocation();
    }

    @Override
    public Entity getCastingEntity() {
        return player;
    }
}
