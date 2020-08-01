package com.songoda.skyblock.gui.coop;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiType;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class GuiCoopChoose extends Gui {
    
    private final SkyBlock plugin;
    private final FileConfiguration languageLoad;
    private final Gui returnGui;
    private final String targetPlayer;
    private final Island island;
    
    public GuiCoopChoose(SkyBlock plugin, Island island, Gui returnGui, String targetPlayer) {
        super(1, returnGui);
        this.plugin = plugin;
        this.returnGui = returnGui;
        this.targetPlayer = targetPlayer;
        this.island = island;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setTitle(TextUtils.formatText(
                languageLoad.getString("Menu.Coop.Item.Word.Normal") + " / " + languageLoad.getString("Menu.Coop.Item.Word.Temp")));
        paint();
    }
    
    public void paint() {
        SoundManager soundManager = plugin.getSoundManager();
        
        if (inventory != null)
            inventory.clear();
        
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setActionForRange(0, 4, null);
    
        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            guiManager.showGUI(event.player, returnGui);
        });
        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            guiManager.showGUI(event.player, returnGui);
        });
    
        setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.OBSIDIAN, // Normal
                TextUtils.formatText("&r" + languageLoad.getString("Menu.Coop.Item.Word.Normal"))),
                (event) ->  {
            Bukkit.getServer().dispatchCommand(event.player,
                        "island coop " + targetPlayer + " " + languageLoad.getString("Menu.Coop.Item.Word.Normal"));
                    guiManager.showGUI(event.player, new GuiCoop(plugin, island, null));
        });
        setButton(5, GuiUtils.createButtonItem(CompatibleMaterial.GLASS, // Temp
                TextUtils.formatText("&r" + languageLoad.getString("Menu.Coop.Item.Word.Temp"))),
                (event) -> {
            Bukkit.getServer().dispatchCommand(event.player,
                    "island coop " + targetPlayer + " " + languageLoad.getString("Menu.Coop.Item.Word.Temp"));
                    guiManager.showGUI(event.player, new GuiCoop(plugin, island, null));
        });
    }
}
