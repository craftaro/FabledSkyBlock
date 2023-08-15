package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class VisitorPermission extends BasicPermission {
    public VisitorPermission() {
        super("Visitor", XMaterial.OAK_SIGN, PermissionType.OPERATOR);
    }
}
