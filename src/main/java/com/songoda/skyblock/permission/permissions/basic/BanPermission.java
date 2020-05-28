package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class BanPermission extends BasicPermission {

    public BanPermission() {
        super("Ban", CompatibleMaterial.IRON_AXE, PermissionType.OPERATOR);
    }

}
