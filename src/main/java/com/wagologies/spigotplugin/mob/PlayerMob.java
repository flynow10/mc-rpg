package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.event.DamageMobByPlayer;
import com.wagologies.spigotplugin.event.DamageMobEvent;
import com.wagologies.spigotplugin.mob.AbstractMob;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.spell.SpellCaster;
import com.wagologies.spigotplugin.utils.StringHelper;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public abstract class PlayerMob extends AbstractMob implements SpellCaster {
    protected NPC npc;
    protected Integer tickTask = null;
    protected int ticksPassed = 0;

    @Override
    public void spawn(Location location) {
        String npcName = StringHelper.nanoId();
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcName);
        CitizensAPI.getNPCRegistry().deregister(npc);
        npc.setAlwaysUseNameHologram(true);
        npc.setProtected(false);
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(getName(), getSkinSignature(),getSkinTexture());
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        updateName();
        npc.spawn(location);
        tickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(mobManager.getPlugin(), this::tick, 0, 1);
    }

    @Override
    public void teleport(Location location) {
        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public void teleport(Entity entity) {
        teleport(entity.getLocation());
    }

    @Override
    public void setVelocity(Vector velocity) {
        npc.getEntity().setVelocity(velocity);
    }

    public void updateName() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        if(hologramTrait != null) {
            hologramTrait.setLine(0, getHoverText());
        }
    }

    public abstract String getSkinTexture();
    public abstract String getSkinSignature();

    @EventHandler
    public void onNPCDamage(NPCDamageByEntityEvent event) {
        if(event.getNPC() != npc) {
            return;
        }
        DamageMobEvent damageEvent = createDamageEvent(event.getEvent());

        Bukkit.getPluginManager().callEvent(damageEvent);
        if(damageEvent.isCancelled()) {
            return;
        }

        boolean isDead = damage(damageEvent.getDamage());
        event.setDamage(isDead ? ((Damageable)event.getNPC().getEntity()).getHealth() : 0);
        if(!isDead) {
            updateName();
        }
    }

    @EventHandler
    public void onNPCDeath(NPCDeathEvent event) {
        if(event.getNPC() == npc) {
            EntityDeathEvent entityDeathEvent = event.getEvent();
            if(entityDeathEvent instanceof PlayerDeathEvent) {
                ((PlayerDeathEvent) entityDeathEvent).setDeathMessage("");
            }
        }
    }

    @Override
    public void onDeath() {
        super.onDeath();
        if(tickTask != null) {
            Bukkit.getScheduler().cancelTask(tickTask);
        }
        Bukkit.getScheduler().runTaskLater(this.mobManager.getPlugin(), () -> {
            npc.destroy();
        }, 100);
    }

    @Override
    public void remove() {
        super.remove();
        if(tickTask != null) {
            Bukkit.getScheduler().cancelTask(tickTask);
        }
        npc.destroy();
    }

    public void tick() {
        ticksPassed ++;
    }

    @Override
    public Location getLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public Location getEyeLocation() {
        return ((CraftPlayer)npc.getEntity()).getEyeLocation();
    }

    @Override
    public Entity getCastingEntity() {
        return npc.getEntity();
    }

    //    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//        if(player.equals(targetPlayer)) {
//            if(player.getLocation().distanceSquared(npc.getStoredLocation()) > 4) {
//                Location targetLocation = npc.getStoredLocation().subtract(player.getLocation()).toVector().normalize().multiply(3).toLocation(player.getWorld()).add(player.getLocation());
//                npc.getNavigator().setTarget(targetLocation);
//            }
//        }
//    }
}
