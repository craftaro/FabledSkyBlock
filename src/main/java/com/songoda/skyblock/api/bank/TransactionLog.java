package com.songoda.skyblock.api.bank;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.bank.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TransactionLog {
    public BankManager getImplementation() {
        return SkyBlock.getPlugin(SkyBlock.class).getBankManager();
    }

    public List<Transaction> getLogForPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return null;
        }

        return getImplementation().getTransactionList(player);
    }
}
