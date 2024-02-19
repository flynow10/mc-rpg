package com.wagologies.spigotplugin.mob.custom;

import com.google.common.base.Predicate;
import net.minecraft.server.v1_8_R3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class CustomEntityGelatinousCube extends EntitySlime {
    public CustomEntityGelatinousCube(World world) {
        super(world);
        this.setSize(7);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2F + 0.1F * (float)3);
        this.moveController = new ControllerMoveGelatinous(this);
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        this.goalSelector.a(1, new PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.a(2, new PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.a(3, new PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.a(5, new PathfinderGoalSlimeIdle(this));
        this.targetSelector.a(1, new PathfinderGoalTargetNearestRealPlayer(this));
    }

    static class PathfinderGoalTargetNearestRealPlayer extends PathfinderGoal {
        private static final Logger a = LogManager.getLogger();
        private EntityInsentient b;
        private final Predicate<Entity> c;
        private final PathfinderGoalNearestAttackableTarget.DistanceComparator d;
        private EntityLiving e;

        public PathfinderGoalTargetNearestRealPlayer(EntityInsentient entityinsentient) {
            this.b = entityinsentient;
            if (entityinsentient instanceof EntityCreature) {
                a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
            }

            this.c = new Predicate() {
                public boolean a(Entity entity) {
                    if (!(entity instanceof EntityHuman)) {
                        return false;
                    } else if (((EntityHuman)entity).abilities.isInvulnerable) {
                        return false;
                    } else if(entity.getBukkitEntity().hasMetadata("NPC")) {
                        return false;
                    } else {
                        double d0 = PathfinderGoalTargetNearestRealPlayer.this.f();
                        if (entity.isSneaking()) {
                            d0 *= 0.800000011920929;
                        }

                        if (entity.isInvisible()) {
                            float f = ((EntityHuman)entity).bY();
                            if (f < 0.1F) {
                                f = 0.1F;
                            }

                            d0 *= (double)(0.7F * f);
                        }

                        return (double)entity.g(PathfinderGoalTargetNearestRealPlayer.this.b) > d0 ? false : PathfinderGoalTarget.a(PathfinderGoalTargetNearestRealPlayer.this.b, (EntityLiving)entity, false, true);
                    }
                }

                public boolean apply(Object object) {
                    return this.a((Entity)object);
                }
            };
            this.d = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entityinsentient);
        }

        public boolean a() {
            double d0 = this.f();
            List list = this.b.world.a(EntityHuman.class, this.b.getBoundingBox().grow(d0, 4.0, d0), this.c);
            Collections.sort(list, this.d);
            if (list.isEmpty()) {
                return false;
            } else {
                this.e = (EntityLiving)list.get(0);
                return true;
            }
        }

        public boolean b() {
            EntityLiving entityliving = this.b.getGoalTarget();
            if (entityliving == null) {
                return false;
            } else if (!entityliving.isAlive()) {
                return false;
            } else if (entityliving instanceof EntityHuman && ((EntityHuman)entityliving).abilities.isInvulnerable) {
                return false;
            } else {
                ScoreboardTeamBase scoreboardteambase = this.b.getScoreboardTeam();
                ScoreboardTeamBase scoreboardteambase1 = entityliving.getScoreboardTeam();
                if (scoreboardteambase != null && scoreboardteambase1 == scoreboardteambase) {
                    return false;
                } else {
                    double d0 = this.f();
                    return this.b.h(entityliving) > d0 * d0 ? false : !(entityliving instanceof EntityPlayer) || !((EntityPlayer)entityliving).playerInteractManager.isCreative();
                }
            }
        }

        public void c() {
            this.b.setGoalTarget(this.e, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
            super.c();
        }

        public void d() {
            this.b.setGoalTarget((EntityLiving)null);
            super.c();
        }

        protected double f() {
            AttributeInstance attributeinstance = this.b.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
            return attributeinstance == null ? 16.0 : attributeinstance.getValue();
        }
    }

    static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {
        private CustomEntityGelatinousCube a;
        private int b;

        public PathfinderGoalSlimeNearestPlayer(CustomEntityGelatinousCube entityslime) {
            this.a = entityslime;
            this.a(2);
        }

        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();
            return entityliving == null ? false : (!entityliving.isAlive() ? false : !(entityliving instanceof EntityHuman) || !((EntityHuman)entityliving).abilities.isInvulnerable);
        }

        public void c() {
            this.b = 300;
            super.c();
        }

        public boolean b() {
            EntityLiving entityliving = this.a.getGoalTarget();
            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && ((EntityHuman)entityliving).abilities.isInvulnerable ? false : --this.b > 0));
        }

        public void e() {
            this.a.a(this.a.getGoalTarget(), 10.0F, 10.0F);
            ((ControllerMoveGelatinous)this.a.getControllerMove()).a(this.a.yaw, this.a.ci());
        }
    }

    static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {
        private EntitySlime a;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(5);
            ((Navigation)entityslime.getNavigation()).d(true);
        }

        public boolean a() {
            return this.a.V() || this.a.ab();
        }

        public void e() {
            if (this.a.bc().nextFloat() < 0.8F) {
                this.a.getControllerJump().a();
            }

            ((ControllerMoveGelatinous)this.a.getControllerMove()).a(1.2);
        }
    }

    static class PathfinderGoalSlimeIdle extends PathfinderGoal {
        private CustomEntityGelatinousCube a;

        public PathfinderGoalSlimeIdle(CustomEntityGelatinousCube entityslime) {
            this.a = entityslime;
            this.a(5);
        }

        public boolean a() {
            return true;
        }

        public void e() {
            ((ControllerMoveGelatinous)this.a.getControllerMove()).a(1.0);
        }
    }

    static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {
        private EntitySlime a;
        private float b;
        private int c;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(2);
        }

        public boolean a() {
            return this.a.getGoalTarget() == null && (this.a.onGround || this.a.V() || this.a.ab());
        }

        public void e() {
            if (--this.c <= 0) {
                this.c = 40 + this.a.bc().nextInt(60);
                this.b = (float)this.a.bc().nextInt(360);
            }

            ((ControllerMoveGelatinous)this.a.getControllerMove()).a(this.b, false);
        }
    }

    static class ControllerMoveGelatinous extends ControllerMove {

        private float g;
        private int h;
        private final CustomEntityGelatinousCube i;
        private boolean j;
        public ControllerMoveGelatinous(CustomEntityGelatinousCube entityInsentient) {
            super(entityInsentient);
            i = entityInsentient;
        }

        public void a(float f, boolean flag) {
            this.g = f;
            this.j = flag;
        }

        public void a(double d0) {
            this.e = d0;
            this.f = true;
        }

        @Override
        public void c() {
            this.a.yaw = this.a(this.a.yaw, this.g, 30.0F);
            this.a.aK = this.a.yaw;
            this.a.aI = this.a.yaw;
            if (!this.f) {
                this.a.n(0.0F);
            } else {
                this.f = false;
                if (this.a.onGround) {
                    this.a.k((float)(this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                    if (this.h-- <= 0) {
                        this.h = this.i.cg();
                        if (this.j) {
                            this.h /= 3;
                        }

                        if(this.j) {
                            this.i.getControllerJump().a();
                        }
                        if (this.i.cn()) {
                            this.i.makeSound(this.i.ck(), this.i.bB(), ((this.i.bc().nextFloat() - this.i.bc().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        }
                    } else {
                        this.i.aZ = this.i.ba = 0.0F;
                        this.a.k(0.0F);
                    }
                } else {
                    this.a.k((float)(this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                }
            }

        }
    }
}
