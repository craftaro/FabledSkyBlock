package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.compatibility.CompatibleSound;
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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class DemoteCommand extends SubCommand {
    public DemoteCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Owner.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

                if (targetPlayer == null) {
                    OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
                    Set<UUID> islandMembers = island.getRole(IslandRole.MEMBER);

                    if (offlinePlayer.getUniqueId() != null && (islandMembers.contains(offlinePlayer.getUniqueId())
                            || island.getRole(IslandRole.OPERATOR).contains(offlinePlayer.getUniqueId()))) {
                        if (islandMembers.contains(offlinePlayer.getUniqueId())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Demote.Role.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        } else {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Demote.Demoted.Sender.Message")
                                            .replace("%player", offlinePlayer.getName()));

                            soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                            island.removeRole(IslandRole.OPERATOR, offlinePlayer.getUniqueId());
                            island.setRole(IslandRole.MEMBER, offlinePlayer.getUniqueId());
                            island.save();
                        }
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Demote.Member.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }
                } else {
                    if (island.hasRole(IslandRole.MEMBER, targetPlayer.getUniqueId())
                            || island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())) {
                        if (island.hasRole(IslandRole.MEMBER, targetPlayer.getUniqueId())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Demote.Role.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        } else {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Demote.Demoted.Sender.Message")
                                            .replace("%player", targetPlayer.getName()));
                            messageManager.sendMessage(targetPlayer,
                                    configLoad.getString("Command.Island.Demote.Demoted.Target.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                            soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                            island.removeRole(IslandRole.OPERATOR, targetPlayer.getUniqueId());
                            island.setRole(IslandRole.MEMBER, targetPlayer.getUniqueId());
                            island.save();
                        }
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Promote.Member.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Permission.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "demote";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Demote.Info.Message";
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
