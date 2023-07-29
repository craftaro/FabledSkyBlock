package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class BiomePermission extends BasicPermission {
    public BiomePermission() {
        super("Biome", CompatibleMaterial.MAP, PermissionType.OPERATOR);
    }
}
