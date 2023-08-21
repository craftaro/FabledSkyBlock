package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.gui.bank.GuiBank;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class AdminBank extends SubCommand {
    public AdminBank(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        FileManager fileManager = this.plugin.getFileManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (!this.plugin.getConfiguration().getBoolean("Island.Bank.Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Disabled.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        if (args.length < 1) {
            if (island != null) {
                this.plugin.getGuiManager().showGUI(player, new GuiBank(this.plugin, island, null, true));
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.NullIsland.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "balance":
                    double balance = 0;

                    if (args.length >= 3) {
                        if (economy != null) {
                            balance = economy.getBalance(Bukkit.getOfflinePlayer(island.getOwnerUUID()));
                        }

                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.Balance.Message")
                                .replace("%player%", args[1])
                                .replace("%bal%", EconomyManager.formatEconomy(balance)));
                    } else {
                        if (economy != null) {
                            balance = economy.getBalance(Bukkit.getOfflinePlayer(args[1]));
                        }

                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.Balance.Message")
                                .replace("%player%", args[1])
                                .replace("%bal%", EconomyManager.formatEconomy(balance)));
                    }
                    return;
                case "deposit":
                    if (args.length >= 3) {
                        islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).addToBank(Double.parseDouble(args[2]));
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.SuccesDeposit.Message").replace("%player%", args[1]).replace("%ammount%", EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                    }
                    return;
                case "withdraw":
                    if (args.length >= 3) {
                        islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).removeFromBank(Double.parseDouble(args[2]));
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.SuccesWithdraw.Message").replace("%player%", args[1]).replace("%ammount%", EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                    }
                    return;
                case "open":
                    if (args.length == 2) {
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
                    if (island != null) {
                        this.plugin.getGuiManager().showGUI(player, new GuiBank(this.plugin, island, null, true));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.NullIsland.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                    break;
                default:
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Bank.Unexpected.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        FileManager fileManager = this.plugin.getFileManager();
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length < 2) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.admin.Bank.ByConsole.Message"));
            return;
        }
        switch (args[0]) {
            case "balance":
                double balance = 0;
                if (economy != null) {
                    balance = economy.getBalance(Bukkit.getOfflinePlayer(args[1]));
                }
                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.Balance.Message")
                        .replace("%player%", args[1])
                        .replace("%bal%", EconomyManager.formatEconomy(balance)));
                return;
            case "deposit":
                if (args.length >= 3) {
                    islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).addToBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.SuccesDeposit.Message").replace("%player%", args[1]).replace("%ammount%", EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                } else {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.ByConsole.Message"));
                }
                return;
            case "withdraw":
                if (args.length >= 3) {
                    islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(Bukkit.getPlayer(args[1]).getUniqueId())).removeFromBank(Double.parseDouble(args[2]));
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Bank.SuccesWithdraw.Message").replace("%player%", args[1]).replace("%ammount%", EconomyManager.formatEconomy(Double.parseDouble(args[2]))));
                } else {
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
