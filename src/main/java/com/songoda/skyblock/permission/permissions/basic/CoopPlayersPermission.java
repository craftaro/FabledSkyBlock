package com.songoda.skyblock.permission.permissions.basic;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class CoopPlayersPermission extends BasicPermission {
    public CoopPlayersPermission() {
        super("CoopPlayers", CompatibleMaterial.BOOK, PermissionType.OPERATOR);
    }
}
