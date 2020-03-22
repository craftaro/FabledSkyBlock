package com.songoda.skyblock.command.commands.island;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.utils.VaultPermissions;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BankCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Disabled.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        if (args.length == 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Invalid.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Unknown.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        if (!island.hasRole(IslandRole.Operator, player.getUniqueId())
                && !island.hasRole(IslandRole.Owner, player.getUniqueId())
                && !island.hasRole(IslandRole.Member, player.getUniqueId())) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Perm.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        double balance = island.getBankBalance();

        switch (args[0].toLowerCase()) {
            case "balance":
            case "bal":
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Balance.Message").replace(
                        "%balance%", NumberUtil.formatNumberByDecimal(balance)));
                return;
            case "deposit": {
                if (args.length == 1) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short3.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }

                // Parse the amount of money
                double amt;
                try {
                    amt = Double.parseDouble(args[1]);

                    // Make sure the amount is positive
                    if (amt <= 0) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short5.Message"));
                        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                        return;
                    }

                    // If decimals aren't allowed, check for them
                    if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
                        int intAmt = (int) amt;
                        if (intAmt != amt) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short6.Message"));
                            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                            return;
                        }
                    }
                } catch (NumberFormatException ex) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short4.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }

                if (!EconomyManager.hasBalance(player, amt)) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }
                EconomyManager.withdrawBalance(player, amt);
                island.addToBank(amt);
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Deposit.Message").replace(
                        "%amount%", NumberUtil.formatNumberByDecimal(amt)));
                return;
            }
            case "withdraw": {
                if (args.length == 1) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short3.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }

                // Parse the amount of money
                double amt;
                try {
                    amt = Double.parseDouble(args[1]);

                    // Make sure the amount is positive
                    if (amt <= 0) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short5.Message"));
                        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                        return;
                    }

                    // If decimals aren't allowed, check for them
                    if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Bank.AllowDecimals")) {
                        int intAmt = (int) amt;
                        if (intAmt != amt) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short6.Message"));
                            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                            return;
                        }
                    }
                } catch (NumberFormatException ex) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short4.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }

                if (amt > balance) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short2.Message"));
                    return;
                }
                EconomyManager.deposit(player, amt);
                island.removeFromBank(amt);
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Withdraw.Message").replace(
                        "%amount%", NumberUtil.formatNumberByDecimal(amt)));
                return;
            }
        }

        messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Invalid.Message"));
        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Bank.Info.Message";
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
