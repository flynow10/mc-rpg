package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.entity.DamageSource;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.mob.EntityMob;
import com.wagologies.spigotplugin.mob.MobType;
import com.wagologies.spigotplugin.mob.custom.EntityNoClimbSpider;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;

public class SplitterSpider extends EntityMob {

    public static final int SPLIT_COUNT = 2;

    public SplitterSpider(SpigotPlugin plugin) {
        super(plugin);
    }

    @Override
    public LivingEntity createEntity(World world, Location location) {
        CraftWorld craftWorld = (CraftWorld) world;
        EntityNoClimbSpider noClimbSpider = new EntityNoClimbSpider(plugin, craftWorld.getHandle());
        noClimbSpider.setPos(location);
        return (LivingEntity) noClimbSpider.getBukkitEntity();
    }

    @Override
    public int getMaxHealth() {
        return 60;
    }

    @Nullable
    @Override
    public RPGItem getHeldItem() {
        return null;
    }

    @Override
    public Armor[] getArmor() {
        return new Armor[4];
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_SPIDER_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_SPIDER_HURT;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if(damageSource.hasAttacker()) {
            split();
        }
    }

    protected void split() {
        List<Player> players= this.entity.getWorld().getPlayers();
        Player closestPlayer = null;
        double playerDistance = Double.MAX_VALUE;
        for(Player player : players) {
            double distance = player.getLocation().distance(this.entity.getLocation());
            if(distance < playerDistance) {
                playerDistance = distance;
                closestPlayer = player;
            }
        }
        Vector playerLoc = new Vector(0, 0, 0);
        if(closestPlayer != null) {
            playerLoc = closestPlayer.getLocation().toVector();
        }

        Vector pointToEntity = playerLoc.subtract(this.entity.getLocation().toVector());

        for (int i = 0; i < SPLIT_COUNT; i++) {
            SplitSpider spider = new SplitSpider(plugin);
            plugin.getEntityManager().spawn(spider, this.entity.getLocation());
            Vector velocity = pointToEntity.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.4);
            if((i % 2) == 0) {
                velocity = velocity.multiply(-1);
            }

            velocity = velocity.add(new Vector(0, 0.4, 0));

            spider.getMainEntity().setVelocity(velocity);
        }
    }

    @Override
    public MobType getType() {
        return MobType.SPLITTER_SPIDER;
    }
}
