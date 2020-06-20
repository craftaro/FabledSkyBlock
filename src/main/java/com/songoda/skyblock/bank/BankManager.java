package com.songoda.skyblock.bank;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BankManager {
    private static BankManager instance;

    public static BankManager getInstance() {return instance == null ? instance = new BankManager() : instance;}

    private final HashMap<UUID, List<Transaction>> log;

    public FileConfiguration lang;

    public BankManager() {
        SkyBlock skyblock = SkyBlock.getInstance();
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        lang = config.getFileConfiguration();
        log = new HashMap<>();
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
        if (log.containsKey(uuid)
                && log.get(uuid) != null
                && !log.get(uuid).isEmpty()) {
            return new ArrayList<>(log.get(uuid));
        }else {
            return new ArrayList<>();
        }
    }

    public void addTransaction(Player p, Transaction transaction) {
        if (log.containsKey(p.getUniqueId())) {
            log.get(p.getUniqueId()).add(transaction);
         }else {
            List<Transaction> t = new ArrayList<>();
            t.add(transaction);
            log.put(p.getUniqueId(),t);
        }
    }

    private void loadTransactions() {
        for (UUID uid:SkyBlock.getInstance().getPlayerDataManager().getPlayerData().keySet()) {
            log.put(uid,SkyBlock.getInstance().getPlayerDataManager().getPlayerData().get(uid).getTransactions());
        }
    }

    public List<String> getBalanceLore(Player player) {
        List<String> result = new ArrayList<>();
        result.add("Some error occurred while loading your balance!");
        Island island = SkyBlock.getInstance().getIslandManager().getIsland(player);
        result.add("If this is null then its a easy to fix bug: "+island.toString());
        if (island != null) {
            result.clear();
            result.add(player.getDisplayName()+"'s balance is "+EconomyManager.formatEconomy(EconomyManager.getBalance(player)));
            result.add(player.getDisplayName()+"'s island has "+EconomyManager.formatEconomy(island.getBankBalance()));
        }
        return result;
    }

    public List<Transaction> getTransactionList(Player player) {
        return getTransactionList(player.getUniqueId());
    }

    public List<Transaction> getTransactionList(UUID uuid) {
        return log.get(uuid);
    }

    public BankResponse deposit(Player player, Island island, double amt, boolean admin) {
        SkyBlock skyblock = SkyBlock.getInstance();
        FileManager fileManager = skyblock.getFileManager();

        // Make sure the amount is positive
        if (amt <= 0) {
            return BankResponse.NEGATIVE_AMOUNT;
        }

        // If decimals aren't allowed, check for them
        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
            int intAmt = (int) amt;
            if (intAmt != amt) {
                return BankResponse.DECIMALS_NOT_ALLOWED;
            }
        }

        if(!admin) {
            if (!EconomyManager.hasBalance(player, amt)) {
                return BankResponse.NOT_ENOUGH_MONEY;
            }

            EconomyManager.withdrawBalance(player, amt);
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
        SkyBlock skyblock = SkyBlock.getInstance();
        FileManager fileManager = skyblock.getFileManager();

        // Make sure the amount is positive
        if (amt <= 0) {
            return BankResponse.NEGATIVE_AMOUNT;
        }

        // If decimals aren't allowed, check for them
        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
            int intAmt = (int) amt;
            if (intAmt != amt) {
                return BankResponse.DECIMALS_NOT_ALLOWED;
            }
        }

        if(!admin){
            if (amt > island.getBankBalance()) {
                return BankResponse.NOT_ENOUGH_MONEY;
            }

            EconomyManager.deposit(player, amt);
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

    public enum BankResponse{
        NOT_ENOUGH_MONEY,
        DECIMALS_NOT_ALLOWED,
        NEGATIVE_AMOUNT,
        SUCCESS
    }
}
