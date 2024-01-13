package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class VisitorPermission extends BasicPermission {
    public VisitorPermission() {
        super("Visitor", XMaterial.OAK_SIGN, PermissionType.OPERATOR);
    }
}
