package com.wagologies.spigotplugin.mob.custom;


import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;

public class CustomEntityKobold extends EntityZombie {

    public CustomEntityKobold(World world) {
        super(world);
    }

    @Override
    protected String z() {
        return "mob.wolf.growl";
    }

    @Override
    protected String bo() {
        return "mob.wolf.hurt";
    }

    @Override
    protected  String bp() {
        return "mob.wolf.death";
    }

    @Override
    protected float bC() {
        return 1.6F;
    }
}
