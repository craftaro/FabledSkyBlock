package com.songoda.skyblock.cooldown;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum CooldownType {
    BIOME,
    CREATION,
    DELETION,
    PREVIEW,
    LEVELLING,
    OWNERSHIP,
    TELEPORT;

    private static final Set<CooldownType> TYPES = Collections.unmodifiableSet(EnumSet.allOf(CooldownType.class));

    public static Set<CooldownType> getTypes() {
        return TYPES;
    }
}
