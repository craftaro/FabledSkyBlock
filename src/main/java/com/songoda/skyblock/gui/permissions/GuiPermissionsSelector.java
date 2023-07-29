package com.songoda.skyblock.gui.permissions;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class GuiPermissionsSelector extends Gui {
    public GuiPermissionsSelector(@Nonnull SkyBlock plugin, @Nullable Player player, @Nullable Island island, @Nullable Gui returnGui) {
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
                this.guiManager.showGUI(event.player, island == null ?
                        new GuiAdminPermissions(plugin, IslandRole.VISITOR, this) :
                        new GuiPermissions(plugin, player, island, IslandRole.VISITOR, this)));

        setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Member.Displayname")),
                TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Member.Lore"))), (event) ->
                this.guiManager.showGUI(event.player, island == null ?
                        new GuiAdminPermissions(plugin, IslandRole.MEMBER, this) :
                        new GuiPermissions(plugin, player, island, IslandRole.MEMBER, this)));

        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.ITEM_FRAME,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Operator.Displayname")),
                TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Operator.Lore"))), (event) ->
                this.guiManager.showGUI(event.player, island == null ?
                        new GuiAdminPermissions(plugin, IslandRole.OPERATOR, this) :
                        new GuiPermissions(plugin, player, island, IslandRole.OPERATOR, this)));

        boolean isCoop = plugin.getConfiguration()
                .getBoolean("Island.Coop.Enable");

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            plugin.getSoundManager().playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        if (isCoop) {
            setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.NAME_TAG,
                    TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Coop.Displayname")),
                    TextUtils.formatText(configLoad.getStringList("Menu.Settings.Categories.Item.Coop.Lore"))), (event) ->
                    this.guiManager.showGUI(event.player, island == null ?
                            new GuiAdminPermissions(plugin, IslandRole.COOP, this) :
                            new GuiPermissions(plugin, player, island, IslandRole.COOP, this)));
        }

        setButton(isCoop ? 7 : 8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SAPLING.getItem(),
                TextUtils.formatText(configLoad.getString("Menu." + admin + "Settings.Categories.Item.Owner.Displayname")),
                TextUtils.formatText(configLoad.getStringList("Menu." + admin + "Settings.Categories.Item.Owner.Lore"))), (event) ->
                this.guiManager.showGUI(event.player, island == null ?
                        new GuiAdminPermissions(plugin, IslandRole.OWNER, this) :
                        new GuiPermissions(plugin, player, island, IslandRole.OWNER, this)));
    }
}
