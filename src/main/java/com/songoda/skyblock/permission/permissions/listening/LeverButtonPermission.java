package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LeverButtonPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public LeverButtonPermission(SkyBlock plugin) {
        super("LeverButton", CompatibleMaterial.LEVER, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {


        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        Player player = event.getPlayer();

        if (material == CompatibleMaterial.STONE_BUTTON || material == CompatibleMaterial.OAK_BUTTON
                || material == CompatibleMaterial.SPRUCE_BUTTON || material == CompatibleMaterial.BIRCH_BUTTON
                || material == CompatibleMaterial.JUNGLE_BUTTON || material == CompatibleMaterial.ACACIA_BUTTON
                || material == CompatibleMaterial.DARK_OAK_BUTTON || material == CompatibleMaterial.LEVER)
            cancelAndMessage(event, player, plugin, messageManager);
    }
}
