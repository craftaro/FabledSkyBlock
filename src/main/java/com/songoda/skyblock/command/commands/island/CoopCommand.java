package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.gui.coop.GuiCoop;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class CoopCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        String temp = configLoad.getString("Menu.Coop.Item.Word.Temp");

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Coop.Enable")) {
            if (island.hasRole(IslandRole.Owner, player.getUniqueId())
                    || (island.hasRole(IslandRole.Operator, player.getUniqueId())
                    && permissionManager.hasPermission(island, "CoopPlayers", IslandRole.Operator))) {
                if (args.length == 1 || args.length == 2) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

                    UUID targetPlayerUUID;
                    String targetPlayerName;

                    if (targetPlayer == null) {
                        OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
                        targetPlayerUUID = offlinePlayer.getUniqueId();
                        targetPlayerName = offlinePlayer.getName();

                        if (targetPlayerUUID != null && !Bukkit.getOfflinePlayer(targetPlayerUUID).hasPlayedBefore()) {
                            targetPlayerUUID = null;
                            targetPlayerName = null;
                        }
                    } else {
                        targetPlayerUUID = targetPlayer.getUniqueId();
                        targetPlayerName = targetPlayer.getName();
                    }

                    if (targetPlayerUUID == null) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Found.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Coop.Yourself.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (island.hasRole(IslandRole.Member, targetPlayerUUID)
                            || island.hasRole(IslandRole.Operator, targetPlayerUUID)
                            || island.hasRole(IslandRole.Owner, targetPlayerUUID)) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Member.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (island.getBan().isBanned(targetPlayerUUID)) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Banned.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (island.isCoopPlayer(targetPlayerUUID)) {
                        if (targetPlayer != null) {
                            if (islandManager.getVisitorsAtIsland(island).contains(targetPlayerUUID)) {
                                if (!(island.getStatus().equals(IslandStatus.OPEN) ||
                                        (island.getStatus().equals(IslandStatus.WHITELISTED) && island.isPlayerWhitelisted(player)))) {
                                    LocationUtil.teleportPlayerToSpawn(targetPlayer);

                                    messageManager.sendMessage(targetPlayer,
                                            configLoad.getString("Command.Island.Coop.Removed.Target.Message"));
                                    soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F,
                                            1.0F);
                                }
                            }
                        }

                        island.removeCoopPlayer(targetPlayerUUID);

                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Coop.Removed.Sender.Message").replace("%player",
                                        targetPlayerName));
                        soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                    } else {
                        IslandCoop type = IslandCoop.NORMAL;
                        if (args.length == 2 && args[1].equalsIgnoreCase(temp))
                            type = IslandCoop.TEMP;

                        island.addCoopPlayer(targetPlayerUUID, type);

                        messageManager.sendMessage(player, configLoad.getString(type == IslandCoop.TEMP ? "Command.Island.Coop.AddedTemp.Message" : "Command.Island.Coop.Added.Message")
                                .replace("%player", targetPlayerName));

                        if (targetPlayer != null) {
                            messageManager.sendMessage(targetPlayer, configLoad.getString(type == IslandCoop.TEMP ? "Command.Island.Coop.AddedTempTarget.Message" : "Command.Island.Coop.AddedTarget.Message")
                                    .replace("%player", player.getName()));
                        }

                        soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                    }

                    return;
                } else if (args.length != 0) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Invalid.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                }
    
                plugin.getGuiManager().showGUI(player, new GuiCoop(plugin, island, null));
                soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Permission.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Disabled.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "coop";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Coop.Info.Message";
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
