package com.songoda.skyblock.command.commands.admin;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.version.Sounds;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class DeleteCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (args.length == 1) {
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            UUID targetPlayerUUID;
            String targetPlayerName;

            if (targetPlayer == null) {
                OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                targetPlayerUUID = targetPlayerOffline.getUniqueId();
                targetPlayerName = targetPlayerOffline.getName();
            } else {
                targetPlayerUUID = targetPlayer.getUniqueId();
                targetPlayerName = targetPlayer.getName();
            }

            if (targetPlayerUUID == null || !islandManager.isIslandExist(targetPlayerUUID)) {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.Delete.Owner.Message"));
                soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            } else {
                Island island = islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));
                Location spawnLocation = LocationUtil.getSpawnLocation();

                if (spawnLocation != null && islandManager.isLocationAtIsland(island, spawnLocation)) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Delete.Spawn.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                    islandManager.unloadIsland(island, null);

                    return;
                }

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (island.hasRole(IslandRole.Member, all.getUniqueId())
                            || island.hasRole(IslandRole.Operator, all.getUniqueId())) {
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Command.Island.Confirmation.Deletion.Broadcast.Message")));
                        soundManager.playSound(all, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
                    }
                }

                island.setDeleted(true);
                islandManager.deleteIsland(island);

                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.Delete.Deleted.Message").replace("%player",
                                targetPlayerName));
                soundManager.playSound(sender, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Delete.Invalid.Message"));
            soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Delete.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"remove", "disband"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
