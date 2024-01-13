package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class CoopPlayersPermission extends BasicPermission {
    public CoopPlayersPermission() {
        super("CoopPlayers", XMaterial.BOOK, PermissionType.OPERATOR);
    }
}
