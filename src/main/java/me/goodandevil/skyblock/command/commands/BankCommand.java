package me.goodandevil.skyblock.command.commands;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BankCommand extends SubCommand {

    private final SkyBlock skyblock;
    private String info;

    public BankCommand(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        EconomyManager economyManager = skyblock.getEconomyManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Invalid.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        String action = WordUtils.capitalize(args[0].toLowerCase());

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

        switch (action) {
            case "Balance":
            case "Bal":
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Balance.Message").replace(
                        "%balance%", String.valueOf(balance)));
                return;
            case "Deposit": {
                double amt = Long.parseLong(args[1]);

                if (!economyManager.hasBalance(player, amt)) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                    return;
                }
                economyManager.withdraw(player, amt);
                island.addToBank(amt);
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Deposit.Message").replace(
                        "%amount%", String.valueOf(amt)));
                return;
            }
            case "Withdraw": {
                double amt = Long.parseLong(args[1]);

                if (amt > balance) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Short2.Message"));
                    return;
                }
                economyManager.deposit(player, amt);
                island.removeFromBank(amt);
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bank.Withdraw.Message").replace(
                        "%amount%", String.valueOf(amt)));
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
    public String getInfo() {
        return info;
    }

    @Override
    public SubCommand setInfo(String info) {
        this.info = info;

        return this;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }

    @Override
    public Type getType() {
        return Type.Default;
    }
}
