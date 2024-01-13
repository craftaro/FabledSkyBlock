package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class UnbanPermission extends BasicPermission {
    public UnbanPermission() {
        super("Unban", XMaterial.RED_DYE, PermissionType.OPERATOR);
    }
}
