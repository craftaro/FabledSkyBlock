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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class GuiBankTransaction extends Gui {
    private final SkyBlock plugin;
    private final BankManager bankManager;
    private final FileConfiguration languageLoad;
    private final FileManager.Config config;
    private final Gui returnGui;
    private final int transactions;
    private final List<Transaction> transactionList;

    public GuiBankTransaction(SkyBlock plugin, Island island, Gui returnGui) {
        super(returnGui);
        this.plugin = plugin;
        this.bankManager = plugin.getBankManager();
        this.transactionList = bankManager.getTransactions(island.getOwnerUUID());
        this.transactions = this.transactionList.size();
        this.returnGui = returnGui;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        this.config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));

        if(transactions == 0){
            setRows(2);
        } else if(transactions > 4*9){
            setRows(6);
        } else {
            setRows(transactions%9+1);
        }

        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Settings.Title"))); // TODO Title
        setDefaultItem(null);
        paint();
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            guiManager.showGUI(event.player, returnGui);
        });

        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING, // Info
                TextUtils.formatText(languageLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))));

        this.pages = (int) Math.max(1, Math.ceil((double) transactions / 36d));

        if (page != 1)
            setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                    TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Last.Displayname"))),
                    (event) -> {
                        page--;
                        paint();
                    });

        if (page != pages)
            setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                    TextUtils.formatText(languageLoad.getString("Menu.Settings.Categories.Item.Next.Displayname"))),
                    (event) -> {
                        page++;
                        paint();
                    });

        for (int i = 9; i < 45; i++) { // TODO dynamic dimension!
            int current = ((page - 1) * 36) - 9;
            if (current + i >= transactions) {
                setItem(i, null);
                continue;
            }
            Transaction transaction = transactionList.get(current + i);
            if (transaction == null) continue;

            ItemStack is = null;
            switch(transaction.action){
                case WITHDRAW:
                    is = CompatibleMaterial.GREEN_DYE.getItem();
                    break;
                case DEPOSIT:
                    is = CompatibleMaterial.RED_DYE.getItem();
                    break;
            }

            // TODO set item meta
            setItem(i, is);
        }
    }
}
