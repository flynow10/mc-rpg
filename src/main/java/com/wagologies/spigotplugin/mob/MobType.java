package com.wagologies.spigotplugin.mob;

import com.wagologies.spigotplugin.mob.mobs.*;

public enum MobType {
    KOBOLD("Kobold", Kobold.class),
    KOBOLD2("Kobold2", Kobold2.class),
    GELATINOUS_CUBE("GelatinousCube", GelatinousCube.class),
    SPLITTER_SPIDER("SplitterSpider", SplitterSpider.class),
    SPLIT_SPIDER("SplitSpider", SplitSpider.class);

    private final String name;
    private final Class<? extends Mob> mobClass;
    MobType(String name, Class<? extends Mob> mobClass) {
        this.name = name;
        this.mobClass = mobClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Mob> getMobClass() {
        return mobClass;
    }
}
