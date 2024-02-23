package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.mob.Mob;
import com.wagologies.spigotplugin.mob.MobManager;
import com.wagologies.spigotplugin.player.PlayerManager;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class BaseSpell {
    protected SpellManager spellManager;
    protected SpellCaster spellCaster;
    protected boolean isActive = true;
    public BaseSpell(SpellManager spellManager, SpellCaster spellCaster) {
        this.spellManager = spellManager;
        this.spellCaster = spellCaster;
    }

    public abstract void tick();

    public void endSpell() {
        isActive = false;
        spellManager.removeSpell(this);
    }

    public boolean isActive() {
        return isActive;
    }

    public World getSpellWorld() {
        return spellCaster.getCastingEntity().getWorld();
    }

    public MagicAffectable findMagicAffectedEntity(Entity entity) {
        if(entity instanceof Player player && !entity.hasMetadata("NPC")) {
            PlayerManager playerManager = spellManager.getPlugin().getPlayerManager();
            return playerManager.getPlayer(player);
        } else {
            MobManager mobManager = spellManager.getPlugin().getMobManager();
            return mobManager.getMobs().stream().filter(mob -> mob.getEntity().equals(entity)).findFirst().orElse(null);
        }
    }
}
