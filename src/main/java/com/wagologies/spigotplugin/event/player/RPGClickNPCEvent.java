package com.wagologies.spigotplugin.event.player;

import com.wagologies.spigotplugin.npc.NPC;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RPGClickNPCEvent extends RPGPlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final NPC npc;
    protected boolean cancelled = false;
    protected boolean isRightClick;

    public RPGClickNPCEvent(RPGPlayer who, NPC npc, boolean isRightClick) {
        super(who);
        this.npc = npc;
        this.isRightClick = isRightClick;
    }

    public boolean isRightClick() {
        return isRightClick;
    }

    public RPGClickNPCEvent setRightClick(boolean rightClick) {
        isRightClick = rightClick;
        return this;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public NPC getNpc() {
        return npc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
