package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class IslandPermission extends BasicPermission {
    public IslandPermission() {
        super("Island", CompatibleMaterial.OAK_SAPLING, PermissionType.OPERATOR);
    }
}
