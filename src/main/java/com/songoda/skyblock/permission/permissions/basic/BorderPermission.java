package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class BorderPermission extends BasicPermission {
    public BorderPermission() {
        super("Border", CompatibleMaterial.BEACON, PermissionType.OPERATOR);
    }
}
