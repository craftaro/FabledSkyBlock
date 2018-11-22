package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.biome.BiomeManager;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Quit implements Listener {

	private final Main plugin;
	
 	public Quit(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		IslandManager islandManager = plugin.getIslandManager();
		
		PlayerData playerData = playerDataManager.getPlayerData(player);
		playerData.setLastOnline(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		
		if (islandManager.hasIsland(player)) {
			Island island = islandManager.getIsland(playerData.getOwner());
			
			List<UUID> islandMembersOnline = islandManager.getMembersOnline(island);
			
			if (islandMembersOnline.size() == 1) {
				LevellingManager levellingManager = plugin.getLevellingManager();
				levellingManager.saveLevelling(island.getOwnerUUID());
				levellingManager.unloadLevelling(island.getOwnerUUID());
			} else if (islandMembersOnline.size() == 2) {
				for (UUID islandMembersOnlineList : islandMembersOnline) {
					if (!islandMembersOnlineList.equals(player.getUniqueId())) {
						Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
						PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
						
						if (targetPlayerData.isChat()) {
							targetPlayerData.setChat(false);
							targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Chat.Untoggled.Message")));	
						}
					}
				}
			}
		}
		
		islandManager.unloadIsland(player.getUniqueId());
		
		playerDataManager.savePlayerData(player);
		playerDataManager.unloadPlayerData(player);
		
		UUID islandOwnerUUID = playerData.getIsland();
		
		if (islandOwnerUUID != null && islandManager.containsIsland(islandOwnerUUID)) {
			islandManager.unloadIsland(islandOwnerUUID);
		}
		
		InviteManager inviteManager = plugin.getInviteManager();
		
		if (inviteManager.hasInvite(player.getUniqueId())) {
			Invite invite = inviteManager.getInvite(player.getUniqueId());
			Player targetPlayer = Bukkit.getServer().getPlayer(invite.getOwnerUUID());
			
			if (targetPlayer != null) {
				targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Command.Island.Invite.Invited.Sender.Disconnected.Message").replace("%player", player.getName())));
				plugin.getSoundManager().playSound(targetPlayer, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
			
			inviteManager.removeInvite(player.getUniqueId());	
		}
		
		BiomeManager biomeManager = plugin.getBiomeManager();
		biomeManager.savePlayer(player);
		biomeManager.unloadPlayer(player);
		
		/*CreationManager creationManager = plugin.getCreationManager();
		creationManager.savePlayer(player);
		creationManager.unloadPlayer(player);*/
	}
}
