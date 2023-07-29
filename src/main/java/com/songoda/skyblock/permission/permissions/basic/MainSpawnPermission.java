package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class MainSpawnPermission extends BasicPermission {
    public MainSpawnPermission() {
        super("MainSpawn", CompatibleMaterial.EMERALD, PermissionType.OPERATOR);
    }
}
