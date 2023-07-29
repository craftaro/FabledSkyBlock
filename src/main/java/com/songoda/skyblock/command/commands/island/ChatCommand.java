package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.player.PlayerIslandChatEvent;
import com.songoda.skyblock.api.event.player.PlayerIslandChatSwitchEvent;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ChatCommand extends SubCommand {
    public ChatCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        PlayerData playerData = playerDataManager.getPlayerData(player);
        if (args.length == 0) {
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
            } else if ((island.getRole(IslandRole.MEMBER).size() + island.getRole(IslandRole.OPERATOR).size()) == 0) {
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
                    island = this.plugin.getIslandManager().getIsland(player);
                }

                if (island != null) {
                    Island finalIsland = island;
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        PlayerIslandChatEvent islandChatEvent = new PlayerIslandChatEvent(player, finalIsland.getAPIWrapper(),
                                String.join(" ", args), configLoad.getString("Island.Chat.Format.Message"));
                        Bukkit.getServer().getPluginManager().callEvent(islandChatEvent);
                    });
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Owner.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                }
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
