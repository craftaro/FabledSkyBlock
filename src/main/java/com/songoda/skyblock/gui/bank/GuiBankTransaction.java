package com.songoda.skyblock.gui.bank;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.bank.Transaction;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
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
        if(transactions == 0){
            setRows(2);
        } else if(transactions > 4*9){
            setRows(6);
        } else {
            setRows((int) (Math.ceil((double) transactions / 9d)+1));
        }

        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Title")));
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            guiManager.showGUI(event.player, returnGui);
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            guiManager.showGUI(event.player, returnGui);
        });

        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING, // Info
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Info.Displayname")),
                TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Info.Lore")
                        .replace("%totalTransactions", String.valueOf(transactions)))));

        if(transactions > 0){
            this.pages = (int) Math.max(1, Math.ceil((double) transactions / 36d));

            if (page != 1)
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Last.Displayname"))),
                        (event) -> {
                            page--;
                            paint();
                        });

            if (page != pages)
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Next.Displayname"))),
                        (event) -> {
                            page++;
                            paint();
                        });

            for (int i = 9; i < ((getRows()-1)*9)+9; i++) { // TODO check dynamic dimension!
                int current = ((page - 1) * 36) - 9;
                if (current + i >= transactions) {
                    setItem(i, null);
                    continue;
                }
                Transaction transaction = transactionList.get(current + i);
                if (transaction == null) continue;

                ItemStack is = null;
                ItemMeta im;
                String name = "";
                SimpleDateFormat formatDate = new SimpleDateFormat(languageLoad.getString("Menu.Bank.Item.Transactions.DateTimeFormat", "dd/MM/yyyy HH:mm:ss"));
                switch(transaction.action){
                    case WITHDRAW:
                        is = CompatibleMaterial.RED_DYE.getItem();
                        im = is.getItemMeta();
                        if(im != null){

                            im.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Withdraw.Displayname")
                                    .replace("%dateTime", formatDate.format(transaction.timestamp))));
                            List<String> lore = new ArrayList<>();
                            switch (transaction.visibility){
                                case ADMIN:
                                    name = languageLoad.getString("Menu.Bank.Transactions.Admin");
                                    if(admin){
                                        name += " " + transaction.player.getName();
                                    }
                                    break;
                                case USER:
                                    name = transaction.player.getName();
                                    break;
                            }
                            if(name == null){
                                name = "null";
                            }
                            lore.add(TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Withdraw.Format")
                                    .replace("%playerName", name)
                                    .replace("%amount", String.valueOf(transaction.amount))));
                            im.setLore(lore);
                            is.setItemMeta(im);
                        }
                        break;
                    case DEPOSIT:
                        is = CompatibleMaterial.GREEN_DYE.getItem();
                        im = is.getItemMeta();
                        if(im != null){

                            im.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Deposit.Displayname")
                                    .replace("%dateTime",
                                            formatDate.format(transaction.timestamp))));
                            List<String> lore = new ArrayList<>();
                            switch (transaction.visibility){
                                case ADMIN:
                                    name = languageLoad.getString("Menu.Bank.Word.Admin");
                                    if(admin){
                                        name += transaction.player.getName();
                                    }
                                    break;
                                case USER:
                                    name = transaction.player.getName();
                                    break;
                            }
                            lore.add(TextUtils.formatText(languageLoad.getString("Menu.Bank.Transactions.Deposit.Format")
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
