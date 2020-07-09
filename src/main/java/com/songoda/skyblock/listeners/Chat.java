package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.player.PlayerIslandChatEvent;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.PlaceholderManager;
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

import java.io.File;
import java.util.UUID;

public class Chat implements Listener {

    private final SkyBlock plugin;

    public Chat(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        FileManager fileManager = plugin.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = null;

            if (playerData.getOwner() != null) {
                island = plugin.getIslandManager().getIsland(player);
            }

            String messageFormat = event.getFormat();

            for (String placeholderList : placeholderManager.getPlaceholders()) {
                String placeholder = "{" + placeholderList + "}";

                if (messageFormat.contains(placeholder)) {
                    messageFormat = messageFormat.replace(placeholder,
                            placeholderManager.getPlaceholder(player, placeholderList));
                }
            }

            event.setFormat(messageFormat);

            if (playerData.isChat()) {
                event.setCancelled(true);

                Config language = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
                FileConfiguration languageLoad = language.getFileConfiguration();

                String islandRole = "";

                if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
                    islandRole = languageLoad.getString("Island.Chat.Format.Role.Member");
                } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
                    islandRole = languageLoad.getString("Island.Chat.Format.Role.Operator");
                } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                    islandRole = languageLoad.getString("Island.Chat.Format.Role.Owner");
                }

                PlayerIslandChatEvent islandChatEvent = new PlayerIslandChatEvent(player, island.getAPIWrapper(),
                        event.getMessage(), languageLoad.getString("Island.Chat.Format.Message"));
                Bukkit.getServer().getPluginManager().callEvent(islandChatEvent);

                if (!islandChatEvent.isCancelled()) {
                    for (UUID islandMembersOnlineList : islandManager.getMembersOnline(island)) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
                        String message = ChatColor.translateAlternateColorCodes('&', messageManager.replaceMessage(targetPlayer,
                                islandChatEvent.getFormat().replace("%role", islandRole).replace("%player", player.getName())))
                                .replace("%message", islandChatEvent.getMessage());
                        messageManager.sendMessage(targetPlayer, message);
                    }
                    
                    for(Player targetPlayer : Bukkit.getServer().getOnlinePlayers()){
                        if(targetPlayer.hasPermission("fabledskyblock.admin.chatspy")) {
                            PlayerData pd = playerDataManager.getPlayerData(targetPlayer);
                            if(pd != null && pd.isChatSpy() && (pd.isGlobalChatSpy() || pd.isChatSpyIsland(island))) {
                                String message = ChatColor.translateAlternateColorCodes('&', messageManager.replaceMessage(targetPlayer,
                                        islandChatEvent.getFormat().replace("%role", islandRole).replace("%player", player.getName())))
                                        .replace("%islandOwner", new OfflinePlayer(island.getOwnerUUID()).getName())
                                        .replace("%message", islandChatEvent.getMessage());
                                messageManager.sendMessage(targetPlayer, message);
                            }
                        }
                    }

                    if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Chat.OutputToConsole")) {
                        messageManager.sendMessage(Bukkit.getConsoleSender(), islandChatEvent.getFormat().replace("%role", islandRole).replace("%player", player.getName())
                                .replace("%message", islandChatEvent.getMessage()));
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onIslandChat(PlayerIslandChatEvent event) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        FileManager fileManager = plugin.getFileManager();
        
        Island island = event.getIsland().getIsland();
        Player player = event.getPlayer();
    
        Config language = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration languageLoad = language.getFileConfiguration();
    
        String islandRole = null;
    
        if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
            islandRole = languageLoad.getString("Island.Chat.Format.Role.Member");
        } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
            islandRole = languageLoad.getString("Island.Chat.Format.Role.Operator");
        } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            islandRole = languageLoad.getString("Island.Chat.Format.Role.Owner");
        }
        if(islandRole == null) {
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
        for(Player targetPlayer : Bukkit.getServer().getOnlinePlayers()){
            if(!targetPlayer.equals(event.getPlayer()) &&
                    !islandManager.getMembersOnline(island).contains(targetPlayer.getUniqueId()) &&
                    targetPlayer.hasPermission("fabledskyblock.admin.chatspy")) {
                PlayerData pd = playerDataManager.getPlayerData(targetPlayer);
                if(pd != null && pd.isChatSpy() && (pd.isGlobalChatSpy() || pd.isChatSpyIsland(island))) {
                    String message = ChatColor.translateAlternateColorCodes('&', messageManager.replaceMessage(targetPlayer,
                            languageLoad.getString("Island.Chat.Spy.Format.Message").replace("%role", islandRole).replace("%player", player.getName())))
                            .replace("%islandOwner", new OfflinePlayer(island.getOwnerUUID()).getName())
                            .replace("%message", event.getMessage());
                    messageManager.sendMessage(targetPlayer, message);
                }
            }
        }
    
        if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Chat.OutputToConsole")) {
            messageManager.sendMessage(Bukkit.getConsoleSender(), event.getFormat().replace("%role", islandRole).replace("%player", player.getName())
                    .replace("%message", event.getMessage()));
        }
    }
}
