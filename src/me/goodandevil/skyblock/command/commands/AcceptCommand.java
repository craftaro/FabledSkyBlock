package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.events.IslandJoinEvent;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class AcceptCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public AcceptCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		PlayerData playerData = playerDataManager.getPlayerData(player);
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			InviteManager inviteManager = plugin.getInviteManager();
			
			if (inviteManager.hasInvite(player.getUniqueId())) {
				Invite invite = inviteManager.getInvite(player.getUniqueId());
				String playerName = args[0];
				
				if (invite.getSenderName().equalsIgnoreCase(playerName)) {
					inviteManager.removeInvite(player.getUniqueId());
					
					if (islandManager.hasIsland(player)) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Owner.Message")));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else {
						boolean unloadIsland = false;
						Island island;
						
						if (islandManager.containsIsland(invite.getOwnerUUID())) {
							island = islandManager.getIsland(invite.getOwnerUUID());
						} else {
							islandManager.loadIsland(invite.getOwnerUUID());
							island = islandManager.getIsland(invite.getOwnerUUID());
							unloadIsland = true;
						}
						
						IslandJoinEvent islandJoinEvent = new IslandJoinEvent(player, island);
						Bukkit.getServer().getPluginManager().callEvent(islandJoinEvent);
						
						if (islandJoinEvent.isCancelled()) {
							if (unloadIsland) {
								islandManager.unloadIsland(invite.getOwnerUUID());
							}
						} else {
							Player targetPlayer = Bukkit.getServer().getPlayer(invite.getSenderUUID());
							
							if (targetPlayer != null) {
								targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Accepted.Target.Message").replace("%player", player.getName())));
								soundManager.playSound(targetPlayer, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
							}
							
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Accepted.Sender.Message").replace("%player", invite.getSenderName())));
							soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
							
							playerData.setPlaytime(0);
							playerData.setOwner(invite.getOwnerUUID());
							playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
							playerData.save();
							
							island.setRole(Role.Member, player.getUniqueId());
							island.save();
							
							if ((island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1) >= fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Member.Capacity")) {
								Map<UUID, Invite> invites = inviteManager.getInvites();
								
								for (UUID inviteList : invites.keySet()) {
									Invite targetInvite = invites.get(inviteList);
									
									if (targetInvite.getOwnerUUID().equals(invite.getOwnerUUID())) {
										inviteManager.removeInvite(inviteList);
										
										Player targetInvitePlayer = Bukkit.getServer().getPlayer(inviteList);
										
										if (targetInvitePlayer != null) {
											targetInvitePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Capacity.Broadcast.Message").replace("%player", targetInvite.getSenderName())));
											soundManager.playSound(targetInvitePlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
										}
									}
								}
							}
							
							plugin.getVisitManager().getIsland(invite.getOwnerUUID()).removeVoter(player.getUniqueId());
							
							for (Player all : Bukkit.getOnlinePlayers()) {
								if (!all.getUniqueId().equals(player.getUniqueId())) {
									if (playerDataManager.hasPlayerData(all)) {
										playerData = playerDataManager.getPlayerData(all);
										
										if (playerData.getOwner() != null && playerData.getOwner().equals(island.getOwnerUUID())) {
											all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Accepted.Broadcast.Message").replace("%player", player.getName())));
											soundManager.playSound(all, Sounds.FIREWORK_BLAST.bukkitSound(), 1.0F, 1.0F);
											
											if (scoreboardManager != null) {
												if (island.getRole(Role.Member).size() == 1 && island.getRole(Role.Operator).size() == 0) {
													Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
													scoreboard.cancel();
													scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Team.Displayname")));
													
													if (island.getVisitors().size() == 0) {
														scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
													} else {
														scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
													}
													
													Map<String, String> displayVariables = new HashMap<>();
													displayVariables.put("%owner", configLoad.getString("Scoreboard.Island.Team.Word.Owner"));
													displayVariables.put("%operator", configLoad.getString("Scoreboard.Island.Team.Word.Operator"));
													displayVariables.put("%member", configLoad.getString("Scoreboard.Island.Team.Word.Member"));
													
													scoreboard.setDisplayVariables(displayVariables);
													scoreboard.run();
												}
											}
										}
									}
								}
							}
							
							if (scoreboardManager != null) {
								Scoreboard scoreboard = scoreboardManager.getScoreboard(player);
								scoreboard.cancel();
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Team.Displayname")));
								
								if (island.getVisitors().size() == 0) {
									scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
								} else {
									scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
								}
								
								Map<String, String> displayVariables = new HashMap<>();
								displayVariables.put("%owner", configLoad.getString("Scoreboard.Island.Team.Word.Owner"));
								displayVariables.put("%operator", configLoad.getString("Scoreboard.Island.Team.Word.Operator"));
								displayVariables.put("%member", configLoad.getString("Scoreboard.Island.Team.Word.Member"));
								
								scoreboard.setDisplayVariables(displayVariables);
								scoreboard.run();
							}
						}
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Invited.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Invite.Message")));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Invalid.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "accept";
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
