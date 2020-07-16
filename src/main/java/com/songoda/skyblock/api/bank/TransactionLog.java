package com.songoda.skyblock.api.bank;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.bank.Transaction;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class TransactionLog {

    public BankManager getImplementation() {
        return SkyBlock.getInstance().getBankManager();
    }

    public List<Transaction> getLogForPlayer(UUID uuid) {
        return getImplementation().getTransactionList(Bukkit.getPlayer(uuid));
    }
}
