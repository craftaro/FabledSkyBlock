package me.goodandevil.skyblock.command.commands.island;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class UnlockCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        EconomyManager economyManager = skyblock.getEconomyManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length != 1) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Invalid.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        String type = WordUtils.capitalize(args[0].toLowerCase());

        if (!type.equals("Nether") && !type.equals("End")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Invalid.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Owner.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        Config islandData = fileManager
                .getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                        island.getOwnerUUID().toString() + ".yml"));
        FileConfiguration configLoadIslandData = islandData.getFileConfiguration();
        double price = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                .getFileConfiguration().getDouble("Island.World." + type + ".UnlockPrice");
        boolean unlocked = configLoadIslandData.getBoolean("Unlocked." + type);

        if (unlocked) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Unlocked.Message").replace(
                    "%type%", type));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        if (!economyManager.hasBalance(player, price)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Money.Message").replace(
                    "%cost%", String.valueOf(price)));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
        configLoadIslandData.set("Unlocked." + type, true);
        economyManager.withdraw(player, price);

        messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Finish.Message").replace(
                "%type%", type));
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "unlock";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Unlock.Info.Message";
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
