package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.command.PlayerCommand;
import com.wagologies.spigotplugin.particle.CircleEffect;
import com.wagologies.spigotplugin.particle.Particle;
import com.wagologies.spigotplugin.utils.Quaternion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleEffectCommand extends PlayerCommand {
    public ParticleEffectCommand(SpigotPlugin plugin) {
        super(plugin, "particleeffect");
    }

    public Player player;
    public CircleEffect effect;
    public int ticks = 0;
    public int task;
    public Location position;
    public Particle<?> particle;
    public int duration;

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        double radius = 3;
        this.ticks = 0;
        this.player = player;
        this.effect = new CircleEffect(plugin, radius);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::rotateCircle, 0, 1);
        position = player.getLocation().add(0, player.getEyeHeight() / 2, 0);
        DustOptions dustOptions = new DustOptions(Color.fromRGB(0x31a3dc), 1.3f);
        particle = new Particle<>(org.bukkit.Particle.REDSTONE, 1, dustOptions);
        duration = 600;
        if(strings.length >= 1) {
            if(strings[0].matches("\\d+")) {
                duration = Integer.parseInt(strings[0]);
            } else {
                particle = new Particle<>(org.bukkit.Particle.valueOf(strings[0]), 0, new Vector(0,0,0), null);
            }
        }
        if(strings.length == 2) {
            duration = Integer.parseInt(strings[1]);
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0],
                    Arrays.stream(org.bukkit.Particle.values()).map(org.bukkit.Particle::name).toList(), completions);
        }
        return completions;
    }

    private void rotateCircle() {
        if(ticks >= duration) {
            Bukkit.getScheduler().cancelTask(task);
            return;
        }
        ticks++;
        double t = (double) ticks / duration;
        double speed = 4;
        for(int j = 0; j < 6; j++) {
            Quaternion spin = Quaternion.FromAxisAngle(new Vector(0, 1, 0), t * Math.PI);
            Quaternion rotation = Quaternion.FromAxisAngle(new Vector(0, 1, 0), j * (2 * Math.PI / 6));
            Quaternion tilt = Quaternion.FromAxisAngle(new Vector(0, 0, 1), Math.PI / 3);
            Quaternion step = Quaternion.FromAxisAngle(new Vector(0,1,0), 2 * Math.PI * t * speed);
            Quaternion phase = Quaternion.FromAxisAngle(new Vector(0, 1, 0), j * (2 * Math.PI / 6));
            effect.setRotation(Quaternion.Compose(step, phase, tilt, rotation, spin));
            effect.setParticleCount(3);
            effect.draw(particle, position);

//            int count = 2;
//            for (int i = 0; i < count; i++) {
//                double phaseShifted = 2 * Math.PI * (t + ((double) i/count));
//                Vector point = new Vector(Math.cos(phaseShifted) * effect.getRadius(), 0, Math.sin(phaseShifted) * effect.getRadius());
//                point = rotation.multiply(tilt).rotate(point);
//                particle.spawnParticle(player.getLocation().add(0, player.getEyeHeight() /2, 0).add(point), player.getWorld());
//            }
        }
    }
}
