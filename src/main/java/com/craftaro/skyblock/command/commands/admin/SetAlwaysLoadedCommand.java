package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class SetAlwaysLoadedCommand extends SubCommand {
    public SetAlwaysLoadedCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        FileManager fileManager = this.plugin.getFileManager();
        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        MessageManager messageManager = this.plugin.getMessageManager();

        if (args.length == 0) {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetAlwaysLoaded.No-Player-Input.Message"));
            return;
        }

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        if (args.length == 1) {
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            UUID islandOwnerUUID;

            if (targetPlayer == null) {
                OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                islandOwnerUUID = targetPlayerOffline.getOwner();
            } else {
                islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
            }

            if (islandManager.containsIsland(islandOwnerUUID)) {
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));

                if (island.isAlwaysLoaded()) {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetAlwaysLoaded.IsOff.Message"));
                    soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);

                    island.setAlwaysLoaded(false);
                } else {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetAlwaysLoaded.IsOn.Message"));
                    soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);

                    island.setAlwaysLoaded(true);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "setalwaysloaded";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.SetAlwaysLoaded.Info.Message";
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
