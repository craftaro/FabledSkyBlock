package com.songoda.skyblock.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.ban.BanManager;
import com.songoda.skyblock.island.Island;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class GuiBans extends Gui {
    private final SkyBlock plugin;
    private final BanManager banManager;
    private final Island island;
    private final FileConfiguration languageLoad;
    private final boolean admin;

    public GuiBans(SkyBlock plugin, Island island, Gui returnGui, boolean admin) {
        super(returnGui);
        this.plugin = plugin;;
        this.banManager = plugin.getBanManager();
        this.island = island;
        this.admin = admin;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setTitle(TextUtils.formatText("Bans"));
        paint();
    }

    public void paint() {
        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            event.player.closeInventory();
        });

        for(int i=9; i<18; i++){
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }


    }
}
