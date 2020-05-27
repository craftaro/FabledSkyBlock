package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class NaturalMobSpawningPermission extends BasicPermission {

    public NaturalMobSpawningPermission() {
        super("NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG, PermissionType.ISLAND);
    }

}
