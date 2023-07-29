package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class VisitorSpawnPermission extends BasicPermission {
    public VisitorSpawnPermission() {
        super("VisitorSpawn", CompatibleMaterial.NETHER_STAR, PermissionType.OPERATOR);
    }
}
