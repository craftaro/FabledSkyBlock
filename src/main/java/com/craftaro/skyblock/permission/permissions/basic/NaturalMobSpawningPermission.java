package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class NaturalMobSpawningPermission extends BasicPermission {
    public NaturalMobSpawningPermission() {
        super("NaturalMobSpawning", XMaterial.PIG_SPAWN_EGG, PermissionType.ISLAND);
    }
}
