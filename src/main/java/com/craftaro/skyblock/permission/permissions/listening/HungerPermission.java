package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerPermission extends ListeningPermission {
    public HungerPermission(SkyBlock plugin) {
        super("Hunger", XMaterial.COOKED_BEEF, PermissionType.GENERIC);
    }

    @PermissionHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
