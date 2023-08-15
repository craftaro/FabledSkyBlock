package com.craftaro.skyblock.gui.bank;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.sound.SoundManager;
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
            setDefaultItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
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

        setDefaultItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        setButton(4, GuiUtils.createButtonItem(XMaterial.BOOK, // Transaction log
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Log.Displayname"))), (event) ->
                this.guiManager.showGUI(event.player, new GuiBankTransaction(this.plugin, this.island, this, this.admin)));

        setButton(10, GuiUtils.createButtonItem(XMaterial.GREEN_DYE, // Deposit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Deposit.Displayname"))), (event) ->
                this.guiManager.showGUI(event.player, new GuiBankSelector(this.plugin, this.island, this, GuiBankSelector.Type.DEPOSIT, this.admin)));

        setItem(13, GuiUtils.createButtonItem(XMaterial.GOLD_INGOT, // Balance
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Balance.Displayname")),
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Balance.Lore")
                        .replace("%balance", String.valueOf(this.island.getBankBalance())))));

        setButton(16, GuiUtils.createButtonItem(XMaterial.RED_DYE, // Withdraw
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Withdraw.Displayname"))), (event) ->
                this.guiManager.showGUI(event.player, new GuiBankSelector(this.plugin, this.island, this, GuiBankSelector.Type.WITHDRAW, this.admin)));
    }
}
