package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.api.event.player.PlayerIslandChatEvent;
import com.songoda.skyblock.api.event.player.PlayerIslandChatSwitchEvent;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ChatCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        PlayerData playerData = playerDataManager.getPlayerData(player);
        if(args.length == 0){
            if (playerData.isChat() && island != null) {
                Bukkit.getServer().getPluginManager()
                        .callEvent(new PlayerIslandChatSwitchEvent(player, island.getAPIWrapper(), false));
                playerData.setChat(false);

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Untoggled.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                return;
            }

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Owner.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else if ((island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size()) == 0) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Team.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else if ((islandManager.getMembersOnline(island).size() - 1) <= 0) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Offline.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else {
                Bukkit.getServer().getPluginManager()
                        .callEvent(new PlayerIslandChatSwitchEvent(player, island.getAPIWrapper(), true));
                playerData.setChat(true);

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Toggled.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
            }
        } else {
            if (playerDataManager.hasPlayerData(player)) {
                if (playerData.getOwner() != null) {
                    island = skyblock.getIslandManager().getIsland(player);
                }

                String islandRole = "";

                if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
                    islandRole = configLoad.getString("Island.Chat.Format.Role.Member");
                } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
                    islandRole = configLoad.getString("Island.Chat.Format.Role.Operator");
                } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                    islandRole = configLoad.getString("Island.Chat.Format.Role.Owner");
                }

                Island finalIsland = island;
                String finalIslandRole = islandRole;
                Bukkit.getScheduler().runTaskAsynchronously(skyblock, new Runnable(){

                    @Override
                    public void run() {
                        PlayerIslandChatEvent islandChatEvent = new PlayerIslandChatEvent(player, finalIsland.getAPIWrapper(),
                                String.join(" ", args), configLoad.getString("Island.Chat.Format.Message"));
                        Bukkit.getServer().getPluginManager().callEvent(islandChatEvent);

                        if (!islandChatEvent.isCancelled()) {
                            for (UUID islandMembersOnlineList : islandManager.getMembersOnline(finalIsland)) {
                                Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
                                String message = ChatColor.translateAlternateColorCodes('&', messageManager.replaceMessage(targetPlayer,
                                        islandChatEvent.getFormat().replace("%role", finalIslandRole).replace("%player", player.getName())))
                                        .replace("%message", islandChatEvent.getMessage());
                                targetPlayer.sendMessage(message);
                            }

                            if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Chat.OutputToConsole")) {
                                messageManager.sendMessage(Bukkit.getConsoleSender(), islandChatEvent.getFormat().replace("%role", finalIslandRole).replace("%player", player.getName())
                                        .replace("%message", islandChatEvent.getMessage()));
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Chat.Info.Message";
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
