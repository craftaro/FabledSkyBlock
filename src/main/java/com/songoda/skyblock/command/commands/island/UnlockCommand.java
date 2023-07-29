package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.compatibility.CompatibleSound;
import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class UnlockCommand extends SubCommand {
    public UnlockCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length != 1) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        String type = WordUtils.capitalize(args[0].toLowerCase());

        if (!type.equals("Nether") && !type.equals("End")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        if (!this.plugin.getConfiguration().getBoolean("Island.World." + type + ".Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Disabled.Message").replace("%type%", type));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        Island island = islandManager.getIsland(player);
        IslandWorld islandWorld = IslandWorld.valueOf(type);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        if (islandManager.isIslandWorldUnlocked(island, islandWorld)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Unlocked.Message").replace("%type%", type));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        double price = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getDouble("Island.World." + islandWorld.name() + ".UnlockPrice");

        if (!economy.hasBalance(player, price)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Money.Message").replace(
                    "%cost%", NumberUtils.formatNumber(price)));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);
        economy.withdrawBalance(player, price);

        islandManager.unlockIslandWorld(island, islandWorld);

        messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Finish.Message").replace("%type%", type));
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
        return new String[]{"Nether", "End"};
    }
}
