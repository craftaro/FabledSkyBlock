package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.economies.Economy;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.gui.bank.GuiBank;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class AdminBank extends SubCommand {


    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        FileManager fileManager = plugin.getFileManager();
        SoundManager soundManager = plugin.getSoundManager();
        Economy economy = plugin.getEconomyManager().getEconomy();

        FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (!this.plugin.getConfiguration().getBoolean("Island.Bank.Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Disabled.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
            return;
        }

        if (args.length < 1) {
            if (island != null){
                plugin.getGuiManager().showGUI(player, new GuiBank(plugin, island, null, true));
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.NullIsland.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "balance":
                    if (args.length >= 3) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.Balance.Message").replace("%player%", args[1]).replace("%bal%", "" + EconomyManager.formatEconomy(economy.getBalance(Bukkit.getOfflinePlayer(island.getOwnerUUID())))));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.Balance.Message").replace("%player%", args[1]).replace("%bal%", "" + EconomyManager.formatEconomy(economy.getBalance(Bukkit.getOfflinePlayer(args[1])))));
                    }
                    return;
                case "deposit":
                    if (args.length >= 3) {
                        islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).addToBank(Double.parseDouble(args[2]));
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.SuccesDeposit.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                    }else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                    }
                    return;
                case "withdraw":
                    if (args.length >= 3) {
                        islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).removeFromBank(Double.parseDouble(args[2]));
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.SuccesWithdraw.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                    }else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                    }
                    return;
                case "open":
                    if(args.length == 2){
                        Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
                        UUID islandOwnerUUID;
                
                        if (targetPlayer == null) {
                            OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[1]);
                            islandOwnerUUID = targetPlayerOffline.getOwner();
                        } else {
                            islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
                        }
                
                        island = islandManager.getIsland(Bukkit.getOfflinePlayer(islandOwnerUUID));
                    }
                    if (island != null){
                        plugin.getGuiManager().showGUI(player, new GuiBank(plugin, island, null, true));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.NullIsland.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
                    }
                    break;
                default:
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.Unexpected.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1f, 1f);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        FileManager fileManager = plugin.getFileManager();
        Economy economy = plugin.getEconomyManager().getEconomy();

        FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length < 2) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
            return;
        }
        switch (args[0]) {
            case "balance":
                messageManager.sendMessage(sender,configLoad.getString("Command.Island.Admin.Bank.Balance.Message").replace("%player%",args[1]).replace("%bal%",""+ EconomyManager.formatEconomy(economy.getBalance(Bukkit.getOfflinePlayer(args[1])))));
                return;
            case "deposit":
                if (args.length >= 3) {
                    islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).addToBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.SuccesDeposit.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                }else {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                }
                return;
            case "withdraw":
                if (args.length >= 3) {
                    islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).removeFromBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.SuccesWithdraw.Message").replace("%player%",args[1]).replace("%ammount%",EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                }else {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                }
                return;
            default:
                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.Unexpected.Message"));
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
        return new String[]{"withdraw", "deposit", "balance", "open"};
    }
}
