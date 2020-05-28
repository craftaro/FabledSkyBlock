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
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandMessage;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiWelcomeEditor extends Gui {

    private final SkyBlock plugin;
    private final FileConfiguration configLoad;
    private final Gui returnGui;
    private final Island island;
    private final FileManager.Config mainConfig;
    private final MessageManager messageManager;
    private final IslandManager islandManager;

    public GuiWelcomeEditor(SkyBlock plugin, Gui returnGui, Island island) {
        super(1);
        this.plugin = plugin;
        this.returnGui = returnGui;
        this.island = island;
        this.configLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.mainConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        this.messageManager = plugin.getMessageManager();
        this.islandManager = plugin.getIslandManager();
        setDefaultItem(null);
        setTitle(TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title")));
        paint();
    }

    public void paint() {
        List<String> welcomeMessage = island.getMessage(IslandMessage.Welcome);
        setButton(2, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))),
                (event) -> guiManager.showGUI(event.player, returnGui));
        setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))),
                (event) -> guiManager.showGUI(event.player, returnGui));

        setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname")),
                TextUtils.formatText(configLoad.getStringList(
                        welcomeMessage.size() == mainConfig.getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")
                                ? "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Limit.Lore"
                                : "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.More.Lore"))),
                (event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setAction((e -> {
                        if (!hasPermission(e.player))
                            return;
                        if (island.getMessage(IslandMessage.Welcome)
                                .size() > mainConfig.getFileConfiguration().getInt(
                                "Island.Visitor.Welcome.Lines")
                                || gui.getInputText().length() > mainConfig.getFileConfiguration()
                                .getInt("Island.Visitor.Welcome.Length")) {
                            CompatibleSound.BLOCK_ANVIL_LAND.play(e.player);
                        } else {
                            welcomeMessage.add(gui.getInputText().trim());
                            island.setMessage(IslandMessage.Welcome, e.player.getName(), welcomeMessage);
                            CompatibleSound.BLOCK_NOTE_BLOCK_PLING.play(e.player);
                        }
                        e.player.closeInventory();
                        paint();
                    }));
                    gui.setTitle(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Word.Enter"));
                    guiManager.showGUI(event.player, gui);
                }));

        List<String> itemLore = new ArrayList<>();
        itemLore.add(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Word.Empty"));
        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SIGN,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname")),
                TextUtils.formatText(welcomeMessage.size() == 0 ? itemLore : welcomeMessage)));

        setButton(5, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                TextUtils.formatText(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname")),
                TextUtils.formatText(configLoad.getStringList(
                        welcomeMessage.size() == 0
                                ? "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.None.Lore"
                                : "Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Lines.Lore"))),
                (event -> {
                    welcomeMessage.remove(welcomeMessage.size() - 1);
                    island.setMessage(IslandMessage.Welcome, event.player.getName(), welcomeMessage);
                    paint();
                }));
    }

    private boolean hasPermission(Player player) {
        Island island1 = islandManager.getIsland(player);

        if (island1 == null) {
            messageManager.sendMessage(player,
                    configLoad.getString(
                            "Command.Island.Settings.Owner.Message"));
            CompatibleSound.BLOCK_ANVIL_LAND.play(player);
            player.closeInventory();
            return false;
        } else if (!(island1.hasRole(IslandRole.Operator,
                player.getUniqueId())
                || island1.hasRole(IslandRole.Owner,
                player.getUniqueId()))) {
            messageManager.sendMessage(player, configLoad
                    .getString("Command.Island.Role.Message"));
            CompatibleSound.BLOCK_ANVIL_LAND.play(player);
            player.closeInventory();
            return false;
        } else if (!plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(),
                        "config.yml"))
                .getFileConfiguration().getBoolean(
                        "Island.Visitor.Welcome.Enable")) {
            messageManager.sendMessage(player,
                    configLoad.getString(
                            "Island.Settings.Visitor.Welcome.Disabled.Message"));
            CompatibleSound.BLOCK_ANVIL_LAND.play(player);
            return false;
        }
        return true;
    }
}
