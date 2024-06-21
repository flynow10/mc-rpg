package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.particle.CircleEffect;
import com.wagologies.spigotplugin.particle.Particle;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Jump extends BaseSpell {
    CircleEffect effect;
    Particle<?> jumpParticle;

    public Jump(SpellManager spellManager, RPGEntity spellCaster) {
        super(spellManager, spellCaster);
        if(spellCaster.getMainEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 2, false, true, true));
        }
        effect = new CircleEffect(spellManager.getPlugin(), 2);
        jumpParticle = new Particle<>(org.bukkit.Particle.REDSTONE, 1, new org.bukkit.Particle.DustOptions(Color.fromRGB(0xFDFF84), 1.5f));
    }

    @Override
    public void tick() {
        if(effect.getRadius() > 0.01) {
            effect.setRadius(effect.getRadius() - 0.3);
            if(effect.getRadius() <= 0.01) {
                getSpellWorld().playSound(spellCaster.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
                endSpell();
            }
            effect.draw(jumpParticle, spellCaster.getLocation());
        }
    }
}
