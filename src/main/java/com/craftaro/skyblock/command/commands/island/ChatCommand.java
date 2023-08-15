package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.api.event.player.PlayerIslandChatEvent;
import com.craftaro.skyblock.api.event.player.PlayerIslandChatSwitchEvent;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
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

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        PlayerData playerData = playerDataManager.getPlayerData(player);
        if (args.length == 0) {
            if (playerData.isChat() && island != null) {
                Bukkit.getServer().getPluginManager()
                        .callEvent(new PlayerIslandChatSwitchEvent(player, island.getAPIWrapper(), false));
                playerData.setChat(false);

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Untoggled.Message"));
                soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
                return;
            }

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Owner.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else if ((island.getRole(IslandRole.MEMBER).size() + island.getRole(IslandRole.OPERATOR).size()) == 0) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Team.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else if ((islandManager.getMembersOnline(island).size() - 1) <= 0) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Offline.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else {
                Bukkit.getServer().getPluginManager()
                        .callEvent(new PlayerIslandChatSwitchEvent(player, island.getAPIWrapper(), true));
                playerData.setChat(true);

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Toggled.Message"));
                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
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
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
