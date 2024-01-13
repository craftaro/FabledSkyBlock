package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class LeafDecayPermission extends BasicPermission {
    public LeafDecayPermission() {
        super("LeafDecay", XMaterial.OAK_LEAVES, PermissionType.ISLAND);
    }
}
