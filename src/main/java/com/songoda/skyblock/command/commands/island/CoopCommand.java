package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.gui.coop.GuiCoop;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandCoop;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
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
    public CoopCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        PermissionManager permissionManager = this.plugin.getPermissionManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        String temp = configLoad.getString("Menu.Coop.Item.Word.Temp");

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if (this.plugin.getConfiguration().getBoolean("Island.Coop.Enable")) {
            if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                    || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    && permissionManager.hasPermission(island, "CoopPlayers", IslandRole.OPERATOR))) {
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
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Coop.Yourself.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (island.hasRole(IslandRole.MEMBER, targetPlayerUUID)
                            || island.hasRole(IslandRole.OPERATOR, targetPlayerUUID)
                            || island.hasRole(IslandRole.OWNER, targetPlayerUUID)) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Member.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (island.getBan().isBanned(targetPlayerUUID)) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Banned.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (island.isCoopPlayer(targetPlayerUUID)) {
                        if (targetPlayer != null) {
                            if (islandManager.getVisitorsAtIsland(island).contains(targetPlayerUUID)) {
                                if (!(island.getStatus() == IslandStatus.OPEN ||
                                        (island.getStatus() == IslandStatus.WHITELISTED && island.isPlayerWhitelisted(player)))) {
                                    LocationUtil.teleportPlayerToSpawn(targetPlayer);

                                    messageManager.sendMessage(targetPlayer,
                                            configLoad.getString("Command.Island.Coop.Removed.Target.Message"));
                                    soundManager.playSound(targetPlayer, XSound.ENTITY_IRON_GOLEM_ATTACK);
                                }
                            }
                        }

                        island.removeCoopPlayer(targetPlayerUUID);

                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Removed.Sender.Message").replace("%player", targetPlayerName));
                        soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
                    } else {
                        IslandCoop type = IslandCoop.NORMAL;
                        if (args.length == 2 && args[1].equalsIgnoreCase(temp)) {
                            type = IslandCoop.TEMP;
                        }

                        island.addCoopPlayer(targetPlayerUUID, type);

                        messageManager.sendMessage(player, configLoad.getString(type == IslandCoop.TEMP ? "Command.Island.Coop.AddedTemp.Message" : "Command.Island.Coop.Added.Message")
                                .replace("%player", targetPlayerName));

                        if (targetPlayer != null) {
                            messageManager.sendMessage(targetPlayer, configLoad.getString(type == IslandCoop.TEMP ? "Command.Island.Coop.AddedTempTarget.Message" : "Command.Island.Coop.AddedTarget.Message")
                                    .replace("%player", player.getName()));
                        }

                        soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                    }

                    return;
                } else if (args.length != 0) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Invalid.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                }

                this.plugin.getGuiManager().showGUI(player, new GuiCoop(this.plugin, island, null));
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Permission.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Disabled.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
