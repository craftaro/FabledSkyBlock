package com.songoda.skyblock.gui.bank;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.gui.GuiSignatureEditor;
import com.songoda.skyblock.gui.GuiWelcomeEditor;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandMessage;
import com.songoda.skyblock.island.IslandPermission;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GuiBank extends Gui {
    private final SkyBlock plugin;
    private final BankManager bankManager;
    private final Island island;
    private final FileConfiguration languageLoad;
    private final boolean admin;

    public GuiBank(SkyBlock plugin, Island island, Gui returnGui, boolean admin) {
        super(2, returnGui);
        this.plugin = plugin;;
        this.bankManager = plugin.getBankManager();
        this.island = island;
        this.admin = admin;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Bank.Title")));
        paint();
    }

    @Override
    public void onOpen(@Nonnull GuiManager manager, @Nonnull Player player) {
        updateItem(13, // Balance
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Balance.Displayname")),
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Balance.Lore")
                        .replace("%balance", String.valueOf(island.getBankBalance()))));
        super.onOpen(manager, player);
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setDefaultItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        setActionForRange(0, 0, 1, 8, null);

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

        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.BOOK, // Transaction log
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Log.Displayname"))), (event) -> guiManager.showGUI(event.player, new GuiBankTransaction(plugin, island, this, admin)));

        setButton(10, GuiUtils.createButtonItem(CompatibleMaterial.RED_DYE, // Deposit
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Deposit.Displayname"))), (event -> {
            AnvilGui gui = new AnvilGui(event.player, this);
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
                BankManager.BankResponse response = bankManager.deposit(e.player, island, amount, admin);

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
                        messageManager.sendMessage(e.player, Objects.requireNonNull(languageLoad.getString("Command.Island.Bank.Deposit.Message")).replace(
                                "%amount%", NumberUtil.formatNumberByDecimal(amount)));
                        break;
                }

                e.player.closeInventory();
                // paint();
                guiManager.showGUI(event.player, this);
            }));

            ItemStack input = CompatibleMaterial.PAPER.getItem();
            ItemMeta im = input.getItemMeta();
            if(im != null){
                im.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Amount")));
                input.setItemMeta(im);
            }

            gui.setInput(input);
            gui.setTitle(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Deposit")));
            guiManager.showGUI(event.player, gui);
        }));

        setItem(13, GuiUtils.createButtonItem(CompatibleMaterial.GOLD_INGOT, // Balance
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Balance.Displayname")),
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Balance.Lore")
                        .replace("%balance", String.valueOf(island.getBankBalance())))));

        setButton(16, GuiUtils.createButtonItem(CompatibleMaterial.GREEN_DYE, // WithDraw
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Withdraw.Displayname"))), (event) -> {
            AnvilGui gui = new AnvilGui(event.player, this);
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

                BankManager.BankResponse response = bankManager.withdraw(e.player, island, amount, admin);

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
                        messageManager.sendMessage(e.player, Objects.requireNonNull(languageLoad.getString("Command.Island.Bank.Withdraw.Message")).replace(
                                "%amount%", NumberUtil.formatNumberByDecimal(amount)));
                        break;
                }

                e.player.closeInventory();
                // paint();
                guiManager.showGUI(event.player, this);
            }));

            ItemStack input = CompatibleMaterial.PAPER.getItem();
            ItemMeta im = input.getItemMeta();
            if(im != null){
                im.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Amount")));
                input.setItemMeta(im);
            }

            gui.setInput(input);
            gui.setTitle(TextUtils.formatText(languageLoad.getString("Menu.Bank.Words.Withdraw")));
            guiManager.showGUI(event.player, gui);
        });
    }
}
