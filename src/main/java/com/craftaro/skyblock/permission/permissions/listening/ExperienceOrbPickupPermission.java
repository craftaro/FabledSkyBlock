package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class ExperienceOrbPickupPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public ExperienceOrbPickupPermission(SkyBlock plugin) {
        super("ExperienceOrbPickup", XMaterial.EXPERIENCE_BOTTLE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onTargetEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb)) {
            return;
        }

        Player player = (Player) event.getTarget();
        cancelAndMessage(event, player, this.plugin, this.messageManager);
    }
}
