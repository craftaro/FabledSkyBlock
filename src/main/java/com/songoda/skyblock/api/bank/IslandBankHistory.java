package com.songoda.skyblock.api.bank;

import java.util.Set;
import java.util.UUID;

public interface IslandBankHistory {

    /**
     * @return All transactions that have been made on the island
     */
    Set<IslandBankTransaction> getTransactions();

    /**
     * @param type The TransactionType to filter by
     * @return All transactions that matches the TransactionType
     */
    Set<IslandBankTransaction> getTransactions(TransactionType type);

    /**
     * @param type The TransactionType to filter by
     * @param limit Max amount of transactions to return
     * @return All transactions by the TransactionType up to the limit
     */
    Set<IslandBankTransaction> getTransactions(TransactionType type, int limit);

    /**
     * @param type The TransactionType to filter by
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @return All transactions by the TransactionType up to the limit with an offset
     */
    Set<IslandBankTransaction> getTransactions(TransactionType type, int limit, int offset);

    /**
     * @param limit Max amount of transactions to return
     * @return All transactions up to the limit
     */
    Set<IslandBankTransaction> getTransactions(int limit);

    /**
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @return All transactions up to the limit with an offset
     */
    Set<IslandBankTransaction> getTransactions(int limit, int offset);

    /**
     * @param type The TransactionType to filter by
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @param from Timestamp to filter transactions from
     * @param to Timestamp to filter transactions to
     * @return All transactions by the TransactionType up to the limit with an offset between the timestamps
     */
    Set<IslandBankTransaction> getTransactions(TransactionType type, int limit, int offset, long from, long to);

    /**
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @param from Timestamp to filter transactions from
     * @param to Timestamp to filter transactions to
     * @return All transactions up to the limit with an offset between the timestamps
     */
    Set<IslandBankTransaction> getTransactions(int limit, int offset, long from, long to);

    /**
     * @param uuid The player's UUID to filter by
     * @return All transactions made by the player
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid);

    /**
     * @param uuid The player's UUID to filter by
     * @param limit Max amount of transactions to return
     * @return All transactions made by the player by the TransactionType
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid, int limit);

    /**
     * @param uuid The player's UUID to filter by
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @return All transactions made by the player by the TransactionType up to the limit with an offset
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid, int limit, int offset);

    /**
     * @param uuid The player's UUID to filter by
     * @param type The TransactionType to filter by
     * @return All transactions made by the player by the TransactionType
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid, TransactionType type);

    /**
     * @param uuid The player's UUID to filter by
     * @param type The TransactionType to filter by
     * @param limit Max amount of transactions to return
     * @return All transactions made by the player by the TransactionType up to the limit
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid, TransactionType type, int limit);

    /**
     * @param uuid The player's UUID to filter by
     * @param type The TransactionType to filter by
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @return All transactions made by the player by the TransactionType up to the limit with an offset
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid, TransactionType type, int limit, int offset);

    /**
     * @param uuid The player's UUID to filter by
     * @param type The TransactionType to filter by
     * @param limit Max amount of transactions to return
     * @param offset Offset of the transactions to return
     * @param from Timestamp to filter transactions from
     * @param to Timestamp to filter transactions to
     * @return All transactions made by the player up to the limit
     */
    Set<IslandBankTransaction> getTransactionByPlayer(UUID uuid, TransactionType type, int limit, int offset, long from, long to);

    /**
     * Delete a transaction from the history
     * @param transaction to delete from the database
     */
    void deleteTransaction(IslandBankTransaction transaction);

    /**
     * Delete all transactions from the history
     */
    void deleteAllTransactions();
}
