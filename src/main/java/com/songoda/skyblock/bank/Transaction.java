package com.songoda.skyblock.bank;

import org.bukkit.OfflinePlayer;

import java.util.Date;

public class Transaction {

    public OfflinePlayer player;
    public float ammount;
    public Date timestamp;
    public Type action;
}