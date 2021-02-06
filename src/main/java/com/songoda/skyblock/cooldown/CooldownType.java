package com.songoda.skyblock.cooldown;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum CooldownType {

    Biome,
    Creation,
    Deletion,
    Preview,
    Levelling,
    Ownership,
    Teleport;

    private static final Set<CooldownType> types = Collections.unmodifiableSet(EnumSet.allOf(CooldownType.class));

    public static Set<CooldownType> getTypes() {
        return types;
    }

}
