package com.songoda.skyblock.gui.bank;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.bank.Transaction;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiBankTransaction extends Gui {
    private final SoundManager soundManager;
    private final FileConfiguration languageLoad;
    private final Gui returnGui;
    private final int transactions;
    private final List<Transaction> transactionList;
    private final boolean admin;

    public GuiBankTransaction(SkyBlock plugin, Island island, Gui returnGui, boolean admin) {
        super(returnGui);
        BankManager bankManager = plugin.getBankManager();
        this.soundManager = plugin.getSoundManager();
        this.transactionList = bankManager.getTransactions(island.getOwnerUUID());
        this.transactions = this.transactionList.size();
        this.returnGui = returnGui;
        this.admin = admin;
        this.languageLoad = plugin.getLanguage();
        if (this.transactions == 0) {
            setRows(2);
        } else if (this.transactions > 4 * 9) {
            setRows(6);
        } else {
            setRows((int) (Math.ceil((double) this.transactions / 9d) + 1));
        }

        setTitle(TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Title")));
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (this.inventory != null) {
            this.inventory.clear();
        }

        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            this.guiManager.showGUI(event.player, this.returnGui);
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            this.guiManager.showGUI(event.player, this.returnGui);
        });

        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING, // Info
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Info.Displayname")),
                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Info.Lore")
                        .replace("%totalTransactions", String.valueOf(this.transactions)))));

        if (this.transactions > 0) {
            this.pages = (int) Math.max(1, Math.ceil((double) this.transactions / 36d));

            if (this.page != 1) {
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Last.Displayname"))),
                        (event) -> {
                            this.page--;
                            paint();
                        });
            }

            if (this.page != this.pages) {
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Next.Displayname"))),
                        (event) -> {
                            this.page++;
                            paint();
                        });
            }

            for (int i = 9; i < ((getRows() - 1) * 9) + 9; i++) { // TODO check dynamic dimension!
                int current = ((this.page - 1) * 36) - 9;
                if (current + i >= this.transactions) {
                    setItem(i, null);
                    continue;
                }
                Transaction transaction = this.transactionList.get(current + i);
                if (transaction == null) {
                    continue;
                }

                ItemStack is = null;
                ItemMeta im;
                String name = "";
                SimpleDateFormat formatDate = new SimpleDateFormat(this.languageLoad.getString("Menu.Bank.Item.Transactions.DateTimeFormat", "dd/MM/yyyy HH:mm:ss"));
                switch (transaction.action) {
                    case WITHDRAW:
                        is = CompatibleMaterial.RED_DYE.getItem();
                        im = is.getItemMeta();
                        if (im != null) {

                            im.setDisplayName(TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Withdraw.Displayname")
                                    .replace("%dateTime", formatDate.format(transaction.timestamp))));
                            List<String> lore = new ArrayList<>();
                            switch (transaction.visibility) {
                                case ADMIN:
                                    name = this.languageLoad.getString("Menu.Bank.Transactions.Admin");
                                    if (this.admin) {
                                        name += " " + transaction.player.getName();
                                    }
                                    break;
                                case USER:
                                    name = transaction.player.getName();
                                    break;
                            }
                            if (name == null) {
                                name = "null";
                            }
                            lore.add(TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Withdraw.Format")
                                    .replace("%playerName", name)
                                    .replace("%amount", String.valueOf(transaction.amount))));
                            im.setLore(lore);
                            is.setItemMeta(im);
                        }
                        break;
                    case DEPOSIT:
                        is = CompatibleMaterial.GREEN_DYE.getItem();
                        im = is.getItemMeta();
                        if (im != null) {

                            im.setDisplayName(TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Deposit.Displayname")
                                    .replace("%dateTime",
                                            formatDate.format(transaction.timestamp))));
                            List<String> lore = new ArrayList<>();
                            switch (transaction.visibility) {
                                case ADMIN:
                                    name = this.languageLoad.getString("Menu.Bank.Word.Admin");
                                    if (this.admin) {
                                        name += transaction.player.getName();
                                    }
                                    break;
                                case USER:
                                    name = transaction.player.getName();
                                    break;
                            }
                            lore.add(TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Transactions.Deposit.Format")
                                    .replace("%playerName", name)
                                    .replace("%amount", String.valueOf(transaction.amount))));
                            im.setLore(lore);
                            is.setItemMeta(im);
                        }
                        break;
                }

                setItem(i, is);
            }
        } else {
            setItem(31, CompatibleMaterial.BARRIER.getItem());
        }
    }
}
