package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class VisitorPermission extends BasicPermission {

    public VisitorPermission() {
        super("Visitor", CompatibleMaterial.OAK_SIGN, PermissionType.OPERATOR);
    }

}
