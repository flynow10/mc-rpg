package com.wagologies.spigotplugin.mob.custom;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.custom.pathfinder.GoalNearestAttackablePlayer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;

public class EntityPolarBear extends net.minecraft.world.entity.animal.EntityPolarBear {
    private final SpigotPlugin plugin;
    public EntityPolarBear(SpigotPlugin plugin, World var1) {
        super(EntityTypes.aA, var1);
        this.plugin = plugin;
        addTargetSelectorGoals();
    }

    @Override
    protected void B() {
        this.bO.a(0, new PathfinderGoalFloat(this));
        this.bO.a(1, new c());
        this.bO.a(5, new PathfinderGoalRandomStroll(this, 1.0));
        this.bO.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.bO.a(7, new PathfinderGoalRandomLookaround(this));
    }

    protected void addTargetSelectorGoals() {
        this.bP.a(1, new GoalNearestAttackablePlayer(plugin, this, true));
    }

    public void setPos(Location location) {
        this.a_(location.getX(), location.getY(), location.getZ());
    }

    class c extends PathfinderGoalMeleeAttack {
        public c() {
            super(EntityPolarBear.this, 1.25, true);
        }

        protected void a(EntityLiving var0) {
            if (this.b(var0)) {
                this.h();
                this.a.C(var0);
                EntityPolarBear.this.w(false);
            } else if (this.a.f(var0) < (double)((var0.dg() + 3.0F) * (var0.dg() + 3.0F))) {
                if (this.i()) {
                    EntityPolarBear.this.w(false);
                    this.h();
                }

                if (this.k() <= 10) {
                    EntityPolarBear.this.w(true);
                    EntityPolarBear.this.w();
                }
            } else {
                this.h();
                EntityPolarBear.this.w(false);
            }

        }

        public void d() {
            EntityPolarBear.this.w(false);
            super.d();
        }
    }
}
