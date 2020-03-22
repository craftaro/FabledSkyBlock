package com.songoda.skyblock.command.commands.island;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.utils.VaultPermissions;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.Sounds;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class UnlockCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
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

        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.World." + type + ".Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Disabled.Message").replace("%type%", type));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        Island island = islandManager.getIsland(player);
        IslandWorld islandWorld = IslandWorld.valueOf(type);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Owner.Message"));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        if (islandManager.isIslandWorldUnlocked(island, islandWorld)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Unlocked.Message").replace("%type%", type));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        double price = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                .getFileConfiguration().getDouble("Island.World." + islandWorld.name() + ".UnlockPrice");

        if (!EconomyManager.hasBalance(player, price)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Money.Message").replace(
                    "%cost%", NumberUtil.formatNumberByDecimal(price)));
            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
        EconomyManager.withdrawBalance(player, price);

        islandManager.unlockIslandWorld(island, islandWorld);

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
