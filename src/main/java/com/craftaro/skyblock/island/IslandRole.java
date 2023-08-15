package com.craftaro.skyblock.island;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum IslandRole {
    COOP,
    VISITOR,
    MEMBER,
    OPERATOR,
    OWNER;

    private static final Set<IslandRole> ROLES = Collections.unmodifiableSet(EnumSet.allOf(IslandRole.class));

    public static Set<IslandRole> getRoles() {
        return ROLES;
    }
}
