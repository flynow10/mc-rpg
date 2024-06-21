package com.wagologies.spigotplugin.mob.custom;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.custom.pathfinder.GoalNearestAttackablePlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;

public class EntityNoClimbSpider extends EntitySpider {
    private final SpigotPlugin plugin;
    public EntityNoClimbSpider(SpigotPlugin plugin, World world) {
        super(EntityTypes.aT, world);
        this.plugin = plugin;
        addTargetSelectorGoals();
    }

    @Override
    protected void B() {
        this.bO.a(1, new PathfinderGoalFloat(this));
        this.bO.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.bO.a(4, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.bO.a(5, new PathfinderGoalRandomStrollLand(this, 0.8));
        this.bO.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.bO.a(6, new PathfinderGoalRandomLookaround(this));
    }

    protected void addTargetSelectorGoals() {
        this.bP.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.bP.a(2, new GoalNearestAttackablePlayer(plugin, this, true));
    }

    public void setPos(Location location) {
        this.a_(location.getX(), location.getY(), location.getZ());
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new Navigation(this, world);
    }
}
