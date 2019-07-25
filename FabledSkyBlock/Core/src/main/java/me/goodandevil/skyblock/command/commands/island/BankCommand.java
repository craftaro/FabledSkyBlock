package me.goodandevil.skyblock.command.commands.island;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BankCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        EconomyManager economyManager = skyblock.getEconomyManager();
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

                if (!economyManager.hasBalance(player, amt)) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }
                economyManager.withdraw(player, amt);
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
                economyManager.deposit(player, amt);
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
