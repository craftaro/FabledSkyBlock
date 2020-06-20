package com.songoda.skyblock.gui.bank;

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
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiBank extends Gui {
    private final SkyBlock plugin;
    private final PermissionManager permissionManager;
    private final Island island;
    private final FileConfiguration languageLoad;
    private final FileManager.Config config;
    private final Gui returnGui;

    public GuiBank(SkyBlock plugin, Island island, Gui returnGui) {
        super(6, returnGui);
        this.plugin = plugin;
        this.permissionManager = plugin.getPermissionManager();
        this.island = island;
        this.returnGui = returnGui;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Settings.Title"))); // TODO Title
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            guiManager.showGUI(event.player, returnGui);
        });

        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.BOOK, // Transaction log
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            guiManager.showGUI(event.player, new GuiBankTransaction(plugin, island, this));
        });

        setButton(10, GuiUtils.createButtonItem(CompatibleMaterial.RED_DYE, // Deposit
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            guiManager.showGUI(event.player, returnGui);
        });

        setItem(13, GuiUtils.createButtonItem(CompatibleMaterial.GOLD_INGOT, // Balance
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))));

        setButton(16, GuiUtils.createButtonItem(CompatibleMaterial.GREEN_DYE, // WithDraw
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            guiManager.showGUI(event.player, returnGui);
        });
    }
}
