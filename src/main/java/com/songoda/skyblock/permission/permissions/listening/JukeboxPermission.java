package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class JukeboxPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public JukeboxPermission(SkyBlock plugin) {
        super("Jukebox", XMaterial.JUKEBOX, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Optional<XMaterial> material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType());
        Player player = event.getPlayer();

        if (material.orElse(null) == XMaterial.JUKEBOX) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
