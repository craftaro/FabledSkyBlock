package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
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
    public PromoteCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
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

        if (!island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Permission.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            return;
        }
        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
            Set<UUID> islandOperators = island.getRole(IslandRole.OPERATOR);

            if (offlinePlayer.getUniqueId() != null
                    && (island.getRole(IslandRole.MEMBER).contains(offlinePlayer.getUniqueId())
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
                            if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                    || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                    || island.hasRole(IslandRole.OWNER, all.getUniqueId())) {
                                all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        configLoad
                                                .getString("Command.Island.Promote.Promoted.Broadcast.Message")
                                                .replace("%player", offlinePlayer.getName())));
                                soundManager.playSound(all, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);
                            }
                        }
                    }

                    island.setRole(IslandRole.OPERATOR, offlinePlayer.getUniqueId());
                }
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Member.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
            return;
        }
        if (island.hasRole(IslandRole.MEMBER, targetPlayer.getUniqueId())
                || island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())) {
            if (island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())) {
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
                        if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                || island.hasRole(IslandRole.OWNER, all.getUniqueId())) {
                            all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    configLoad
                                            .getString("Command.Island.Promote.Promoted.Broadcast.Message")
                                            .replace("%player", targetPlayer.getName())));
                            soundManager.playSound(all, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F, 1.0F);
                        }
                    }
                }

                island.setRole(IslandRole.OPERATOR, targetPlayer.getUniqueId());
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
