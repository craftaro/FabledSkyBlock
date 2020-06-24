package com.songoda.skyblock.gui.permissions;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class GuiPermissionsSelector extends Gui {

    public GuiPermissionsSelector(@Nonnull SkyBlock plugin, @Nullable Island island, @Nullable Gui returnGui) {
        super(1, returnGui);
        setDefaultItem(null);

        String admin = island == null ? "Admin." : "";

        FileConfiguration configLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();

        setTitle(ChatColor.translateAlternateColorCodes('&',
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Title"))));

        setButton(2, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SIGN,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Visitor.Displayname")),
                TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Visitor.Lore"))), (event) ->
                guiManager.showGUI(event.player, island == null ?
                        new GuiAdminPermissions(plugin, IslandRole.Visitor, this) :
                        new GuiPermissions(plugin, island, IslandRole.Visitor, this)));

        setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Member.Displayname")),
                TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Member.Lore"))), (event) ->
                guiManager.showGUI(event.player, island == null ?
                        new GuiAdminPermissions(plugin, IslandRole.Member, this) :
                        new GuiPermissions(plugin, island, IslandRole.Member, this)));

        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.ITEM_FRAME,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Operator.Displayname")),
                TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Operator.Lore"))), (event) ->
                guiManager.showGUI(event.player, island == null ?
                    new GuiAdminPermissions(plugin, IslandRole.Operator, this) :
                    new GuiPermissions(plugin, island, IslandRole.Operator, this)));

        boolean isCoop = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Coop.Enable");

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            plugin.getSoundManager().playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        if (isCoop)
            setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.NAME_TAG,
                    TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Coop.Displayname")),
                            TextUtils.formatText(configLoad.getStringList("Menu.Settings.Categories.Item.Coop.Lore"))), (event) ->
                            guiManager.showGUI(event.player, island == null ?
                                    new GuiAdminPermissions(plugin, IslandRole.Coop, this) :
                                    new GuiPermissions(plugin, island, IslandRole.Coop, this)));

        setButton(isCoop ? 7 : 8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SAPLING.getItem(),
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Owner.Displayname")),
                        TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Owner.Lore"))), (event) ->
                        guiManager.showGUI(event.player, island == null ?
                                new GuiAdminPermissions(plugin, IslandRole.Owner, this) :
                                new GuiPermissions(plugin, island, IslandRole.Owner, this)));

    }
}
