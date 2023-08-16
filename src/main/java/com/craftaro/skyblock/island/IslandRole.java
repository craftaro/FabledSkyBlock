package com.craftaro.skyblock.island;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum IslandRole {
    COOP("Coop"),
    VISITOR("Visitor"),
    MEMBER("Member"),
    OPERATOR("Operator"),
    OWNER("Owner");

    private final String friendlyName;

    IslandRole(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    private static final Set<IslandRole> ROLES = Collections.unmodifiableSet(EnumSet.allOf(IslandRole.class));

    public static Set<IslandRole> getRoles() {
        return ROLES;
    }
}
