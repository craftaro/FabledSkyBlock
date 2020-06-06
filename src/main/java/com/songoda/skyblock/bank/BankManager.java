package com.songoda.skyblock.bank;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BankManager {
    private static BankManager instance;

    public static BankManager getInstance() {return instance == null ? instance = new BankManager() : instance;}

    private HashMap<UUID, List<Transaction>> log;

    public FileConfiguration lang;

    public BankManager() {
        SkyBlock skyblock = SkyBlock.getInstance();
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        lang = config.getFileConfiguration();
        log = new HashMap<>();
        loadTransactions();
    }

    public List<String> getTransactions(Player player) {
        if (log.containsKey(player.getUniqueId())&&log.get(player.getUniqueId())!=null&&!log.get(player.getUniqueId()).isEmpty()) {
            List<String> lore = new ArrayList<>();
            List<Transaction> transactions = log.get(player.getUniqueId());
            int size = transactions.size()>10 ? 10 : transactions.size();
            for (int i = 0;i<size;i++) {
                Transaction t = transactions.get((transactions.size()-1)-i);
                lore.add("#" + (i+1) + " " + t.timestamp.toString() +" " + t.player.getPlayer().getDisplayName() + " " + t.action.name().toLowerCase() + " " + EconomyManager.formatEconomy(t.ammount));
            }
            return lore;
        }else {
            List<String> lore = new ArrayList<>();
            lore.add(lang.getString("Menu.Bank.Item.Log.Empty"));
            return lore;
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
        Island island = SkyBlock.getInstance().getIslandManager().getIslandByPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        result.add("If this is null then its a easy to fix bug: "+island.toString());
        if (island != null) {
            result.clear();
            result.add(player.getDisplayName()+"'s balance is "+EconomyManager.formatEconomy(EconomyManager.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()))));
            result.add(player.getDisplayName()+"'s island has "+EconomyManager.formatEconomy(island.getBankBalance()));
        }
        return result;
    }

    public List<Transaction> getTransactionList(Player player) {
        return log.get(player.getUniqueId());
    }
}
