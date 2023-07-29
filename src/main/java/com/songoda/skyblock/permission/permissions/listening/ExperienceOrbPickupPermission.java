package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class ExperienceOrbPickupPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public ExperienceOrbPickupPermission(SkyBlock plugin) {
        super("ExperienceOrbPickup", CompatibleMaterial.EXPERIENCE_BOTTLE, PermissionType.GENERIC);
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
