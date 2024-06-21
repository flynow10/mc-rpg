package com.wagologies.spigotplugin.entity;

import com.wagologies.spigotplugin.item.WandCoreType;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class DamageSource {
    private DamageType damageType;
    private boolean isMagic;
    @Nullable
    private final RPGEntity attacker;
    @Nullable
    private final Location damageSourceLocation;

    public DamageSource(DamageType damageType) {
        this(damageType, false);
    }

    public DamageSource(DamageType damageType, boolean isMagic) {
        this(damageType, isMagic, null, null);
    }

    public DamageSource(DamageType damageType, boolean isMagic, RPGEntity attacker) {
        this(damageType, isMagic, attacker, null);
    }

    public DamageSource(DamageType damageType, boolean isMagic, @Nullable Location damageSourceLocation) {
        this(damageType, isMagic, null, damageSourceLocation);
    }

    public DamageSource(DamageType damageType, boolean isMagic, @Nullable RPGEntity attacker, @Nullable Location damageSourceLocation) {
        this.damageType = damageType;
        this.isMagic = isMagic;
        this.attacker = attacker;
        this.damageSourceLocation = damageSourceLocation;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public DamageSource setDamageType(DamageType damageType) {
        this.damageType = damageType;
        return this;
    }

    @Nullable
    public RPGEntity getAttacker() {
        return attacker;
    }

    public boolean hasAttacker() {
        return attacker != null;
    }

    @Nullable
    public Location getDamageLocation() {
        if(damageSourceLocation != null) {
            return damageSourceLocation;
        } else if(attacker != null) {
            return attacker.getLocation();
        } else {
            return null;
        }
    }

    @Nullable
    public Location getDamageLocationRaw() {
        return damageSourceLocation;
    }

    public boolean isMagic() {
        return isMagic;
    }

    public enum DamageType {
        BLUNT,
        SLICING,
        PIERCING,
        ACID,
        COLD(DamageTags.WEAK_ARMOR),
        LIGHTNING,
        FORCE(DamageTags.NO_KNOCKBACK),
        FIRE(DamageTags.WEAK_ARMOR),
        POISON(DamageTags.BYPASS_ARMOR, DamageTags.NO_KNOCKBACK),
        NATURAL(DamageTags.BYPASS_MAGIC, DamageTags.NON_COMBAT_DAMAGE),
        NATURAL_FIRE(DamageTags.NON_COMBAT_DAMAGE),
        COMMAND(DamageTags.BYPASS_ARMOR, DamageTags.BYPASS_MAGIC, DamageTags.IGNORE_INVULNERABLE, DamageTags.NON_COMBAT_DAMAGE, DamageTags.BYPASS_COOLDOWN);

        private final DamageTags[] damageTags;

        DamageType() {
            this(new DamageTags[0]);
        }

        DamageType(DamageTags... damageTags) {
            this.damageTags = damageTags;
        }

        public DamageTags[] getDamageTags() {
            return damageTags;
        }

        public boolean has(DamageTags... tags) {
            return new HashSet<>(List.of(damageTags)).containsAll(List.of(tags));
        }

        public static DamageType fromString(@Nullable String damageType) {
            if(damageType == null) {
                return null;
            } else {
                try {
                    return DamageType.valueOf(damageType);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
    }

    public enum DamageTags {
        BYPASS_ARMOR,
        WEAK_ARMOR,
        BYPASS_MAGIC,
        BYPASS_COOLDOWN,
        IGNORE_INVULNERABLE,
        NON_COMBAT_DAMAGE,
        NO_KNOCKBACK,
    }
}
