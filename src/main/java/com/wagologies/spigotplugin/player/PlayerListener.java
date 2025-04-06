package com.wagologies.spigotplugin.player;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.ItemType;
import com.wagologies.spigotplugin.item.Scroll;
import com.wagologies.spigotplugin.item.Wand;
import com.wagologies.spigotplugin.spell.SpellType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    private final SpigotPlugin plugin;
    private final RPGPlayer player;

    public PlayerListener(SpigotPlugin plugin, RPGPlayer player) {
        this.plugin = plugin;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void removeListener() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onNPCInteract(RPGClickNPCEvent event) {
        if(event.getRPGPlayer().equals(this.player)) {
            if(this.player.isInConversation() || this.player.isInCombat()) {
                event.setCancelled(true);
                if(this.player.isInCombat()) {
                    player.getPlayer().sendMessage(ChatColor.RED + "You cannot talk while in combat!");
                }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(event.getDamager().equals(player.getPlayer())) {
            RPGEntity rpgEntity = plugin.getEntityManager().getEntity(event.getEntity());
            if (rpgEntity != null) {
                player.doDamageTarget(rpgEntity);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand != null && !itemInHand.getType().isAir()) {
                ItemType itemType = RPGItem.GetItemType(itemInHand);
                if(itemType == ItemType.WAND) {
                    Wand wand = (Wand) RPGItem.ConvertToCustomItem(plugin, itemInHand);
                    assert wand != null;
                    if (!wand.isSpellLoaded()) {
                        return;
                    }
                    SpellType spellType = wand.getLoadedSpell();
                    player.castSpell(spellType, wand);
                } else if(itemType == ItemType.SCROLL) {
                    Scroll scroll = (Scroll) RPGItem.ConvertToCustomItem(plugin, itemInHand);
                    assert scroll != null;
                    SpellType spellType = scroll.getSpellType();
                    player.learnSpell(spellType);
                    player.updateSpellBook(true);
                    Player bukkitPlayer = player.getPlayer();
                    bukkitPlayer.sendMessage(ChatColor.RED + "The scroll disintegrates and you feel an understanding of the magic within");
                    bukkitPlayer.getInventory().setItemInMainHand(null);
                    bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            if (event.isSneaking() && player.canChargeSpell()) {
                Bukkit.getScheduler().runTaskLater(plugin, player::beginChargingSpell, 1);
            } else {
                player.chargeSpell();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterPlace(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWaterPickup(PlayerBucketFillEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onActivateBlock(PlayerInteractEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onWeaponDamage(PlayerItemDamageEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            if (player.getSpellCast() != null) {
                Location to = event.getTo();
                if (to != null && player.getSpellCast().getSpellCastLocation().distance(to) > 0.2) {
                    player.cancelChargingSpell();
                    player.getPlayer().sendMessage(ChatColor.RED + "You moved! Canceling spell cast...");
                }
            }
        }
    }

    @EventHandler
    public void onChangeItem(PlayerItemHeldEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            if (event.getNewSlot() != event.getPreviousSlot()) {
                ItemStack originalItem = player.getPlayer().getInventory().getItem(event.getPreviousSlot());
                if (originalItem != null) {
                    player.clearSpell(originalItem);
                }
                player.cancelChargingSpell();
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().equals(this.player.getPlayer())) {
            player.clearSpell(event.getItemDrop().getItemStack());
        }
    }

    @EventHandler
    public void onMoveItem(InventoryClickEvent event) {
        if (event.getWhoClicked().equals(this.player.getPlayer())) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                player.clearSpell(clickedItem);
            }
            ItemStack cursorItem = event.getCursor();
            if (cursorItem != null) {
                player.clearSpell(cursorItem);
            }
        }
    }

    @EventHandler
    public void onNaturalRegen(EntityRegainHealthEvent event) {
        if(event.getEntity().equals(this.player.getPlayer())) {
            if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) || event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onNaturalDamage(EntityDamageEvent event) {
        if(event.getEntity().equals(this.player.getPlayer())) {
            event.setCancelled(true);
            player.onNaturalDamage(event);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        Player bukkitPlayer = this.player.getPlayer();
        if(event.getEntity().equals(bukkitPlayer)) {
            event.setCancelled(true);
            bukkitPlayer.setFoodLevel(20);
            bukkitPlayer.setSaturation(5);
        }
    }
}
