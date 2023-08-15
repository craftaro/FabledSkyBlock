package com.craftaro.skyblock.permission.permissions.basic;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionType;

public class WeatherPermission extends BasicPermission {
    public WeatherPermission() {
        super("Weather", XMaterial.CLOCK, PermissionType.OPERATOR);
    }
}
