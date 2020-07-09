package com.songoda.skyblock.island.reward;

import com.songoda.core.hooks.EconomyManager;
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

        if (islandBalance > 0) {
            Island island = plugin.getIslandManager().getIsland(player);
            island.addToBank(islandBalance);
        }

        if (money > 0)
            EconomyManager.deposit(player, money);

        if (!commands.isEmpty()) {
            for (String cmd : commands) {
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