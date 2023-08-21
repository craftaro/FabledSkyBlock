package com.craftaro.skyblock.bank;

import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BankManager {
    private final HashMap<UUID, List<Transaction>> log;

    private final SkyBlock plugin;

    public FileConfiguration lang;

    public BankManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.lang = this.plugin.getLanguage();
        this.log = new HashMap<>();
        loadTransactions();
    }

    /*public List<String> getTransactions(Player player) {
        if (log.containsKey(player.getUniqueId())&&log.get(player.getUniqueId())!=null&&!log.get(player.getUniqueId()).isEmpty()) {
            List<String> lore = new ArrayList<>();
            List<Transaction> transactions = log.get(player.getUniqueId());
            int size = transactions.size()>10 ? 10 : transactions.size();
            for (int i = 0;i<size;i++) {
                Transaction t = transactions.get((transactions.size()-1)-i);
                SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                lore.add("#" + (i+1) + " " + formatDate.format(t.timestamp) +" " + t.player.getPlayer().getDisplayName() + " " + t.action.name().toLowerCase() + " " + EconomyManager.formatEconomy(t.ammount));
            }
            return lore;
        }else {
            List<String> lore = new ArrayList<>();
            lore.add(lang.getString("Menu.Bank.Item.Log.Empty"));
            return lore;
        }
    }*/

    public List<Transaction> getTransactions(Player player) {
        return getTransactions(player.getUniqueId());
    }

    public List<Transaction> getTransactions(UUID uuid) {
        if (this.log.containsKey(uuid)
                && this.log.get(uuid) != null
                && !this.log.get(uuid).isEmpty()) {
            return new ArrayList<>(this.log.get(uuid));
        } else {
            return new ArrayList<>();
        }
    }

    public void addTransaction(Player p, Transaction transaction) {
        if (this.log.containsKey(p.getUniqueId())) {
            this.log.get(p.getUniqueId()).add(transaction);
        } else {
            List<Transaction> t = new ArrayList<>();
            t.add(transaction);
            this.log.put(p.getUniqueId(), t);
        }
    }

    private void loadTransactions() {
        for (UUID uid : SkyBlock.getInstance().getPlayerDataManager().getPlayerData().keySet()) {
            this.log.put(uid, SkyBlock.getInstance().getPlayerDataManager().getPlayerData().get(uid).getTransactions());
        }
    }

    public List<String> getBalanceLore(Player player) {
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        List<String> result = new ArrayList<>();
        result.add("Some error occurred while loading your balance!");
        Island island = SkyBlock.getPlugin(SkyBlock.class).getIslandManager().getIsland(player);
        result.add("If this is null then its a easy to fix bug: " + island.toString());
        if (island != null) {
            double accountBalance = 0;
            if (economy != null) {
                accountBalance = economy.getBalance(player);
            }

            result.clear();
            result.add(player.getDisplayName() + "'s balance is " + EconomyManager.formatEconomy(accountBalance));
            result.add(player.getDisplayName() + "'s island has " + EconomyManager.formatEconomy(island.getBankBalance()));
        }
        return result;
    }

    public List<Transaction> getTransactionList(Player player) {
        return getTransactionList(player.getUniqueId());
    }

    public List<Transaction> getTransactionList(UUID uuid) {
        return this.log.get(uuid);
    }

    public BankResponse deposit(Player player, Island island, double amt, boolean admin) {
        Economy economy = this.plugin.getEconomyManager().getEconomy();
        FileManager fileManager = this.plugin.getFileManager();

        // Make sure the amount is positive
        if (amt <= 0) {
            return BankResponse.NEGATIVE_AMOUNT;
        }

        // If decimals aren't allowed, check for them
        if (!this.plugin.getConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
            int intAmt = (int) amt;
            if (intAmt != amt) {
                return BankResponse.DECIMALS_NOT_ALLOWED;
            }
        }

        if (!admin) {
            if (economy == null || !economy.hasBalance(player, amt)) {
                if (economy == null) {
                    this.plugin.getLogger().warning("No compatible economy plugin found – Please check your configuration");
                }

                return BankResponse.NOT_ENOUGH_MONEY;
            }

            economy.withdrawBalance(player, amt);
        }

        island.addToBank(amt);
        Transaction t = new Transaction();
        t.player = player;
        t.amount = (float) amt;
        t.timestamp = Calendar.getInstance().getTime();
        t.action = Transaction.Type.DEPOSIT;
        t.visibility = admin ? Transaction.Visibility.ADMIN : Transaction.Visibility.USER;
        this.addTransaction(player, t);
        return BankResponse.SUCCESS;
    }

    public BankResponse withdraw(Player player, Island island, double amt, boolean admin) {
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        // Make sure the amount is positive
        if (amt <= 0) {
            return BankResponse.NEGATIVE_AMOUNT;
        }

        // If decimals aren't allowed, check for them
        if (!this.plugin.getConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
            int intAmt = (int) amt;
            if (intAmt != amt) {
                return BankResponse.DECIMALS_NOT_ALLOWED;
            }
        }

        if (!admin) {
            if (economy == null || amt > island.getBankBalance()) {
                if (economy == null) {
                    this.plugin.getLogger().warning("No compatible economy plugin found – Please check your configuration");
                }

                return BankResponse.NOT_ENOUGH_MONEY;
            }

            economy.deposit(player, amt);
        }

        island.removeFromBank(amt);
        Transaction t = new Transaction();
        t.player = player;
        t.amount = (float) amt;
        t.timestamp = Calendar.getInstance().getTime();
        t.action = Transaction.Type.WITHDRAW;
        t.visibility = admin ? Transaction.Visibility.ADMIN : Transaction.Visibility.USER;
        this.addTransaction(player, t);
        return BankResponse.SUCCESS;
    }

    public enum BankResponse {
        NOT_ENOUGH_MONEY,
        DECIMALS_NOT_ALLOWED,
        NEGATIVE_AMOUNT,
        SUCCESS
    }
}
