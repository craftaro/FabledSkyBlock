package com.songoda.skyblock.gui.bank;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GuiBank extends Gui {
    private final SkyBlock plugin;
    private final SoundManager soundManager;
    private final Island island;
    private final FileConfiguration languageLoad;
    private final boolean admin;

    public GuiBank(SkyBlock plugin, Island island, Gui returnGui, boolean admin) {
        super(2, returnGui);
        this.plugin = plugin;
        this.soundManager = plugin.getSoundManager();
        this.island = island;
        this.admin = admin;
        this.languageLoad = this.plugin.getLanguage();
        if (island != null) {
            setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
            setTitle(TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Title")));
            paint();
        }
    }

    @Override
    public void onOpen(@Nonnull GuiManager manager, @Nonnull Player player) {
        updateItem(13, // Balance
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Balance.Displayname")),
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Balance.Lore")
                        .replace("%balance", String.valueOf(this.island.getBankBalance()))));
        super.onOpen(manager, player);
    }

    public void paint() {
        if (this.inventory != null) {
            this.inventory.clear();
        }

        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.BOOK, // Transaction log
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Log.Displayname"))), (event) ->
                this.guiManager.showGUI(event.player, new GuiBankTransaction(this.plugin, this.island, this, this.admin)));

        setButton(10, GuiUtils.createButtonItem(CompatibleMaterial.GREEN_DYE, // Deposit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Deposit.Displayname"))), (event) ->
                this.guiManager.showGUI(event.player, new GuiBankSelector(this.plugin, this.island, this, GuiBankSelector.Type.DEPOSIT, this.admin)));

        setItem(13, GuiUtils.createButtonItem(CompatibleMaterial.GOLD_INGOT, // Balance
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Balance.Displayname")),
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Balance.Lore")
                        .replace("%balance", String.valueOf(this.island.getBankBalance())))));

        setButton(16, GuiUtils.createButtonItem(CompatibleMaterial.RED_DYE, // Withdraw
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Withdraw.Displayname"))), (event) ->
                this.guiManager.showGUI(event.player, new GuiBankSelector(this.plugin, this.island, this, GuiBankSelector.Type.WITHDRAW, this.admin)));
    }
}
