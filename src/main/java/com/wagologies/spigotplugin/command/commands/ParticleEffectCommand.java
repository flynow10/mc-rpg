package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.particle.CircleEffect;
import com.wagologies.spigotplugin.particle.LineEffect;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleEffectCommand extends PlayerCommand {
    public ParticleEffectCommand(SpigotPlugin plugin) {
        super(plugin, "particleeffect");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0xff0000), 0.5f);
        com.wagologies.spigotplugin.particle.Particle<Particle.DustOptions> particle = new com.wagologies.spigotplugin.particle.Particle<>(Particle.REDSTONE, 1, dustOptions);
        CircleEffect circle1 = new CircleEffect(plugin, 3, new Vector(0, 1, 0));
        circle1.drawForTicks(particle, player.getLocation(), 200);
        CircleEffect circle2 = new CircleEffect(plugin, 2, new Vector(1, 0, 0));
        circle2.drawForTicks(particle, player.getLocation(), 200);
        LineEffect lineEffect = new LineEffect(plugin, player.getLocation().add(10, 0, 0), player.getLocation().add(0, 0, 4));
        lineEffect.drawForTicks(particle, player.getLocation(), 200);
        return true;
    }
}
