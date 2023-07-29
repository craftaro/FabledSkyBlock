package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.player.PlayerIslandChatEvent;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListeners implements Listener {
    private final SkyBlock plugin;

    public ChatListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = null;

            if (playerData.getOwner() != null) {
                island = this.plugin.getIslandManager().getIsland(player);
            }

            if (playerData.isChat() && island != null) {
                event.setCancelled(true);
                FileConfiguration languageLoad = this.plugin.getLanguage();

                PlayerIslandChatEvent islandChatEvent = new PlayerIslandChatEvent(player, island.getAPIWrapper(),
                        event.getMessage(), languageLoad.getString("Island.Chat.Format.Message"));
                Bukkit.getServer().getPluginManager().callEvent(islandChatEvent);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandChat(PlayerIslandChatEvent event) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();

        Island island = event.getIsland().getIsland();
        Player player = event.getPlayer();

        FileConfiguration languageLoad = this.plugin.getLanguage();

        String islandRole = null;

        if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())) {
            islandRole = languageLoad.getString("Island.Chat.Format.Role.Member");
        } else if (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())) {
            islandRole = languageLoad.getString("Island.Chat.Format.Role.Operator");
        } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            islandRole = languageLoad.getString("Island.Chat.Format.Role.Owner");
        }
        if (islandRole == null) {
            islandRole = "";
        }

        for (UUID islandMembersOnlineList : islandManager.getMembersOnline(island)) {
            Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
            String message = ChatColor.translateAlternateColorCodes('&', messageManager.replaceMessage(targetPlayer,
                            event.getFormat().replace("%role", islandRole).replace("%player", player.getName())))
                    .replace("%message", event.getMessage());
            messageManager.sendMessage(targetPlayer, message);
        }

        // Spy
        for (Player targetPlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (!targetPlayer.equals(event.getPlayer()) &&
                    !islandManager.getMembersOnline(island).contains(targetPlayer.getUniqueId()) &&
                    targetPlayer.hasPermission("fabledskyblock.admin.chatspy")) {
                PlayerData pd = playerDataManager.getPlayerData(targetPlayer);
                if (pd != null && pd.isChatSpy() && (pd.isGlobalChatSpy() || pd.isChatSpyIsland(island))) {
                    String message = ChatColor.translateAlternateColorCodes('&', messageManager.replaceMessage(targetPlayer,
                                    languageLoad.getString("Island.Chat.Spy.Format.Message").replace("%role", islandRole).replace("%player", player.getName())))
                            .replace("%islandOwner", new OfflinePlayer(island.getOwnerUUID()).getName())
                            .replace("%message", event.getMessage());
                    messageManager.sendMessage(targetPlayer, message);
                }
            }
        }

        if (this.plugin.getConfiguration().getBoolean("Island.Chat.OutputToConsole")) {
            messageManager.sendMessage(Bukkit.getConsoleSender(), event.getFormat().replace("%role", islandRole).replace("%player", player.getName())
                    .replace("%message", event.getMessage()));
        }
    }
}
