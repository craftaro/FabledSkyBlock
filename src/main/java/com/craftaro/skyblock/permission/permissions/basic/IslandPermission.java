package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class IslandPermission extends BasicPermission {
    public IslandPermission() {
        super("Island", XMaterial.OAK_SAPLING, PermissionType.OPERATOR);
    }
}
