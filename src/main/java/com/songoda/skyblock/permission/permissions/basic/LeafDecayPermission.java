package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class LeafDecayPermission extends BasicPermission {
    public LeafDecayPermission() {
        super("LeafDecay", CompatibleMaterial.OAK_LEAVES, PermissionType.ISLAND);
    }
}
