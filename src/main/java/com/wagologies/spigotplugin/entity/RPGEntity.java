package com.wagologies.spigotplugin.entity;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.event.RPGEntityDeathEvent;
import com.wagologies.spigotplugin.item.Armor;
import com.wagologies.spigotplugin.item.RPGItem;
import com.wagologies.spigotplugin.item.MeleeWeapon;
import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public abstract class RPGEntity {
    public static final EntityDamageEvent.DamageCause[] NATURAL_CAUSES = new EntityDamageEvent.DamageCause[] {
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.LAVA
    };

    public static final EntityDamageEvent.DamageCause[] NATURAL_FIRE = new EntityDamageEvent.DamageCause[] {
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.LAVA
    };
    protected final SpigotPlugin plugin;
    protected int tickCount = 0;
    private int health = getMaxHealth();
    private boolean dead;
    private boolean removed = false;
    private final AbilityScores abilityScores = new AbilityScores();
    private boolean inCombat = false;
    private int inCombatTicks = 0;
    private int invulnerableTicks = 0;
    private int lastDamage = 0;
    private StatusEffects[] statusEffects;

    public abstract void playHurtAnimation();
    public abstract int getMaxHealth();
    public abstract Location getLocation();
    public abstract Location getEyeLocation();
    public abstract World getWorld();
    public abstract BoundingBox getBoundingBox();
    public abstract Entity getMainEntity();
    @Nullable
    public abstract RPGItem getHeldItem();
    public abstract boolean isInvulnerable();
    public abstract Armor[] getArmor();
    public abstract String getName();

    public RPGEntity(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    public void kill() {
        damage(new DamageSource(DamageSource.DamageType.COMMAND), Integer.MAX_VALUE);
    }

    public void tick() {
        tickCount ++;
        if(invulnerableTicks > 0) {
            invulnerableTicks--;
        }
        if(inCombat) {
            if(inCombatTicks > 0) {
                inCombatTicks--;
            } else {
                setInCombat(false);
            }
        }
    }

    public void damage(DamageSource damageSource, int damage) {
        if(isRemoved() || isDeadOrDying()) {
            return;
        }
        if(isInvulnerable() && !damageSource.getDamageType().has(DamageSource.DamageTags.IGNORE_INVULNERABLE)) {
            return;
        }
        if(this.invulnerableTicks > 10 && !(damageSource.getDamageType().has(DamageSource.DamageTags.BYPASS_COOLDOWN))) {
            if(damage <= this.lastDamage) {
                return;
            }
            int tempDamage = damage;
            damage = this.lastDamage - damage;
            this.lastDamage = tempDamage;
        } else {
            this.lastDamage = damage;
            this.invulnerableTicks = 20;
        }
        damage = calculateArmorAbsorption(damageSource, damage);

//        plugin.getLogger().info(String.format("Damaged: %s Amount: %d", getName(), damage));

        if(damage != 0) {
            setHealth(Math.max(0, this.health - damage));
        }

        Location damageLocation = damageSource.getDamageLocation();
        if(damageLocation != null && !damageSource.getDamageType().has(DamageSource.DamageTags.NO_KNOCKBACK)) {
            double d0 = damageLocation.getX() - getLocation().getX();

            double d1;
            for(d1 = damageLocation.getZ() - getLocation().getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                d0 = (Math.random() - Math.random()) * 0.01D;
            }

            this.knockback(0.4F, d0, d1);
        }

        playHurtAnimation();

        if(!damageSource.getDamageType().has(DamageSource.DamageTags.NON_COMBAT_DAMAGE)) {
            setInCombat(true);
        }

        if(isDeadOrDying()) {
            getWorld().playSound(getLocation(), getDeathSound(), getVoiceVolume(), getVoicePitch());
            die(damageSource);
        } else {
            playSound(getHurtSound(), getVoiceVolume(), getVoicePitch());
        }
    }

    public void knockback(double strength, double x, double z) {
        strength *= 1.0D - (double) this.getWeight() / 40;
        if (strength > 0.0D) {
            Vector previousVel = this.getMainEntity().getVelocity();
            Vector knockbackVel = (new Vector(x, 0.0D, z)).normalize().multiply(strength);
            Vector newVel = new Vector(previousVel.getX() / 2.0D - knockbackVel.getX(), this.getMainEntity().isOnGround() ? Math.min(0.4D, previousVel.getY() / 2.0D + strength) : previousVel.getY(), previousVel.getZ() / 2.0D - knockbackVel.getZ());
            this.getMainEntity().setVelocity(newVel);
        }
    }

    public void playSound(Sound sound, float volume, float pitch) {
        getWorld().playSound(getMainEntity(), sound, volume, pitch);
    }

    protected int calculateArmorAbsorption(DamageSource damageSource, int damage) {
        DamageSource.DamageType damageType = damageSource.getDamageType();
        if(damageType.has(DamageSource.DamageTags.BYPASS_ARMOR)) {
            return damage;
        }
        int armorClass = getActualArmorClass();
        if(damageType.has(DamageSource.DamageTags.WEAK_ARMOR)) {
            armorClass /= 2;
        }
        float multiplier = ((armorClass * 4) - ((float) damage /4))/450;
        return (int) ((float)damage * Math.min(1, 1 - multiplier));
    }

    public void die(DamageSource damageSource) {
        if(!dead) {
            dead = true;
            RPGEntityDeathEvent event = new RPGEntityDeathEvent(this, damageSource);
            Bukkit.getPluginManager().callEvent(event);
            remove(true);
        }
    }

    public boolean doDamageTarget(RPGEntity target) {
        if(target.isInvulnerable()) {
            return false;
        }
        int damage = this.getAbilityScores().getModifier(AbilityScores.AbilityScore.STRENGTH);
        DamageSource.DamageType damageType = DamageSource.DamageType.BLUNT;
        RPGItem item = this.getHeldItem();
        if(item instanceof MeleeWeapon meleeWeapon) {
            damage += meleeWeapon.getBaseDamage();
            damageType = meleeWeapon.getDamageType();
        }
        if(this instanceof RPGPlayer && this.getMainEntity() instanceof HumanEntity humanEntity) {
            float attackCooldown = humanEntity.getAttackCooldown();
            if(attackCooldown < 1) {
                float attackCooldownMultiplier = attackCooldown * 2f / 3f + 0.33333334f;
                damage = (int) (damage * attackCooldownMultiplier * 0.5);
            }
        }
        target.damage(new DamageSource(damageType, false, this), Math.max(damage,0));
        return true;
    }

    public void onNaturalDamage(EntityDamageEvent event) {
        if(Arrays.stream(NATURAL_CAUSES).anyMatch(cause -> cause.equals(event.getCause()))) {
            int damage = (int) Math.round((event.getDamage()/40)*this.getMaxHealth());
            DamageSource.DamageType damageType = DamageSource.DamageType.NATURAL;
            if(Arrays.stream(NATURAL_FIRE).anyMatch(cause -> cause.equals(event.getCause()))) {
                damageType = DamageSource.DamageType.NATURAL_FIRE;
            }
            damage(new DamageSource(damageType, false), damage);
        }
    }

    public boolean canTarget(RPGEntity entity) {
        return !entity.isInvulnerable() && this instanceof RPGPlayer ^ entity instanceof RPGPlayer;
    }

    public void remove(boolean isDead) {
        plugin.getEntityManager().removeEntity(this);
        plugin.getSpellManager().cancelEntitySpells(this);
        removed = true;
    }
    public int getWeight() {
        Armor[] armor = getArmor();
        return Arrays.stream(armor).filter(Objects::nonNull).mapToInt(Armor::getWeight).sum();
    }

    public int getArmorClass() {
        Armor[] armor = getArmor();
        return Arrays.stream(armor).filter(Objects::nonNull).mapToInt(Armor::getArmorClass).sum();
    }

    public int getActualArmorClass() {
        return Math.max(0, getArmorClass() + abilityScores.getModifier(AbilityScores.AbilityScore.CONSTITUTION));
    }

    public boolean isEntityPart(Entity entity) {
        return getMainEntity().equals(entity);
    }

    public boolean isDeadOrDying() {
        return health <= 0;
    }

    public AbilityScores getAbilityScores() {
        return abilityScores;
    }

    public int getInCombatTicks() {
        return inCombatTicks;
    }

    public StatusEffects[] getStatusEffects() {
        return statusEffects;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isRemoved() { return removed; }

    public boolean isInCombat() {
        return inCombat;
    }

    protected void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
        if(inCombat) {
            inCombatTicks = 200;
        } else {
            inCombatTicks = 0;
        }
    }

    protected void setAbilityScores(int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        getAbilityScores().setScores(strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    public int getCombatTicks() {
        return inCombatTicks;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int newHealth) {
        this.health = Math.min(newHealth, getMaxHealth());
    }

    public Sound getDeathSound() {
        return Sound.ENTITY_GENERIC_DEATH;
    }
    public Sound getHurtSound() {
        return Sound.ENTITY_GENERIC_HURT;
    }

    public float getVoiceVolume() {
        return 1.0f;
    }

    public float getVoicePitch() {
        return (float)(Math.random() - Math.random()) * 0.2F + 1.0F;
    }
}
