package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class KickPermission extends BasicPermission {

    public KickPermission() {
        super("Visitor", CompatibleMaterial.IRON_DOOR, PermissionType.OPERATOR);
    }

}
