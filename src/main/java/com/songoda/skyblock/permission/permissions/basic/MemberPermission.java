package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class MemberPermission extends BasicPermission {
    public MemberPermission() {
        super("Member", CompatibleMaterial.PAINTING, PermissionType.OPERATOR);
    }
}
