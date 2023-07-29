package com.songoda.skyblock.bank;

import org.bukkit.OfflinePlayer;

import java.util.Date;

public class Transaction {
    public OfflinePlayer player;
    public float amount;
    public Date timestamp;
    public Type action;
    public Visibility visibility;

    public enum Type {
        WITHDRAW,
        DEPOSIT
    }

    public enum Visibility {
        ADMIN,
        USER
    }
}
