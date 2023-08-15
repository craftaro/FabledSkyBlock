package com.craftaro.skyblock.gui.permissions;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.gui.GuiSignatureEditor;
import com.craftaro.skyblock.gui.GuiWelcomeEditor;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandPermission;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.permission.PermissionType;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.visit.Visit;
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
    private final Player player;
    private final FileConfiguration languageLoad;
    private final FileConfiguration configLoad;
    private final Gui returnGui;

    public GuiPermissions(SkyBlock plugin, Player player, Island island, IslandRole role, Gui returnGui) {
        super(6, returnGui);
        this.plugin = plugin;
        this.player = player;
        this.permissionManager = plugin.getPermissionManager();
        this.soundManager = plugin.getSoundManager();
        this.role = role;
        this.island = island;
        this.returnGui = returnGui;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.configLoad = this.plugin.getConfiguration();
        setTitle(TextUtils.formatText(this.languageLoad.getString("Menu.Settings." + role.name() + ".Title")));
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (this.inventory != null) {
            this.inventory.clear();
        }
        setActionForRange(0, 0, 5, 9, null);

        setButton(0, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            this.guiManager.showGUI(event.player, this.returnGui);
        });

        if (this.role == IslandRole.VISITOR) {
            if (this.configLoad.getBoolean("Island.Visitor.Welcome.Enable")) {
                setButton(5, GuiUtils.createButtonItem(XMaterial.MAP,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname")),
                                TextUtils.formatText(this.languageLoad.getStringList("Menu.Settings.Visitor.Item.Welcome.Lore"))),
                        (event) -> this.guiManager.showGUI(event.player, new GuiWelcomeEditor(this.plugin, this, this.island)));
            }

            if (this.configLoad.getBoolean("Island.Visitor.Signature.Enable")) {
                setButton(3, GuiUtils.createButtonItem(XMaterial.PAPER,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname")),
                                TextUtils.formatText(this.languageLoad.getStringList("Menu.Settings.Visitor.Item.Signature.Lore"))),
                        (event) -> this.guiManager.showGUI(event.player, new GuiSignatureEditor(this.plugin, this, this.island)));
            }

            Visit visit = this.island.getVisit();
            String configAddress = "";
            switch (this.island.getStatus()) {
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
            List<String> welcomeLore = TextUtils.formatText(this.languageLoad.getStringList(configAddress));

            List<String> welcomeFinal = new ArrayList<>();

            for (String line : welcomeLore) {
                welcomeFinal.add(line.replace("%visits", String.valueOf(visit.getVisitors().size()))
                        .replace("%votes", String.valueOf(visit.getVoters().size()))
                        .replace("%visitors", String.valueOf(this.plugin.getIslandManager().getVisitorsAtIsland(this.island).size())));
            }

            setButton(4, GuiUtils.createButtonItem(XMaterial.PAINTING,
                            TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname")),
                            welcomeFinal),
                    (event -> {
                        switch (this.island.getStatus()) {
                            case OPEN:
                                this.plugin.getIslandManager().whitelistIsland(this.island);
                                this.soundManager.playSound(event.player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                break;
                            case CLOSED:
                                this.plugin.getIslandManager().closeIsland(this.island);
                                this.soundManager.playSound(event.player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
                                break;
                            case WHITELISTED:
                                this.island.setStatus(IslandStatus.OPEN);
                                this.soundManager.playSound(event.player, XSound.BLOCK_WOODEN_DOOR_OPEN);
                                break;
                        }
                        paint();
                    }));
        }

        setButton(8, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            this.guiManager.showGUI(event.player, this.returnGui);
        });

        List<BasicPermission> permissions = this.permissionManager.getPermissions().stream()
                .filter(p -> p.getType() == getType(this.role))
                .collect(Collectors.toList());

        if (this.configLoad.getBoolean("Island.Settings.Permission")) {
            permissions.removeIf(permission -> !this.player.hasPermission("fabledskyblock.settings." +
                    this.role.name().toLowerCase() + "." + permission.getName().toLowerCase()));
        }

        double itemCount = permissions.size();
        this.pages = (int) Math.max(1, Math.ceil(itemCount / 36));

        if (this.page != 1) {
            setButton(5, 2, GuiUtils.createButtonItem(XMaterial.ARROW,
                            TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Categories.Item.Last.Displayname"))),
                    (event) -> {
                        this.page--;
                        paint();
                    });
        }

        if (this.page != this.pages) {
            setButton(5, 6, GuiUtils.createButtonItem(XMaterial.ARROW,
                            TextUtils.formatText(this.languageLoad.getString("Menu.Settings.Categories.Item.Next.Displayname"))),
                    (event) -> {
                        this.page++;
                        paint();
                    });
        }

        if (!permissions.isEmpty()) {
            for (int i = 9; i < 45; i++) {
                int current = ((this.page - 1) * 36) - 9;
                if (current + i >= permissions.size()) {
                    setItem(i, null);
                    continue;
                }
                BasicPermission permission = permissions.get(current + i);
                if (permission == null) {
                    continue;
                }

                setButton(i, permission.getItem(this.island, this.role), (event) -> {
                    if (!hasPermission(this.island, event.player, this.role)) {
                        this.plugin.getMessageManager().sendMessage(event.player, this.languageLoad
                                .getString("Command.Island.Settings.Permission.Change.Message"));
                        this.soundManager.playSound(event.player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }
                    IslandPermission islandPermission = this.island.getPermission(this.role, permission);
                    islandPermission.setStatus(!islandPermission.getStatus());
                    paint();
                });
            }
        } else {
            setItem(31, XMaterial.BARRIER.parseItem()); // TODO
        }
    }

    private boolean hasPermission(Island island, Player player, IslandRole role) {
        PermissionManager permissionManager = SkyBlock.getInstance().getPermissionManager();
        if (role == IslandRole.VISITOR || role == IslandRole.MEMBER || role == IslandRole.COOP
                || role == IslandRole.OWNER) {
            String roleName = role.name();

            if (role == IslandRole.OWNER) {
                roleName = "Island";
            }

            return !island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    || permissionManager.hasPermission(island, roleName, IslandRole.OPERATOR);
        } else if (role == IslandRole.OPERATOR) {
            return island.hasRole(IslandRole.OWNER, player.getUniqueId());
        }

        return true;
    }

    public PermissionType getType(IslandRole role) {
        switch (role) {
            default:
            case VISITOR:
            case MEMBER:
            case COOP:
                return PermissionType.GENERIC;
            case OPERATOR:
                return PermissionType.OPERATOR;
            case OWNER:
                return PermissionType.ISLAND;
        }
    }
}
