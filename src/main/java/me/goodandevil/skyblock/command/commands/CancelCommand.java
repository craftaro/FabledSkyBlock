package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class CancelCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public CancelCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		InviteManager inviteManager = skyblock.getInviteManager();
		SoundManager soundManager = skyblock.getSoundManager();
		
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			if (islandManager.hasIsland(player)) {
				me.goodandevil.skyblock.island.Island island = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
				
				if (island.hasRole(IslandRole.Owner, player.getUniqueId()) || island.hasRole(IslandRole.Operator, player.getUniqueId())) {
					String playerName = args[0];
					Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
					
					if (targetPlayer == null) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Offline.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (island.hasRole(IslandRole.Member, targetPlayer.getUniqueId()) || island.hasRole(IslandRole.Operator, targetPlayer.getUniqueId()) || island.hasRole(IslandRole.Owner, targetPlayer.getUniqueId())) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Member.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (inviteManager.hasInvite(targetPlayer.getUniqueId())) {
						Invite invite = inviteManager.getInvite(targetPlayer.getUniqueId());
						
						if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
							inviteManager.removeInvite(targetPlayer.getUniqueId());
							
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Cancelled.Message").replace("%player", targetPlayer.getName()));
							soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
						} else {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Invited.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					} else {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Invited.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Owner.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Cancel.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}
	
	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "cancel";
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
	public String[] getArguments() {
		return new String[0];
	}
	
	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
