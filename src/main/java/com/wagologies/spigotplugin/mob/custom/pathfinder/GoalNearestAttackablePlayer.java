package com.wagologies.spigotplugin.mob.custom.pathfinder;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.utils.LocationHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class GoalNearestAttackablePlayer extends PathfinderGoalNearestAttackableTarget {
    private final SpigotPlugin plugin;
    @Nullable
    private final Predicate<RPGPlayer> playerPredicate;

    public GoalNearestAttackablePlayer(SpigotPlugin plugin, EntityInsentient entityInsentient, boolean mustSeeTarget) {
        this(plugin, entityInsentient, mustSeeTarget, null);
    }
    public GoalNearestAttackablePlayer(SpigotPlugin plugin, EntityInsentient entityInsentient, boolean mustSeeTarget, @Nullable Predicate<RPGPlayer> playerPredicate) {
        super(entityInsentient, EntityHuman.class, mustSeeTarget);
        this.plugin = plugin;
        this.playerPredicate = playerPredicate;
    }


    @Override
    protected void h() {
        List<RPGPlayer> players = plugin.getPlayerManager().getPlayers();
        RPGPlayer closestPlayer = LocationHelper.getClosest(this.e.dr(), this.e.dv(), this.e.dx(), players, player -> player.getLocation().toVector(), player -> {
            if(player.isInvulnerable()) {
                return false;
            }
            if(playerPredicate != null && !playerPredicate.test(player)) {
                return false;
            }
            if(!this.f) {
                return true;
            }
            return this.e.O().a(((CraftPlayer)player.getPlayer()).getHandle());
        });
        if(closestPlayer != null) {
            CraftPlayer craftPlayer = (CraftPlayer) closestPlayer.getPlayer();
            this.c = craftPlayer.getHandle();
        }
    }
}
