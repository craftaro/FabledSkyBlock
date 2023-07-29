package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class UnbanPermission extends BasicPermission {
    public UnbanPermission() {
        super("Unban", CompatibleMaterial.RED_DYE, PermissionType.OPERATOR);
    }
}
