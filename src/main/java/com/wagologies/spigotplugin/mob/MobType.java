package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.mob.mobs.*;

public enum MobType {
    BANDIT("Bandit", Bandit.class),
    KOBOLD("Kobold", Kobold.class),
    POLAR_BEAR("Polar Bear", PolarBear.class),
    GELATINOUS_CUBE("Gelatinous Cube", GelatinousCube.class),
    SPLITTER_SPIDER("Splitter Spider", SplitterSpider.class),
    SPLIT_SPIDER("Mini Spider", SplitSpider.class),
    DIRE_WOLF("Dire Wolf", DireWolf.class),
    WOLF("Wolf", Wolf.class),
    DUMMY("Dummy", Dummy.class),
    ICE_MAGE("Ice Mage", IceMage.class),
    ICE_WARRIOR("Ice Warrior", IceWarrior.class);

    private final String name;
    private final Class<? extends AbstractMob> mobClass;
    MobType(String name, Class<? extends AbstractMob> mobClass) {
        this.name = name;
        this.mobClass = mobClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends AbstractMob> getMobClass() {
        return mobClass;
    }
}
