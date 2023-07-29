package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class FireSpreadPermission extends BasicPermission {
    public FireSpreadPermission() {
        super("FireSpread", CompatibleMaterial.FLINT_AND_STEEL, PermissionType.ISLAND);
    }
}
