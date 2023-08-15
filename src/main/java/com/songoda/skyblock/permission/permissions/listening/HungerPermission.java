package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
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
