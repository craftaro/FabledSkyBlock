package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class KeepItemsOnDeathPermission extends BasicPermission {
    public KeepItemsOnDeathPermission() {
        super("KeepItemsOnDeath", XMaterial.ITEM_FRAME, PermissionType.ISLAND);
    }
}
