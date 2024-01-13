package com.craftaro.skyblock.gui.permissions;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.permission.BasicPermission;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.permission.PermissionType;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GuiAdminPermissions extends Gui {
    private final PermissionManager permissionManager;
    private final SoundManager soundManager;
    private final IslandRole role;
    private final FileConfiguration configLoad;
    private final FileManager.Config settingsConfig;
    private final FileConfiguration settingsConfigLoad;
    private final Gui returnGui;

    public GuiAdminPermissions(SkyBlock plugin, IslandRole role, Gui returnGui) {
        super(6, returnGui);
        this.permissionManager = plugin.getPermissionManager();
        this.soundManager = plugin.getSoundManager();
        this.role = role;
        this.returnGui = returnGui;
        this.configLoad = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.settingsConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "settings.yml"));
        this.settingsConfigLoad = this.settingsConfig.getFileConfiguration();
        setTitle(TextUtils.formatText(this.configLoad.getString("Menu.Settings." + role.getFriendlyName() + ".Title")));
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (this.inventory != null) {
            this.inventory.clear();
        }
        setActionForRange(0, 0, 5, 9, null);

        setButton(0, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(this.configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            this.guiManager.showGUI(event.player, this.returnGui);
        });

        setButton(8, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(this.configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            this.guiManager.showGUI(event.player, this.returnGui);
        });

        List<BasicPermission> permissions = this.permissionManager.getPermissions().stream()
                .filter(p -> p.getType() == getType(this.role))
                .collect(Collectors.toList());
        double itemCount = permissions.size();
        this.pages = (int) Math.max(1, Math.ceil(itemCount / 36));

        if (this.page != 1) {
            setButton(5, 2, GuiUtils.createButtonItem(XMaterial.ARROW,
                            TextUtils.formatText(this.configLoad.getString("Menu.Settings.Categories.Item.Last.Displayname"))),
                    (event) -> {
                        this.page--;
                        paint();
                    });
        }

        if (this.page != this.pages) {
            setButton(5, 6, GuiUtils.createButtonItem(XMaterial.ARROW,
                            TextUtils.formatText(this.configLoad.getString("Menu.Settings.Categories.Item.Next.Displayname"))),
                    (event) -> {
                        this.page++;
                        paint();
                    });
        }

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

            final String path = "Settings." + this.role.getFriendlyName() + "." + permission.getName();
            boolean setting = this.settingsConfigLoad.getBoolean(path);
            setButton(i, permission.getItem(setting, this.role), (event) -> {
                this.settingsConfigLoad.set(path, !setting);
                try {
                    this.settingsConfigLoad.save(this.settingsConfig.getFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                paint();
            });
        }
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
