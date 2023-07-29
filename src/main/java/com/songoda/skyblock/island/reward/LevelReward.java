package com.songoda.skyblock.island.reward;

import com.songoda.core.hooks.economies.Economy;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelReward {
    private List<String> commands = new ArrayList<>();

    private double money = 0;

    private double islandBalance = 0;

    public LevelReward() {
    }

    public void give(Player player, SkyBlock plugin, long level) {
        Economy economy = plugin.getEconomyManager().getEconomy();

        if (this.islandBalance > 0) {
            Island island = plugin.getIslandManager().getIsland(player);
            island.addToBank(this.islandBalance);
        }

        if (this.money > 0) {
            economy.deposit(player, this.money);
        }

        if (!this.commands.isEmpty()) {
            for (String cmd : this.commands) {
                cmd = cmd.replace("%level%", String.valueOf(level))
                        .replace("%player%", player.getName())
                        .trim();

                plugin.getServer().dispatchCommand(plugin.getConsole(), cmd);
            }
        }
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setIslandBalance(double islandBalance) {
        this.islandBalance = islandBalance;
    }
}
