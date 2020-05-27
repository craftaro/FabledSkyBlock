package com.songoda.skyblock.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignatureEditor extends Gui {

    private final SkyBlock plugin;
    private final FileConfiguration configLoad;
    private final Gui returnGui;
    private final Island island;
    private final FileManager.Config mainConfig;

    public SignatureEditor(SkyBlock plugin, Gui returnGui, Island island) {
        super(1);
        this.plugin = plugin;
        this.returnGui = returnGui;
        this.island = island;
        this.configLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.mainConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        setDefaultItem(null);
        setTitle(TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")));
        paint();
    }

    public void paint() {
        List<String> signatureMessage = island.getMessage(IslandMessage.Signature);
        setButton(2, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"))),
                (event) -> guiManager.showGUI(event.player, returnGui));
        setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"))),
                (event) -> guiManager.showGUI(event.player, returnGui));

        setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname")),
                TextUtils.formatText(configLoad.getStringList(
                        signatureMessage.size() == mainConfig.getFileConfiguration().getInt("Island.Visitor.Signature.Lines")
                                ? "Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Limit.Lore"
                                : "Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.More.Lore"))),
                (event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setAction((e -> {
                        signatureMessage.add(gui.getInputText().trim());
                        island.setMessage(IslandMessage.Signature, e.player.getName(), signatureMessage);
                        CompatibleSound.BLOCK_NOTE_BLOCK_PLING.play(e.player);
                        e.player.closeInventory();
                        paint();
                    }));
                    gui.setTitle(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Word.Enter"));
                    guiManager.showGUI(event.player, gui);
                }));

        List<String> itemLore = new ArrayList<>();
        itemLore.add(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Word.Empty"));
        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SIGN,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname")),
                TextUtils.formatText(signatureMessage.size() == 0 ? itemLore : signatureMessage)));

        setButton(5, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname")),
                TextUtils.formatText(configLoad.getStringList(
                        signatureMessage.size() == 0
                                ? "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.None.Lore"
                                : "Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Lines.Lore"))),
                (event -> {
                    signatureMessage.remove(signatureMessage.size() - 1);
                    island.setMessage(IslandMessage.Signature, event.player.getName(), signatureMessage);
                    paint();
                }));
    }
}
