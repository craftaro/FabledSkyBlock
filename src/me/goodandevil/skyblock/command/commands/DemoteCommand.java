package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class DemoteCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public DemoteCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		MessageManager messageManager = plugin.getMessageManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		
		PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			if (islandManager.hasIsland(player)) {
				me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());
				
				if (island.isRole(Role.Owner, player.getUniqueId())) {
					if (Bukkit.getServer().getPlayer(args[0]) == null) {
						OfflinePlayer targetPlayer = new OfflinePlayer(args[0]);
						List<UUID> islandMembers = island.getRole(Role.Member);
						
						if (targetPlayer.getUniqueId() != null && (islandMembers.contains(targetPlayer.getUniqueId()) || island.getRole(Role.Operator).contains(targetPlayer.getUniqueId()))) {
							if (islandMembers.contains(targetPlayer.getUniqueId())) {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Role.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Promoted.Sender.Message").replace("%player", targetPlayer.getName()));
								soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
								
								island.removeRole(Role.Operator, targetPlayer.getUniqueId());
								island.setRole(Role.Member, targetPlayer.getUniqueId());
								island.save();
							}
						} else {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Member.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					} else {
						Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						
						if (island.isRole(Role.Member, targetPlayer.getUniqueId()) || island.isRole(Role.Operator, targetPlayer.getUniqueId())) {
							if (island.isRole(Role.Member, targetPlayer.getUniqueId())) {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Role.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Demoted.Sender.Message").replace("%player", targetPlayer.getName()));
								messageManager.sendMessage(targetPlayer, configLoad.getString("Command.Island.Demote.Demoted.Target.Message"));
								soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
								soundManager.playSound(targetPlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
								
								island.removeRole(Role.Operator, targetPlayer.getUniqueId());
								island.setRole(Role.Member, targetPlayer.getUniqueId());
								island.save();
							}
						} else {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Member.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}	
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Demote.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "demote";
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
