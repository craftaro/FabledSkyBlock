package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SignEditPermission extends ListeningPermission {

    private final SkyBlock plugin;

    public SignEditPermission(SkyBlock plugin) {
        super("EditSign", XMaterial.OAK_SIGN, PermissionType.GENERIC, new HashMap<IslandRole, Boolean>() {{
            put(IslandRole.VISITOR, false);
            put(IslandRole.MEMBER, true);
            put(IslandRole.OPERATOR, true);
            put(IslandRole.COOP, true);
            put(IslandRole.OWNER, true);
        }});
        this.plugin = plugin;
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getClickedBlock().getType().name().contains("SIGN")) {
            cancelAndMessage(event, player, this.plugin, this.plugin.getMessageManager());
        }
    }
}
