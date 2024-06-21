package com.wagologies.spigotplugin.mob.custom;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.mob.custom.pathfinder.GoalNearestAttackablePlayer;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityRemoveEvent;

import java.util.EnumSet;

public class EntityGelatinousCube extends EntitySlime {

    private final SpigotPlugin plugin;
    public EntityGelatinousCube(SpigotPlugin plugin, World world) {
        super(EntityTypes.aM, world);
        this.plugin = plugin;
        addTargetSelectorGoals();
        this.bL = new GelatinousCubeController(this);
        this.ai().a(world);
    }

    @Override
    protected void B() {
        this.bO.a(1, new PathfinderGoalSlimeRandomJump(this));
        this.bO.a(2, new PathfinderGoalSlimeNearestPlayer(this));
        this.bO.a(3, new PathfinderGoalSlimeRandomDirection(this));
        this.bO.a(5, new PathfinderGoalSlimeIdle(this));
    }

    protected void addTargetSelectorGoals() {
        this.bP.a(1, new GoalNearestAttackablePlayer(plugin, this, true, (player) -> Math.abs(player.getLocation().getY() - this.dt()) <= 4.0));
    }


    protected int A() {
        return this.ag.a(20) + 10;
    }

    float getSoundPitch() {
        float f = this.gg() ? 1.4F : 0.8F;
        return ((this.ag.i() - this.ag.i()) * 0.2F + 1.0F) * f;
    }

    public void setPos(Location location) {
        this.a_(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void remove(RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        this.b(entity_removalreason);
        this.bz.a();
    }

    private static class GelatinousCubeController extends ControllerMove {
        private float yRot;
        private int jumpDelay;
        private final EntityGelatinousCube slime;
        private boolean isAggressive;

        public GelatinousCubeController(EntityGelatinousCube entitySlime) {
            super(entitySlime);
            this.slime = entitySlime;
            this.yRot = 180.0F * entitySlime.dC() / 3.1415927F;
        }

        public void a(float f, boolean flag) {
            this.yRot = f;
            this.isAggressive = flag;
        }

        public void a(double d0) {
            this.h = d0;
            this.k = Operation.b;
        }

        public void a() {
            this.d.r(this.a(this.d.dC(), this.yRot, 90.0F));
            this.d.aW = this.d.dC();
            this.d.aU = this.d.dC();
            if (this.k != Operation.b) {
                this.d.A(0.0F);
            } else {
                this.k = Operation.a;
                if (this.d.aC()) {
                    this.d.w((float)(this.h * this.d.b(GenericAttributes.m)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.A();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        if(this.isAggressive) {
                            this.slime.M().a();
                        }
                        if (this.slime.gf() > 0) {
                            this.slime.a(this.slime.ge(), this.slime.eW(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.bk = 0.0F;
                        this.slime.bm = 0.0F;
                        this.d.w(0.0F);
                    }
                } else {
                    this.d.w((float)(this.h * this.d.b(GenericAttributes.m)));
                }
            }

        }
    }

    private static class PathfinderGoalSlimeIdle extends PathfinderGoal {
        private final EntitySlime a;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(Type.c, Type.a));
        }

        public boolean a() {
            return !this.a.bO();
        }

        public void e() {
            ControllerMove controllermove = this.a.K();
            if (controllermove instanceof GelatinousCubeController entityslime_controllermoveslime) {
                entityslime_controllermoveslime.a(1.0);
            }

        }
    }

    private static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {
        private final EntitySlime a;
        private int b;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(Type.b));
        }

        public boolean a() {
            EntityLiving entityliving = this.a.q();
            return entityliving == null ? false : (!this.a.c(entityliving) ? false : this.a.K() instanceof GelatinousCubeController);
        }

        public void c() {
            this.b = b(300);
            super.c();
        }

        public boolean b() {
            EntityLiving entityliving = this.a.q();
            return entityliving == null ? false : (!this.a.c(entityliving) ? false : --this.b > 0);
        }

        public boolean T_() {
            return true;
        }

        public void e() {
            EntityLiving entityliving = this.a.q();
            if (entityliving != null) {
                this.a.a(entityliving, 10.0F, 10.0F);
            }

            ControllerMove controllermove = this.a.K();
            if (controllermove instanceof GelatinousCubeController entityslime_controllermoveslime) {
                entityslime_controllermoveslime.a(this.a.dC(), !this.a.gg() && this.a.cY());
            }

        }
    }

    private static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {
        private final EntityGelatinousCube a;
        private float b;
        private int c;

        public PathfinderGoalSlimeRandomDirection(EntityGelatinousCube entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(Type.b));
        }

        public boolean a() {
            return this.a.q() == null && (this.a.aC() || this.a.aZ() || this.a.bn() || this.a.a((MobEffectList) MobEffects.y)) && this.a.K() instanceof GelatinousCubeController;
        }

        public void e() {
            if (--this.c <= 0) {
                this.c = this.a(40 + this.a.eg().a(60));
                this.b = (float)this.a.eg().a(360);
            }

            ControllerMove controllermove = this.a.K();
            if (controllermove instanceof GelatinousCubeController entityslime_controllermoveslime) {
                entityslime_controllermoveslime.a(this.b, false);
            }

        }
    }

    private static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {
        private final EntityGelatinousCube a;

        public PathfinderGoalSlimeRandomJump(EntityGelatinousCube entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(Type.c, Type.a));
            entityslime.N().a(true);
        }

        public boolean a() {
            return (this.a.aZ() || this.a.bn()) && this.a.K() instanceof GelatinousCubeController;
        }

        public boolean T_() {
            return true;
        }

        public void e() {
            if (this.a.eg().i() < 0.8F) {
                this.a.M().a();
            }

            ControllerMove controllermove = this.a.K();
            if (controllermove instanceof GelatinousCubeController entityslime_controllermoveslime) {
                entityslime_controllermoveslime.a(1.2);
            }

        }
    }
}
