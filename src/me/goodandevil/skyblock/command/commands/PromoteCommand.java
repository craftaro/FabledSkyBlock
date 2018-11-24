package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class PromoteCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public PromoteCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		MessageManager messageManager = plugin.getMessageManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		
		PlayerData playerData = playerDataManager.getPlayerData(player);
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			if (islandManager.hasIsland(player)) {
				me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());
				
				if (island.isRole(Role.Owner, player.getUniqueId())) {
					if (Bukkit.getServer().getPlayer(args[0]) == null) {
						OfflinePlayer targetPlayer = new OfflinePlayer(args[0]);
						List<UUID> islandOperators = island.getRole(Role.Operator);
						
						if (targetPlayer.getUniqueId() != null && (island.getRole(Role.Member).contains(targetPlayer.getUniqueId()) || islandOperators.contains(targetPlayer.getUniqueId()))) {
							if (islandOperators.contains(targetPlayer.getUniqueId())) {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Operator.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Promoted.Sender.Message").replace("%player", targetPlayer.getName()));
								
								for (Player all : Bukkit.getOnlinePlayers()) {
									if (islandManager.hasIsland(all)) {
										playerData = playerDataManager.getPlayerData(all);
										
										if (islandManager.getIsland(playerData.getOwner()).isRole(Role.Owner, player.getUniqueId())) {
											if (!all.getUniqueId().equals(player.getUniqueId())) {
												all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Promote.Promoted.Broadcast.Message").replace("%player", targetPlayer.getName())));
											}
											
											soundManager.playSound(all, Sounds.FIREWORK_BLAST.bukkitSound(), 1.0F, 1.0F);
										}
									}
								}
								
								island.removeRole(Role.Member, targetPlayer.getUniqueId());
								island.setRole(Role.Operator, targetPlayer.getUniqueId());
								island.save();
							}
						} else {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Member.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					} else {
						Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						
						if (island.isRole(Role.Member, targetPlayer.getUniqueId()) || island.isRole(Role.Operator, targetPlayer.getUniqueId())) {
							if (island.isRole(Role.Operator, targetPlayer.getUniqueId())) {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Operator.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Promoted.Sender.Message").replace("%player", targetPlayer.getName()));
								messageManager.sendMessage(targetPlayer, configLoad.getString("Command.Island.Promote.Promoted.Target.Message"));
								
								for (Player all : Bukkit.getOnlinePlayers()) {
									if (islandManager.hasIsland(all)) {
										playerData = playerDataManager.getPlayerData(all);
										
										if (islandManager.getIsland(playerData.getOwner()).isRole(Role.Owner, player.getUniqueId())) {
											if (!(all.getUniqueId().equals(player.getUniqueId()) || all.getUniqueId().equals(targetPlayer.getUniqueId()))) {
												all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Promote.Promoted.Broadcast.Message").replace("%player", targetPlayer.getName())));
											}
											
											soundManager.playSound(all, Sounds.FIREWORK_BLAST.bukkitSound(), 1.0F, 1.0F);
										}
									}
								}
								
								island.setRole(Role.Operator, targetPlayer.getUniqueId());
							}
						} else {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Member.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}	
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "promote";
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
