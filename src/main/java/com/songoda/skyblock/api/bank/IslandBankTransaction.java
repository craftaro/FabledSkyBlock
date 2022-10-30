package com.songoda.skyblock.api.bank;

import com.songoda.skyblock.bank.Transaction;

import java.util.UUID;

public interface IslandBankTransaction {

    /**
     * @return The player's name who made the transaction
     */
    String getPlayerName();

    /**
     * @return The player's UUID who made the transaction
     */
    UUID getPlayerUUID();

    /**
     * @return The amount of money that was deposited or withdrawn
     */
    int getAmount();

    /**
     * @return The timestamp of the transaction
     */
    long getTime();

    /**
     * @return The type of transaction
     */
    TransactionType getType();
}
