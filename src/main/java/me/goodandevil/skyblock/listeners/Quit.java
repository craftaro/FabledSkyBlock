package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Quit implements Listener {

	private final SkyBlock skyblock;

	public Quit(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		CooldownManager cooldownManager = skyblock.getCooldownManager();
		MessageManager messageManager = skyblock.getMessageManager();
		InviteManager inviteManager = skyblock.getInviteManager();
		IslandManager islandManager = skyblock.getIslandManager();

		PlayerData playerData = playerDataManager.getPlayerData(player);

		try {
			playerData.setLastOnline(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		} catch (Exception e) {
		}

		Island island = islandManager.getIsland(player);

		if (island != null) {
			Set<UUID> islandMembersOnline = islandManager.getMembersOnline(island);

			if (islandMembersOnline.size() == 1) {
				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
				cooldownManager.setCooldownPlayer(CooldownType.Levelling, offlinePlayer);
				cooldownManager.removeCooldownPlayer(CooldownType.Levelling, offlinePlayer);

				cooldownManager.setCooldownPlayer(CooldownType.Ownership, offlinePlayer);
				cooldownManager.removeCooldownPlayer(CooldownType.Ownership, offlinePlayer);
			} else if (islandMembersOnline.size() == 2) {
				for (UUID islandMembersOnlineList : islandMembersOnline) {
					if (!islandMembersOnlineList.equals(player.getUniqueId())) {
						Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
						PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);

						if (targetPlayerData.isChat()) {
							targetPlayerData.setChat(false);
							messageManager.sendMessage(targetPlayer,
									skyblock.getFileManager()
											.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
											.getFileConfiguration().getString("Island.Chat.Untoggled.Message"));
						}
					}
				}
			}

			islandManager.unloadIsland(island, player);
		}

		cooldownManager.setCooldownPlayer(CooldownType.Biome, player);
		cooldownManager.removeCooldownPlayer(CooldownType.Biome, player);

		cooldownManager.setCooldownPlayer(CooldownType.Creation, player);
		cooldownManager.removeCooldownPlayer(CooldownType.Creation, player);

		playerDataManager.savePlayerData(player);
		playerDataManager.unloadPlayerData(player);

		if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
				.getBoolean("Island.Coop.Unload")) {
			for (Island islandList : islandManager.getCoopIslands(player)) {
				islandList.removeCoopPlayer(player.getUniqueId());
			}
		}

		if (playerData != null && playerData.getIsland() != null && islandManager.containsIsland(playerData.getIsland())) {
			island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

			if (!island.hasRole(IslandRole.Member, player.getUniqueId())
					&& !island.hasRole(IslandRole.Operator, player.getUniqueId())
					&& !island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				islandManager.unloadIsland(island, null);
			}
		}

		if (inviteManager.hasInvite(player.getUniqueId())) {
			Invite invite = inviteManager.getInvite(player.getUniqueId());
			Player targetPlayer = Bukkit.getServer().getPlayer(invite.getOwnerUUID());

			if (targetPlayer != null) {
				messageManager.sendMessage(targetPlayer,
						skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
								.getFileConfiguration()
								.getString("Command.Island.Invite.Invited.Sender.Disconnected.Message")
								.replace("%player", player.getName()));
				skyblock.getSoundManager().playSound(targetPlayer, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}

			inviteManager.removeInvite(player.getUniqueId());
		}
	}
}
