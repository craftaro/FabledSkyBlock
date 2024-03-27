package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
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

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length != 1) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            return;
        }

        if (!island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Permission.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            return;
        }
        Player targetPlayer = Bukkit.getPlayerExact(args[0]);

        if (targetPlayer == null) {
            OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
            Set<UUID> islandOperators = island.getRole(IslandRole.OPERATOR);

            if (offlinePlayer.getUniqueId() != null
                    && (island.getRole(IslandRole.MEMBER).contains(offlinePlayer.getUniqueId())
                    || islandOperators.contains(offlinePlayer.getUniqueId()))) {
                if (islandOperators.contains(offlinePlayer.getUniqueId())) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Promote.Operator.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Promote.Promoted.Sender.Message")
                                    .replace("%player", offlinePlayer.getName()));
                    soundManager.playSound(player, XSound.ENTITY_FIREWORK_ROCKET_BLAST);

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (!all.getUniqueId().equals(player.getUniqueId())) {
                            if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                    || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                    || island.hasRole(IslandRole.OWNER, all.getUniqueId())) {
                                all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        configLoad
                                                .getString("Command.Island.Promote.Promoted.Broadcast.Message")
                                                .replace("%player", offlinePlayer.getName())));
                                soundManager.playSound(all, XSound.ENTITY_FIREWORK_ROCKET_BLAST);
                            }
                        }
                    }

                    island.setRole(IslandRole.OPERATOR, offlinePlayer.getUniqueId());
                }
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Member.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
            return;
        }
        if (island.hasRole(IslandRole.MEMBER, targetPlayer.getUniqueId())
                || island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())) {
            if (island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())) {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Operator.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Promote.Promoted.Sender.Message")
                                .replace("%player", targetPlayer.getName()));
                soundManager.playSound(player, XSound.ENTITY_FIREWORK_ROCKET_BLAST);

                messageManager.sendMessage(targetPlayer,
                        configLoad.getString("Command.Island.Promote.Promoted.Target.Message"));
                soundManager.playSound(targetPlayer, XSound.ENTITY_FIREWORK_ROCKET_BLAST);

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.getUniqueId().equals(player.getUniqueId())) {
                        if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                || island.hasRole(IslandRole.OWNER, all.getUniqueId())) {
                            all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    configLoad
                                            .getString("Command.Island.Promote.Promoted.Broadcast.Message")
                                            .replace("%player", targetPlayer.getName())));
                            soundManager.playSound(all, XSound.ENTITY_FIREWORK_ROCKET_BLAST);
                        }
                    }
                }

                island.setRole(IslandRole.OPERATOR, targetPlayer.getUniqueId());
            }
        } else {
            messageManager.sendMessage(player,
                    configLoad.getString("Command.Island.Promote.Member.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
