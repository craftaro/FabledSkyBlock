package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.io.File;

public class HungerPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public HungerPermission(SkyBlock plugin) {
        super("Hunger", CompatibleMaterial.COOKED_BEEF, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean overridingCheck() {
        return plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable");
    }

    @PermissionHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
