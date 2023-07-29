package com.songoda.skyblock.permission.permissions.basic;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;

public class WeatherPermission extends BasicPermission {
    public WeatherPermission() {
        super("Weather", CompatibleMaterial.CLOCK, PermissionType.OPERATOR);
    }
}
