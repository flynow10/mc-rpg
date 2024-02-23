package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.mob.EntityMob;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class SplitterSpider extends EntityMob {

    public static final int SPLIT_COUNT = 2;
    @Override
    public Entity createBaseEntity(World world, Location location) {
        return world.spawnEntity(location, EntityType.SPIDER);
    }

    @Override
    public String getName() {
        return "Splitter";
    }

    @Override
    public int getMaxHealth() {
        return 60;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        this.split();
    }

    protected void split() {
        List<Player> players= this.baseEntity.getWorld().getPlayers();
        Player closestPlayer = null;
        double playerDistance = Double.MAX_VALUE;
        for(Player player : players) {
            double distance = player.getLocation().distance(this.baseEntity.getLocation());
            if(distance < playerDistance) {
                playerDistance = distance;
                closestPlayer = player;
            }
        }
        Vector playerLoc = new Vector(0, 0, 0);
        if(closestPlayer != null) {
            playerLoc = closestPlayer.getLocation().toVector();
        }

        Vector pointToEntity = playerLoc.subtract(this.baseEntity.getLocation().toVector());

        for (int i = 0; i < SPLIT_COUNT; i++) {
            SplitSpider spider = new SplitSpider();
            mobManager.spawn(spider, this.baseEntity.getLocation());
            Vector velocity = pointToEntity.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.4);
            if((i % 2) == 0) {
                velocity = velocity.multiply(-1);
            }

            velocity = velocity.add(new Vector(0, 0.4, 0));

            spider.getBaseEntity().setVelocity(velocity);
        }
    }
}
