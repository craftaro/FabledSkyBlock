package com.songoda.skyblock.island;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum IslandRole {

    Coop,
    Visitor,
    Member,
    Operator,
    Owner;

    private static final Set<IslandRole> roles = Collections.unmodifiableSet(EnumSet.allOf(IslandRole.class));

    public static Set<IslandRole> getRoles() {
        return roles;
    }

}
