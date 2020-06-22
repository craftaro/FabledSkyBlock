package com.songoda.skyblock.gui.bank;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.utils.NumberUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Objects;

public class GuiBankSelector extends Gui {
    private final SkyBlock plugin;
    private final BankManager bankManager;
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
        this.island = island;
        this.returnGui = returnGui;
        this.admin = admin;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Input.Title")));
        paint();
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Input.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Input.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            event.player.closeInventory();
        });

        String action;
        switch(type){
            case DEPOSIT:
                action = "Deposit";
                break;
            case WITHDRAW:
                action = "Withdraw";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        final String finalAction = action;

        setButton(2, GuiUtils.createButtonItem(CompatibleMaterial.GOLD_BLOCK, // All
                TextUtils.formatText(languageLoad.getString("Menu.Input.Item.All.Displayname")),
                TextUtils.formatText(languageLoad.getString("Menu.Input.Item.All.Lore")
                        .replace("%action%", languageLoad.getString("Menu.Bank.Words." + action)))), (event -> {
            MessageManager messageManager = plugin.getMessageManager();
            BankManager.BankResponse response;
            double amount;

            switch(type){
                case DEPOSIT:
                    amount = EconomyManager.getBalance(event.player);
                    if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
                        amount = Math.floor(amount);
                    }
                    response = bankManager.deposit(event.player, island, amount, admin);
                    break;
                case WITHDRAW:
                    amount = island.getBankBalance();
                    if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
                        amount = Math.floor(amount);
                    }
                    response = bankManager.withdraw(event.player, island, amount, admin);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }

            switch(response){
                case NOT_ENOUGH_MONEY:
                    messageManager.sendMessage(event.player, languageLoad.getString("Command.Island.Bank.Short2.Message"));
                    CompatibleSound.BLOCK_ANVIL_LAND.play(event.player);
                    break;
                case DECIMALS_NOT_ALLOWED:
                    messageManager.sendMessage(event.player, languageLoad.getString("Command.Island.Bank.Short6.Message"));
                    CompatibleSound.BLOCK_ANVIL_LAND.play(event.player);
                    break;
                case NEGATIVE_AMOUNT:
                    messageManager.sendMessage(event.player, languageLoad.getString("Command.Island.Bank.Short5.Message"));
                    CompatibleSound.BLOCK_ANVIL_LAND.play(event.player);
                    break;
                case SUCCESS:
                    CompatibleSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(event.player);
                    messageManager.sendMessage(event.player, Objects.requireNonNull(languageLoad.getString("Command.Island.Bank." + finalAction + ".Message")).replace(
                            "%amount%", NumberUtil.formatNumberByDecimal(amount)));

                    break;
            }
            guiManager.showGUI(event.player, returnGui);
        }));

        setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.PAPER, // Custom
                TextUtils.formatText(languageLoad.getString("Menu.Input.Item.Custom.Displayname")),
                TextUtils.formatText(languageLoad.getString("Menu.Input.Item.Custom.Lore")
                        .replace("%action%", languageLoad.getString("Menu.Bank.Words." + action)))), (event) -> {
            AnvilGui gui = new AnvilGui(event.player, returnGui);
            gui.setAction((e -> {
                MessageManager messageManager = plugin.getMessageManager();

                double amount;
                try {
                    amount = Double.parseDouble(gui.getInputText().trim());
                } catch (NumberFormatException e1) {
                    messageManager.sendMessage(e.player, languageLoad.getString("Command.Island.Bank.Short4.Message"));
                    CompatibleSound.BLOCK_ANVIL_LAND.play(e.player);
                    return;
                }

                BankManager.BankResponse response;

                switch(type){
                    case DEPOSIT:
                        response = bankManager.deposit(event.player, island, amount, admin);
                        break;
                    case WITHDRAW:
                        response = bankManager.withdraw(event.player, island, amount, admin);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }

                switch(response){
                    case NOT_ENOUGH_MONEY:
                        messageManager.sendMessage(e.player, languageLoad.getString("Command.Island.Bank.Short2.Message"));
                        CompatibleSound.BLOCK_ANVIL_LAND.play(e.player);
                        break;
                    case DECIMALS_NOT_ALLOWED:
                        messageManager.sendMessage(e.player, languageLoad.getString("Command.Island.Bank.Short6.Message"));
                        CompatibleSound.BLOCK_ANVIL_LAND.play(e.player);
                        break;
                    case NEGATIVE_AMOUNT:
                        messageManager.sendMessage(e.player, languageLoad.getString("Command.Island.Bank.Short5.Message"));
                        CompatibleSound.BLOCK_ANVIL_LAND.play(e.player);
                        break;
                    case SUCCESS:
                        CompatibleSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(e.player);
                        messageManager.sendMessage(e.player, Objects.requireNonNull(languageLoad.getString("Command.Island.Bank." + finalAction + ".Message")).replace(
                                "%amount%", NumberUtil.formatNumberByDecimal(amount)));
                        break;
                }

                e.player.closeInventory();
                guiManager.showGUI(event.player, returnGui);
            }));

            ItemStack input = CompatibleMaterial.PAPER.getItem();
            ItemMeta im = input.getItemMeta();
            if(im != null){
                im.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Amount")));
                input.setItemMeta(im);
            }

            gui.setInput(input);
            switch(type){
                case DEPOSIT:
                    gui.setTitle(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Deposit")));
                    break;
                case WITHDRAW:
                    gui.setTitle(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Withdraw")));
                    break;
            }
            guiManager.showGUI(event.player, gui);
        });
    }
}
