package com.songoda.skyblock.command.commands.admin;

import com.craftaro.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class LevelScanCommand extends SubCommand {
    public LevelScanCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        this.onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        this.onCommand(sender, args);
    }

    private void onCommand(CommandSender sender, String[] args) {
        IslandLevelManager levellingManager = this.plugin.getLevellingManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 0) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.LevelScan.Invalid.Message"));
            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        Island island = islandManager.getIsland(offlinePlayer);

        if (island == null) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.LevelScan.NoIsland.Message"));
            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        levellingManager.startScan(sender instanceof Player ? (Player) sender : null, island);

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.LevelScan.Started.Message"));
        soundManager.playSound(sender, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);
    }

    @Override
    public String getName() {
        return "levelscan";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.LevelScan.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"scanlevel"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
