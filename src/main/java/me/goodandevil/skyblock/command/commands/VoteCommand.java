package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.VisitManager;

public class VoteCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public VoteCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		VisitManager visitManager = skyblock.getVisitManager();
		FileManager fileManager = skyblock.getFileManager();
		
		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Disabled.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				
				return;
			}
			
			Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
			UUID islandOwnerUUID;
			String targetPlayerName;
			
			if (targetPlayer == null) {
				OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
				islandOwnerUUID = targetPlayerOffline.getOwner();
				targetPlayerName = targetPlayerOffline.getName();
			} else {
				islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
				targetPlayerName = targetPlayer.getName();
			}
			
			if (islandOwnerUUID == null) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.None.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else if (!visitManager.hasIsland(islandOwnerUUID)) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.Unloaded.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else {
				me.goodandevil.skyblock.visit.Visit visit = visitManager.getIsland(islandOwnerUUID);
    			
    			if (visit.isOpen()) {
    				if (!islandManager.containsIsland(islandOwnerUUID)) {
    					islandManager.loadIsland(islandOwnerUUID);
    				}
    				
    				Island island = islandManager.getIsland(islandOwnerUUID);
    				
    				if (island.isRole(Role.Member, player.getUniqueId()) || island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId())) {
    					messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.Member.Message"));
    					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
    				} else if (playerDataManager.hasPlayerData(player)) {
    					PlayerData playerData = playerDataManager.getPlayerData(player);
    					
    					if (playerData.getIsland() != null && playerData.getIsland().equals(island.getOwnerUUID())) {
	    					List<UUID> islandVotes = visit.getVoters();
	    					
	    					if (islandVotes.contains(player.getUniqueId())) {
	    						visit.removeVoter(player.getUniqueId());
		    					
	    						messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Vote.Removed.Message").replace("%player", targetPlayerName));
		    					soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
	    					} else {
	    						visit.addVoter(player.getUniqueId());
		    					
	    						messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Vote.Added.Message").replace("%player", targetPlayerName));
		    					soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
	    					}
	    				} else {
	    					messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.Location.Message"));
	    					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
	    				}
    					
    					islandManager.unloadIsland(islandOwnerUUID);
    				}
    			} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.Closed.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
    			}
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "vote";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public SubCommand setInfo(String info) {
		this.info = info;
		
		return this;
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
