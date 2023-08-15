package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class NaturalMobSpawningPermission extends BasicPermission {
    public NaturalMobSpawningPermission() {
        super("NaturalMobSpawning", XMaterial.PIG_SPAWN_EGG, PermissionType.ISLAND);
    }
}
