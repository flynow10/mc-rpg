package com.wagologies.spigotplugin.npc;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.RPGClickNPCEvent;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.utils.StringHelper;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class NPC implements Listener {
    private final SpigotPlugin plugin;
    private final Campaign campaign;
    protected final net.citizensnpcs.api.npc.NPC citizenNPC;
    protected Location targetLocation;

    public NPC(SpigotPlugin plugin, Campaign campaign) {
        this.plugin = plugin;
        this.campaign = campaign;
        String npcName = StringHelper.nanoId();
        citizenNPC = plugin.getNPCRegistry().createNPC(EntityType.PLAYER, npcName);
        CitizensAPI.getNPCRegistry().deregister(citizenNPC);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    protected abstract void setupNPC();
    public abstract String getName();
    public abstract void onInteract(RPGClickNPCEvent event);

    public void spawn() {
        LookClose lookClose = citizenNPC.getOrAddTrait(LookClose.class);
        lookClose.lookClose(true);
        lookClose.setPerPlayer(false);
        citizenNPC.setAlwaysUseNameHologram(true);
        citizenNPC.data().set(net.citizensnpcs.api.npc.NPC.Metadata.NAMEPLATE_VISIBLE, false);
        HologramTrait hologramTrait = citizenNPC.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.GRAY + "NPC");
        hologramTrait.setLine(1, ChatColor.DARK_GREEN + getName());
        hologramTrait.setLineHeight(0.3);
        setupNPC();
        citizenNPC.spawn(getTargetLocation());
    }

    public void despawn() {
        citizenNPC.destroy();
        HandlerList.unregisterAll(this);
    }

    public void updateName() {
        if(citizenNPC.hasTrait(HologramTrait.class)) {
            HologramTrait hologramTrait = citizenNPC.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(1, ChatColor.DARK_GREEN + getName());
        }
    }

    public void speakToPlayer(Player player, String message) {
        speakToPlayer(player, message, getName());
    }

    public void speakToPlayer(Player player, String message, String customName) {
        player.sendMessage(ChatColor.DARK_GREEN + customName + ": " + ChatColor.GREEN + message);
    }

    public void speakToPlayer(Player player, String message, int messageNum, int conversationLength) {
        speakToPlayer(player, message, messageNum, conversationLength, getName());
    }
    public void speakToPlayer(Player player, String message, int messageNum, int conversationLength, String customName) {
        player.sendMessage(ChatColor.GRAY + "[" + messageNum + "/" + conversationLength + "] " + ChatColor.DARK_GREEN + customName + ": " + ChatColor.GREEN + message);
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public net.citizensnpcs.api.npc.NPC getCitizenNPC() {
        return citizenNPC;
    }

    public Location getLocation() {
        if(!this.citizenNPC.isSpawned()) {
            return getTargetLocation();
        }
        return citizenNPC.getStoredLocation();
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
        if(this.citizenNPC.isSpawned()) {
            citizenNPC.getNavigator().setTarget(targetLocation);
        }
    }

    public SpigotPlugin getPlugin() {
        return plugin;
    }

    @Nullable
    public Map<String, Object> serialize() {
        return null;
    }

    public void deserialize(@Nullable Map<String, Object> data) {}


    @EventHandler
    public void _internalOnNPCRightClick(NPCRightClickEvent event) {
        _internalOnNPCClick(event, true);
    }

    @EventHandler
    public void _internalOnNPCLeftClick(NPCLeftClickEvent event) {
        _internalOnNPCClick(event, false);
    }

    private void _internalOnNPCClick(NPCClickEvent event, boolean rightClick) {
        if(event.getNPC().equals(citizenNPC)) {
            RPGPlayer player = plugin.getPlayerManager().getPlayer(event.getClicker());
            if(player != null) {
                RPGClickNPCEvent clickNPCEvent = new RPGClickNPCEvent(player, this, rightClick);
                Bukkit.getPluginManager().callEvent(clickNPCEvent);
                if(!clickNPCEvent.isCancelled()) {
                    this.onInteract(clickNPCEvent);
                }
            }
        }
    }
}
