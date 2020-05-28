package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class KeepItemsOnDeathPermission extends BasicPermission {

    public KeepItemsOnDeathPermission() {
        super("KeepItemsOnDeath", CompatibleMaterial.ITEM_FRAME, PermissionType.ISLAND);
    }

}
