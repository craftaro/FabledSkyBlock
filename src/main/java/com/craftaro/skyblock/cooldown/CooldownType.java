package com.craftaro.skyblock.cooldown;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum CooldownType {
    BIOME("Biome"),
    CREATION("Creation"),
    DELETION("Deletion"),
    PREVIEW("Preview"),
    LEVELLING("Levelling"),
    OWNERSHIP("Ownership"),
    TELEPORT("Teleport");

    private final String friendlyName;

    CooldownType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    private static final Set<CooldownType> TYPES = Collections.unmodifiableSet(EnumSet.allOf(CooldownType.class));

    public static Set<CooldownType> getTypes() {
        return TYPES;
    }
}
