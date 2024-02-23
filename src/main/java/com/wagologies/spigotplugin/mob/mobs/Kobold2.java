package com.wagologies.spigotplugin.mob.mobs;

import com.wagologies.spigotplugin.mob.PlayerMob;
import com.wagologies.spigotplugin.player.RPGPlayer;
import com.wagologies.spigotplugin.spell.SpellType;
import net.citizensnpcs.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;

import java.util.List;
import java.util.Vector;

public class Kobold2 extends PlayerMob {
    @Override
    public int getMaxHealth() {
        return 50;
    }

    @Override
    public String getName() {
        return "Kobold";
    }

    @Override
    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwODM3MzE4ODM4NiwKICAicHJvZmlsZUlkIiA6ICJmZmU5MzczY2YyMDM0OWFhYTJlN2NiYzJkZmY2M2I5MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWxvblR1bmExIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdiYTQyOGRhZGFjMzA5NjJjODM5NTkzYTg2MWQ2ODI3NjhkYTcxN2QwMDI5MjViODc1NzgwNjI0OWM0NWEzODIiCiAgICB9CiAgfQp9";
    }

    @Override
    public String getSkinSignature() {
        return "JlK8knf5b0uZdQyDRd6oMnzEyZC7OoxOQrLSPI1lXmwmegqsRlgPKuvzl8P6+jjKxMWpS5YiQ374kbUB/uW4Boib8m8i8/r5gCdD8ESDSgxwKeh4rjDl0tV3gJwFZEsxFDFgrsA2bBd8NftWKAb9uYCwFYSmfvn7VQAkMcUt9rYYwJjGS26OIOU06da30xIBOA5P8D9PwQnAwawB3z73kKdcanG/PjO2VdyIb2Iyt4KJoFbw4Yl2zE/4HeyxA67fFVV+ti84wHNqXG2P7KWTBb1Kb0tqtxEEiD7L0FKzrm3SvgHBcxzhX5QdBC8DXOJQgcWDx1grfz4KxxVXjz9IDoHb/7cZBrjgbfGRROtEgonD0BvQHiaZXwl6kkaAsxY04Lrjg0kZt2P3FCdciA3L0ixc+hqR1cwczeJIDgBeZ7bNpN3967xE6kL4sUinKC+kgyJQSIDjFKrRXRRjNBTsGYm50o/3K+ozgsmyCKcUr9aeoJR1wugnG4Uc12XDKY6i4T6fdt2KblGcXS+V3WUln5Toh1xoanWHb9QjiCjojehsf19t+Zg+mbFv2y1s1vA+I+ul218P5s7Uc7caIkbFR3IHupRvwB0Vi23SbxUgKGGYOkyfOgjE2lkm/w8WEG7Hsp/TKjgOjEiaw4GF0z+fgCso9P/tNIoMGGOuw609Edw=";
    }

    @Override
    public void tick() {
        super.tick();
        if(ticksPassed % 100 == 0) {
            if(Math.random() > 0.5) {
                castEldritchBlast();
            }
        }
    }

    public void castEldritchBlast() {
        List<RPGPlayer> players = this.mobManager.getPlugin().getPlayerManager().getPlayers();
        Location npcLocation = this.npc.getStoredLocation();
        players.sort((a, b) -> (int) (b.getLocation().distanceSquared(npcLocation) - a.getLocation().distanceSquared(npcLocation)));
        for (RPGPlayer player : players) {
            double distance = player.getLocation().distance(npcLocation);
            if(distance >= 30) {
                break;
            }
            BlockIterator blockIterator = new BlockIterator(npc.getEntity().getWorld(), getEyeLocation().toVector(), player.getEyeLocation().subtract(npcLocation).toVector().normalize(), 0, (int) distance);
            mobManager.getPlugin().getLogger().info("Kobold cast a spell");
            boolean isBlocked = false;
            while(blockIterator.hasNext()) {
                Block block = blockIterator.next();
                if (!block.isEmpty()) {
                    isBlocked = true;
                    break;
                }
            }
            if(isBlocked) {
                continue;
            }
            npc.faceLocation(player.getLocation());
            mobManager.getPlugin().getSpellManager().castSpell(this, SpellType.EldritchBlast);
        }
    }
}
