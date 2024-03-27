package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.ban.Ban;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class BanCommand extends SubCommand {
    public BanCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            MessageManager messageManager = this.plugin.getMessageManager();
            PermissionManager permissionManager = this.plugin.getPermissionManager();
            IslandManager islandManager = this.plugin.getIslandManager();
            SoundManager soundManager = this.plugin.getSoundManager();
            FileManager fileManager = this.plugin.getFileManager();

            FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (args.length == 1) {
                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Owner.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else if (this.plugin.getConfiguration().getBoolean("Island.Visitor.Banning")) {
                    if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                            || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId()) && permissionManager.hasPermission(island, "Ban", IslandRole.OPERATOR))) {
                        Player targetPlayer = Bukkit.getPlayerExact(args[0]);

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

                        if (targetPlayerUUID == null) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Found.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (targetPlayer != null && (targetPlayer.hasPermission("fabledskyblock.bypass.ban") || targetPlayer.isOp())) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Exempt"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Yourself.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (island.hasRole(IslandRole.MEMBER, targetPlayerUUID) || island.hasRole(IslandRole.OPERATOR, targetPlayerUUID)
                                || island.hasRole(IslandRole.OWNER, targetPlayerUUID)) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Member.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (island.getBan().isBanned(targetPlayerUUID)) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Already.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Banned.Sender.Message")
                                    .replace("%player", targetPlayerName));
                            soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);

                            if (island.isCoopPlayer(targetPlayerUUID)) {
                                island.removeCoopPlayer(targetPlayerUUID);
                            }

                            Ban ban = island.getBan();
                            ban.addBan(player.getUniqueId(), targetPlayerUUID);
                            ban.save();

                            if (targetPlayer != null) {
                                if (islandManager.isPlayerAtIsland(island, targetPlayer)) {
                                    messageManager.sendMessage(targetPlayer, configLoad.getString("Command.Island.Ban.Banned.Target.Message")
                                            .replace("%player", player.getName()));
                                    soundManager.playSound(targetPlayer, XSound.ENTITY_IRON_GOLEM_ATTACK);

                                    LocationUtil.teleportPlayerToSpawn(targetPlayer);
                                }
                            }
                        }
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Permission.Message"));
                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Disabled.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Invalid.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        });
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Ban.Info.Message";
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
