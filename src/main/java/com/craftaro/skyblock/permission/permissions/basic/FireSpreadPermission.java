package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class FireSpreadPermission extends BasicPermission {
    public FireSpreadPermission() {
        super("FireSpread", XMaterial.FLINT_AND_STEEL, PermissionType.ISLAND);
    }
}
