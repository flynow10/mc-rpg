package com.wagologies.spigotplugin.command.commands;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.command.PlayerCommand;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

public class SpawnNoClipCommand extends PlayerCommand {

    public SpawnNoClipCommand(SpigotPlugin plugin) {
        super(plugin, "spawnnoclip");
    }

    @Override
    public boolean playerExecutor(Player player, String s, String[] strings) {
        Location location = player.getLocation();
        CraftWorld craftWorld = (CraftWorld)player.getWorld();
        NoClipArmorStand noClipArmorStand = new NoClipArmorStand(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ());
        craftWorld.addEntity(noClipArmorStand, CreatureSpawnEvent.SpawnReason.COMMAND);
        ArmorStand armorStand = (ArmorStand) noClipArmorStand.getBukkitEntity();
        armorStand.setCustomName("Test");
        armorStand.setCustomNameVisible(true);
        armorStand.setInvisible(true);
        armorStand.setVelocity(new Vector(0, 0.3,0));
        Bukkit.getScheduler().runTaskLater(plugin, armorStand::remove, 80);
        return true;
    }

    static class NoClipArmorStand extends EntityArmorStand {

        public NoClipArmorStand(World world, double d0, double d1, double d2) {
            super(world, d0, d1, d2);
        }

        @Override
        public boolean aV() {
            return false;
        }

        @Override
        public boolean a(BlockPosition blockposition, IBlockData iblockdata) {
            return false;
        }

        @Override
        public boolean a(DamageSource damagesource, float f) {
            return false;
        }

        @Override
        public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
            return EnumInteractionResult.d;
        }

        @Override
        public EntitySize a(EntityPose vec3d) {
            return new EntitySize(0.0F, 0.0F, false);
        }

        @Override
        public boolean g(double d0, double d1, double d2) {
            return true;
        }

        @Override
        public void c(boolean flag) {
            super.c(false);
        }

        @Override
        public void a(boolean flag, Vec3D vec3d) {
            super.a(false, vec3d);
        }

        @Override
        public void a(EnumMoveType enummovetype, Vec3D vec3d) {
            this.a_(this.dr() + vec3d.c, this.dt() + vec3d.d, this.dx() + vec3d.e);
        }
    }
}
