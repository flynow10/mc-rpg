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
        double radius = 3;
        CircleEffect circle = new CircleEffect(plugin, radius);
        Location location = player.getLocation();
        return true;
    }
}
