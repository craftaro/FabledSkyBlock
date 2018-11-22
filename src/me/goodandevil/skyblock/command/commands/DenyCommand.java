package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class DenyCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public DenyCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		SoundManager soundManager = plugin.getSoundManager();
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			InviteManager inviteManager = plugin.getInviteManager();
			
			if (inviteManager.hasInvite(player.getUniqueId())) {
				Invite invite = inviteManager.getInvite(player.getUniqueId());
				String playerName = args[0];
				
				if (invite.getSenderName().equalsIgnoreCase(playerName)) {
					Player targetPlayer = Bukkit.getServer().getPlayer(invite.getSenderUUID());
					
					if (targetPlayer != null) {
						targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Denied.Target.Message").replace("%player", player.getName())));
						soundManager.playSound(targetPlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 5.0F, 5.0F);
					}
					
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Denied.Sender.Message").replace("%player", invite.getSenderName())));
					soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 5.0F, 5.0F);
					
					inviteManager.removeInvite(player.getUniqueId());
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Invited.Message")));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Invited.Message")));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Invalid.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "deny";
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
