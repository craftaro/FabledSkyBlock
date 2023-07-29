package com.songoda.skyblock.gui.bank;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.economies.Economy;
import com.songoda.core.utils.NumberUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class GuiBankSelector extends Gui {
    private final SkyBlock plugin;
    private final BankManager bankManager;
    private final SoundManager soundManager;
    private final Island island;
    private final FileConfiguration languageLoad;
    private final Type type;
    private final Gui returnGui;
    private final boolean admin;

    public enum Type {
        DEPOSIT,
        WITHDRAW
    }

    public GuiBankSelector(SkyBlock plugin, Island island, Gui returnGui, Type type, boolean admin) {
        super(1, returnGui);
        this.plugin = plugin;
        this.type = type;
        this.bankManager = plugin.getBankManager();
        this.soundManager = plugin.getSoundManager();
        this.island = island;
        this.returnGui = returnGui;
        this.admin = admin;
        this.languageLoad = this.plugin.getLanguage();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setTitle(TextUtils.formatText(this.languageLoad.getString("Menu.Input.Title")));
        paint();
    }

    public void paint() {
        Economy economy = this.plugin.getEconomyManager().getEconomy();
        if (this.inventory != null) {
            this.inventory.clear();
        }

        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Input.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Input.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        String action;
        switch (this.type) {
            case DEPOSIT:
                action = "Deposit";
                break;
            case WITHDRAW:
                action = "Withdraw";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.type);
        }
        final String finalAction = action;

        setButton(2, GuiUtils.createButtonItem(CompatibleMaterial.GOLD_BLOCK, // All
                TextUtils.formatText(this.languageLoad.getString("Menu.Input.Item.All.Displayname")),
                TextUtils.formatText(this.languageLoad.getString("Menu.Input.Item.All.Lore")
                        .replace("%action%", this.languageLoad.getString("Menu.Bank.Words." + action)))), (event -> {
            MessageManager messageManager = this.plugin.getMessageManager();
            BankManager.BankResponse response;
            double amount;

            switch (this.type) {
                case DEPOSIT:
                    amount = economy.getBalance(event.player);
                    if (!this.plugin.getConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
                        amount = Math.floor(amount);
                    }
                    response = this.bankManager.deposit(event.player, this.island, amount, this.admin);
                    break;
                case WITHDRAW:
                    amount = this.island.getBankBalance();
                    if (!this.plugin.getConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
                        amount = Math.floor(amount);
                    }
                    response = this.bankManager.withdraw(event.player, this.island, amount, this.admin);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + this.type);
            }

            switch (response) {
                case NOT_ENOUGH_MONEY:
                    messageManager.sendMessage(event.player, this.languageLoad.getString("Command.Island.Bank.Short2.Message"));
                    this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                    break;
                case DECIMALS_NOT_ALLOWED:
                    messageManager.sendMessage(event.player, this.languageLoad.getString("Command.Island.Bank.Short6.Message"));
                    this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                    break;
                case NEGATIVE_AMOUNT:
                    messageManager.sendMessage(event.player, this.languageLoad.getString("Command.Island.Bank.Short5.Message"));
                    this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                    break;
                case SUCCESS:
                    this.soundManager.playSound(event.player, CompatibleSound.ENTITY_EXPERIENCE_ORB_PICKUP.getSound(), 1f, 1f);
                    messageManager.sendMessage(event.player, Objects.requireNonNull(this.languageLoad.getString("Command.Island.Bank." + finalAction + ".Message")).replace(
                            "%amount%", NumberUtils.formatNumber(amount)));

                    break;
            }
            this.guiManager.showGUI(event.player, this.returnGui);
        }));

        setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.PAPER, // Custom
                TextUtils.formatText(this.languageLoad.getString("Menu.Input.Item.Custom.Displayname")),
                TextUtils.formatText(this.languageLoad.getString("Menu.Input.Item.Custom.Lore")
                        .replace("%action%", this.languageLoad.getString("Menu.Bank.Words." + action)))), (event) -> {
            AnvilGui gui = new AnvilGui(event.player, this.returnGui);
            gui.setAction((e -> {
                MessageManager messageManager = this.plugin.getMessageManager();

                double amount;
                try {
                    if (gui.getInputText() != null) {
                        amount = Double.parseDouble(gui.getInputText().trim());
                    } else {
                        amount = 0;
                    }
                } catch (NumberFormatException e1) {
                    messageManager.sendMessage(e.player, this.languageLoad.getString("Command.Island.Bank.Short4.Message"));
                    this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                    return;
                }

                BankManager.BankResponse response;

                switch (this.type) {
                    case DEPOSIT:
                        response = this.bankManager.deposit(event.player, this.island, amount, this.admin);
                        break;
                    case WITHDRAW:
                        response = this.bankManager.withdraw(event.player, this.island, amount, this.admin);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + this.type);
                }

                switch (response) {
                    case NOT_ENOUGH_MONEY:
                        messageManager.sendMessage(e.player, this.languageLoad.getString("Command.Island.Bank.Short2.Message"));
                        this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                        break;
                    case DECIMALS_NOT_ALLOWED:
                        messageManager.sendMessage(e.player, this.languageLoad.getString("Command.Island.Bank.Short6.Message"));
                        this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                        break;
                    case NEGATIVE_AMOUNT:
                        messageManager.sendMessage(e.player, this.languageLoad.getString("Command.Island.Bank.Short5.Message"));
                        this.soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                        break;
                    case SUCCESS:
                        this.soundManager.playSound(event.player, CompatibleSound.ENTITY_EXPERIENCE_ORB_PICKUP.getSound(), 1f, 1f);
                        messageManager.sendMessage(e.player, Objects.requireNonNull(this.languageLoad.getString("Command.Island.Bank." + finalAction + ".Message")).replace(
                                "%amount%", NumberUtils.formatNumber(amount)));
                        break;
                }

                e.player.closeInventory();
                this.guiManager.showGUI(event.player, this.returnGui);
            }));

            switch (this.type) {
                case DEPOSIT:
                    gui.setTitle(this.languageLoad.getString("Menu.Bank.Words.Deposit"));
                    break;
                case WITHDRAW:
                    gui.setTitle(this.languageLoad.getString("Menu.Bank.Words.Withdraw"));
                    break;
            }
            this.guiManager.showGUI(event.player, gui);
        });
    }
}
