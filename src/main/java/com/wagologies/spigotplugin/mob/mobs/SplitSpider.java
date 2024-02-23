package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.mob.EntityMob;
import net.minecraft.world.entity.monster.EntityCaveSpider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SplitSpider extends EntityMob {
    @Override
    public Entity createBaseEntity(World world, Location location) {
        return world.spawnEntity(location, EntityType.CAVE_SPIDER);
    }

    @Override
    public String getName() {
        return "Mini Spider";
    }

    @Override
    public int getMaxHealth() {
        return 30;
    }
}
