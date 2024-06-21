package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.utils.StringHelper;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByBlockEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nullable;
import java.util.List;

public abstract class PlayerMob extends AbstractMob implements Listener {
    protected NPC npc;
    protected RPGPlayer target = null;

    public PlayerMob(SpigotPlugin plugin) {
        super(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void spawn(Location location) {
        String npcName = StringHelper.nanoId();
        npc = plugin.getNPCRegistry().createNPC(EntityType.PLAYER, npcName);
        CitizensAPI.getNPCRegistry().deregister(npc);
        npc.setAlwaysUseNameHologram(true);
        npc.setProtected(false);
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(getName(), getSkinSignature(),getSkinTexture());
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        updateName();
        npc.spawn(location);
    }

    @Override
    public void updateName() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        if(hologramTrait != null) {
            hologramTrait.setLine(0, getDisplayName());
        }
    }

    public abstract String getSkinTexture();
    public abstract String getSkinSignature();
    @Override
    public Location getLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public Location getEyeLocation() {
        return ((CraftPlayer)npc.getEntity()).getEyeLocation();
    }

    @Override
    public World getWorld() {
        return npc.getStoredLocation().getWorld();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return npc.getEntity().getBoundingBox();
    }

    @Override
    public Entity getMainEntity() {
        return npc.getEntity();
    }

    @Override
    public void playHurtAnimation() {
        ((LivingEntity)npc.getEntity()).playHurtAnimation(0);
    }

    @Override
    public void tick() {
        super.tick();
        if(target == null || tickCount % 200 == 0) {
            setTarget(findNewTarget());
        }
    }

    protected void setTarget(@Nullable RPGPlayer newTarget) {
        target = newTarget;
        if(newTarget != null) {
            npc.getNavigator().setTarget(newTarget.getMainEntity(), true);
        }
    }

    public RPGPlayer findNewTarget() {
        List<RPGPlayer> players = plugin.getPlayerManager().getPlayers();
        Location npcLocation = this.npc.getStoredLocation();
        players.sort((a, b) -> (int) (b.getLocation().distanceSquared(npcLocation) - a.getLocation().distanceSquared(npcLocation)));
        for (RPGPlayer player : players) {
            double distance = player.getLocation().distance(npcLocation);
            if(distance >= 30) {
                break;
            }
            if(!player.isInvulnerable()) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void remove(boolean isDead) {
        super.remove(isDead);
        if(isDead) {
            ((LivingEntity)npc.getEntity()).setHealth(0);
        }
        npc.destroy();
        HandlerList.unregisterAll(this);
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

    @EventHandler
    public void onEntityDamage(NPCDamageByEntityEvent event) {
        if(event.getNPC().equals(npc)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNPCNaturalDamage(NPCDamageByBlockEvent event) {
        if(event.getNPC().equals(npc)) {
            event.setCancelled(true);
            onNaturalDamage(event.getEvent());
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager().equals(npc.getEntity())) {
            event.setCancelled(true);
            RPGEntity rpgEntity = plugin.getEntityManager().getEntity(event.getEntity());
            if(rpgEntity != null) {
                doDamageTarget(rpgEntity);
            }
        }
    }
}
