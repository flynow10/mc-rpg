package com.wagologies.spigotplugin.mob.custom.pathfinding;

import net.minecraft.server.v1_8_R3.*;

public class PathfinderGoalWalkToLoc extends PathfinderGoal {

    EntityInsentient entityInsentient;
    BlockPosition targetLocation;

    public PathfinderGoalWalkToLoc(EntityInsentient entityInsentient) {
        this.entityInsentient = entityInsentient;
        targetLocation = new BlockPosition(0, 0, 0);
        a(1);
    }

    @Override
    public boolean a() {
        return true;
    }

    @Override
    public boolean b() {
        return !entityInsentient.getNavigation().m();
    }

    @Override
    public void c() {
        PathEntity path = entityInsentient.getNavigation().a(targetLocation);
        entityInsentient.getNavigation().a(path, 1);
    }

    public BlockPosition getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(BlockPosition targetLocation) {
        this.targetLocation = targetLocation;
    }
}