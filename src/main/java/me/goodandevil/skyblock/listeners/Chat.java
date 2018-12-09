package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.player.PlayerIslandChatEvent;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.PlaceholderManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class Chat implements Listener {

	private final SkyBlock skyblock;

	public Chat(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		PlaceholderManager placeholderManager = skyblock.getPlaceholderManager();
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);
			Island island = null;

			if (playerData.getOwner() != null) {
				island = skyblock.getIslandManager().getIsland(playerData.getOwner());
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

				Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
				FileConfiguration configLoad = config.getFileConfiguration();

				String islandRole = "";

				if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
					islandRole = configLoad.getString("Island.Chat.Format.Role.Member");
				} else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
					islandRole = configLoad.getString("Island.Chat.Format.Role.Operator");
				} else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
					islandRole = configLoad.getString("Island.Chat.Format.Role.Owner");
				}

				PlayerIslandChatEvent islandChatEvent = new PlayerIslandChatEvent(player, island.getAPIWrapper(),
						event.getMessage(), configLoad.getString("Island.Chat.Format.Message"));
				Bukkit.getServer().getPluginManager().callEvent(islandChatEvent);

				if (!islandChatEvent.isCancelled()) {
					for (UUID islandMembersOnlineList : islandManager.getMembersOnline(island)) {
						Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
						targetPlayer.sendMessage(ChatColor
								.translateAlternateColorCodes('&',
										messageManager.replaceMessage(targetPlayer,
												islandChatEvent.getFormat().replace("%role", islandRole)
														.replace("%player", player.getName())))
								.replace("%message", islandChatEvent.getMessage()));
					}
				}
			}
		}
	}
}
