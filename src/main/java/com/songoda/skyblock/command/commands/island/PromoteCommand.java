package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class PromoteCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length != 1) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            return;
        }

        if (!island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Permission.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            return;
        }
        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
            Set<UUID> islandOperators = island.getRole(IslandRole.Operator);

            if (offlinePlayer.getUniqueId() != null
                    && (island.getRole(IslandRole.Member).contains(offlinePlayer.getUniqueId())
                    || islandOperators.contains(offlinePlayer.getUniqueId()))) {
                if (islandOperators.contains(offlinePlayer.getUniqueId())) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Promote.Operator.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Promote.Promoted.Sender.Message")
                                    .replace("%player", offlinePlayer.getName()));
                    soundManager.playSound(player, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (!all.getUniqueId().equals(player.getUniqueId())) {
                            if (island.hasRole(IslandRole.Member, player.getUniqueId())
                                    || island.hasRole(IslandRole.Operator, all.getUniqueId())
                                    || island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                                all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        configLoad
                                                .getString("Command.Island.Promote.Promoted.Broadcast.Message")
                                                .replace("%player", offlinePlayer.getName())));
                                soundManager.playSound(all, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);
                            }
                        }
                    }

                    island.setRole(IslandRole.Operator, offlinePlayer.getUniqueId());
                }
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Member.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
            return;
        }
        if (island.hasRole(IslandRole.Member, targetPlayer.getUniqueId())
                || island.hasRole(IslandRole.Operator, targetPlayer.getUniqueId())) {
            if (island.hasRole(IslandRole.Operator, targetPlayer.getUniqueId())) {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Operator.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Promoted.Sender.Message")
                                .replace("%player", targetPlayer.getName()));
                soundManager.playSound(player, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);

                messageManager.sendMessage(targetPlayer,
                        configLoad.getString("Command.Island.Promote.Promoted.Target.Message"));
                soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.getUniqueId().equals(player.getUniqueId())) {
                        if (island.hasRole(IslandRole.Member, player.getUniqueId())
                                || island.hasRole(IslandRole.Operator, all.getUniqueId())
                                || island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                            all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    configLoad
                                            .getString("Command.Island.Promote.Promoted.Broadcast.Message")
                                            .replace("%player", targetPlayer.getName())));
                            soundManager.playSound(all, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);
                        }
                    }
                }

                island.setRole(IslandRole.Operator, targetPlayer.getUniqueId());
            }
        } else {
            messageManager.sendMessage(player,
                    configLoad.getString("Command.Island.Promote.Member.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "promote";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Promote.Info.Message";
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
