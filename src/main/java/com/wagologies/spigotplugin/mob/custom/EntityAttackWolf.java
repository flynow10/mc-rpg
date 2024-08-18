package com.wagologies.spigotplugin.mob.custom;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.custom.pathfinder.GoalNearestAttackablePlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;

public class EntityAttackWolf extends EntityWolf {
    private final SpigotPlugin plugin;
    public EntityAttackWolf(SpigotPlugin plugin, World world) {
        super(EntityTypes.bp, world);
        this.plugin = plugin;
        this.persist = true;
        addTargetSelectorGoals();
    }

    @Override
    protected void B() {
        this.bO.a(1, new PathfinderGoalFloat(this));
        this.bO.a(4, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.bO.a(5, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.bO.a(8, new PathfinderGoalRandomStrollLand(this, 1.0));
        this.bO.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.bO.a(10, new PathfinderGoalRandomLookaround(this));
        this.bP.a(1, (new PathfinderGoalHurtByTarget(this)).a(new Class[0]));
    }

    protected void addTargetSelectorGoals() {
        this.bP.a(2, new GoalNearestAttackablePlayer(plugin, this, true));
    }

    public void setPos(Location location) {
        this.a_(location.getX(), location.getY(), location.getZ());
    }
}
