package com.songoda.skyblock.gui.permissions;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.gui.GuiSignatureEditor;
import com.songoda.skyblock.gui.GuiWelcomeEditor;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandPermission;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiPermissions extends Gui {

    private final SkyBlock plugin;
    private final PermissionManager permissionManager;
    private final SoundManager soundManager;
    private final IslandRole role;
    private final Island island;
    private final FileConfiguration configLoad;
    private final FileManager.Config config;
    private final Gui returnGui;

    public GuiPermissions(SkyBlock plugin, Island island, IslandRole role, Gui returnGui) {
        super(6, returnGui);
        this.plugin = plugin;
        this.permissionManager = plugin.getPermissionManager();
        this.soundManager = plugin.getSoundManager();
        this.role = role;
        this.island = island;
        this.returnGui = returnGui;
        this.configLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        setTitle(TextUtils.formatText(configLoad.getString("Menu.Settings." + role.name() + ".Title")));
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 5, 9, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            guiManager.showGUI(event.player, returnGui);
        });

        if (role == IslandRole.Visitor) {
            if (config.getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable"))
                setButton(5, GuiUtils.createButtonItem(CompatibleMaterial.MAP,
                        TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname")),
                        TextUtils.formatText(configLoad.getStringList("Menu.Settings.Visitor.Item.Welcome.Lore"))),
                        (event) -> guiManager.showGUI(event.player, new GuiWelcomeEditor(plugin, this, island)));

            if (config.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.PAPER,
                        TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname")),
                        TextUtils.formatText(configLoad.getStringList("Menu.Settings.Visitor.Item.Signature.Lore"))),
                        (event) -> guiManager.showGUI(event.player, new GuiSignatureEditor(plugin, this, island)));
            }

            Visit visit = island.getVisit();
            String configAddress = "";
            switch (island.getStatus()) {
                case OPEN:
                    configAddress = "Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Open.Lore";
                    break;
                case CLOSED:
                    configAddress = "Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Closed.Lore";
                    break;
                case WHITELISTED:
                    configAddress = "Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Whitelisted.Lore";
                    break;
            }
            List<String> welcomeLore = TextUtils.formatText(configLoad.getStringList(configAddress));

            List<String> welcomeFinal = new ArrayList<>();

            for (String line : welcomeLore) {
                welcomeFinal.add(line.replace("%visits", String.valueOf(visit.getVisitors().size()))
                        .replace("%votes", String.valueOf(visit.getVoters().size()))
                        .replace("%visitors", String.valueOf(plugin.getIslandManager().getVisitorsAtIsland(island).size())));
            }

            setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                    TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname")),
                    welcomeFinal),
                    (event -> {
                        switch (island.getStatus()) {
                            case OPEN:
                                plugin.getIslandManager().whitelistIsland(island);
                                soundManager.playSound(event.player, CompatibleSound.BLOCK_WOODEN_DOOR_CLOSE.getSound(), 1f, 1f);
                                break;
                            case CLOSED:
                                plugin.getIslandManager().closeIsland(island);
                                soundManager.playSound(event.player, CompatibleSound.BLOCK_WOODEN_DOOR_CLOSE.getSound(), 1f, 1f);
                                break;
                            case WHITELISTED:
                                island.setStatus(IslandStatus.OPEN);
                                soundManager.playSound(event.player, CompatibleSound.BLOCK_WOODEN_DOOR_OPEN.getSound(), 1f, 1f);
                                break;
                        }
                        paint();
                    }));
        }

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            guiManager.showGUI(event.player, returnGui);
        });

        List<BasicPermission> permissions = permissionManager.getPermissions().stream()
                .filter(p -> p.getType() == getType(role))
                .collect(Collectors.toList());
        double itemCount = permissions.size();
        this.pages = (int) Math.max(1, Math.ceil(itemCount / 36));

        if (page != 1)
            setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                    TextUtils.formatText(configLoad.getString("Menu.Settings.Categories.Item.Last.Displayname"))),
                    (event) -> {
                        page--;
                        paint();
                    });

        if (page != pages)
            setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                    TextUtils.formatText(configLoad.getString("Menu.Settings.Categories.Item.Next.Displayname"))),
                    (event) -> {
                        page++;
                        paint();
                    });

        for (int i = 9; i < 45; i++) {
            int current = ((page - 1) * 36) - 9;
            if (current + i >= permissions.size()) {
                setItem(i, null);
                continue;
            }
            BasicPermission permission = permissions.get(current + i);
            if (permission == null) continue;

            setButton(i, permission.getItem(island, role), (event) -> {
                if (!hasPermission(island, event.player, role)) {
                    plugin.getMessageManager().sendMessage(event.player, configLoad
                            .getString("Command.Island.Settings.Permission.Change.Message"));
                    soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);

                    return;
                }
                IslandPermission islandPermission = island.getPermission(role, permission);
                islandPermission.setStatus(!islandPermission.getStatus());
                paint();
            });
        }
    }

    private boolean hasPermission(Island island, Player player, IslandRole role) {
        PermissionManager permissionManager = SkyBlock.getInstance().getPermissionManager();
        if (role == IslandRole.Visitor || role == IslandRole.Member || role == IslandRole.Coop
                || role == IslandRole.Owner) {
            String roleName = role.name();

            if (role == IslandRole.Owner) {
                roleName = "Island";
            }

            return !island.hasRole(IslandRole.Operator, player.getUniqueId())
                    || permissionManager.hasPermission(island, roleName, IslandRole.Operator);
        } else if (role == IslandRole.Operator) {
            return island.hasRole(IslandRole.Owner, player.getUniqueId());
        }

        return true;
    }

    public PermissionType getType(IslandRole role) {
        switch (role) {
            default:
            case Visitor:
            case Member:
            case Coop:
                return PermissionType.GENERIC;
            case Operator:
                return PermissionType.OPERATOR;
            case Owner:
                return PermissionType.ISLAND;
        }
    }
}
