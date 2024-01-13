package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class BiomePermission extends BasicPermission {
    public BiomePermission() {
        super("Biome", XMaterial.MAP, PermissionType.OPERATOR);
    }
}
