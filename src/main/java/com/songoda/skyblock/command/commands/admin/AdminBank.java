package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public class AdminBank extends SubCommand {


    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Disabled.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        if (args.length < 1) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.admin.Bank.Short01.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        if (island == null && args.length <2) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.admin.Bank.NullIsland.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        switch (args[0]) {
            case "balance":
                if (args.length <2) {
                    messageManager.sendMessage(player, Objects.requireNonNull(configLoad.getString("Command.Island.Admin.Bank.Balance.Message")).replace("%player%", args[1]).replace("%bal%", "" + EconomyManager.formatEconomy(EconomyManager.getBalance(Bukkit.getOfflinePlayer(args[1])))));
                }else {
                    messageManager.sendMessage(player, Objects.requireNonNull(configLoad.getString("Command.Island.Admin.Bank.Balance.Message")).replace("%player%", args[1]).replace("%bal%", "" + EconomyManager.formatEconomy(EconomyManager.getBalance(Bukkit.getOfflinePlayer(island.getOwnerUUID())))));
                }
                return;
            case "deposit":
                if (args.length >= 3) {
                    islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId())).addToBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.SuccesDeposit.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                }else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
                }
                return;
            case "withdraw":
                if (args.length >= 3) {
                    islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).removeFromBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.SuccesWithdraw.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                }else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
                }
                return;
            default:
                configLoad.getString("Command.Island.admin.Bank.Unexpected.Message");
                return;
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        FileManager fileManager = skyblock.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length < 2) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
            return;
        }
        switch (args[0]) {
            case "balance":
                messageManager.sendMessage(sender,configLoad.getString("Command.Island.Admin.Bank.Balance.Message").replace("%player%",args[1]).replace("%bal%",""+ EconomyManager.formatEconomy(EconomyManager.getBalance(Bukkit.getOfflinePlayer(args[1])))));
                return;
            case "deposit":
                if (args.length >= 3) {
                    islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).addToBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.SuccesDeposit.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                }else {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
                }
                return;
            case "withdraw":
                if (args.length >= 3) {
                    islandManager.getIslandByPlayer(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).removeFromBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.SuccesWithdraw.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                }else {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
                }
                return;
            default:
                configLoad.getString("Command.Island.admin.Bank.Unexpected.Message");
                return;
        }

    }

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Bank.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
