package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
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

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length != 1) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        String type = WordUtils.capitalize(args[0].toLowerCase());

        if (!type.equals("Nether") && !type.equals("End")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        if (!this.plugin.getConfiguration().getBoolean("Island.World." + type + ".Enable")) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Disabled.Message").replace("%type%", type));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        Island island = islandManager.getIsland(player);
        IslandWorld islandWorld = IslandWorld.valueOf(type.toUpperCase());

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        if (islandManager.isIslandWorldUnlocked(island, islandWorld)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Unlocked.Message").replace("%type%", type));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        double price = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getDouble("Island.World." + islandWorld.getFriendlyName() + ".UnlockPrice");

        if (economy == null || !economy.hasBalance(player, price)) {
            if (economy == null) {
                this.plugin.getLogger().warning("No compatible economy plugin found – Please check your configuration");
            }

            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unlock.Money.Message").replace(
                    "%cost%", NumberUtils.formatNumber(price)));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);
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
