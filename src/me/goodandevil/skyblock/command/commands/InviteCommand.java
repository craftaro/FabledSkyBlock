package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.events.IslandInviteEvent;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class InviteCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public InviteCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			if (islandManager.hasIsland(player)) {
				me.goodandevil.skyblock.island.Island island = islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
				
				if (island.isRole(Role.Owner, player.getUniqueId()) || (island.isRole(Role.Operator, player.getUniqueId()) && island.getSetting(Settings.Role.Operator, "Invite").getStatus())) {
					Config mainConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
					
					if ((island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1) >= mainConfig.getFileConfiguration().getInt("Island.Member.Capacity")) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Capacity.Message")));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else {
						String playerName = args[0];
						
						if (playerName.equalsIgnoreCase(player.getName())) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Yourself.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
							
							if (targetPlayer == null) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Offline.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Yourself.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else if (island.isRole(Role.Member, targetPlayer.getUniqueId()) || island.isRole(Role.Operator, targetPlayer.getUniqueId()) || island.isRole(Role.Owner, targetPlayer.getUniqueId())) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Member.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else if (plugin.getInviteManager().hasInvite(targetPlayer.getUniqueId())) {
								Invite invite = plugin.getInviteManager().getInvite(targetPlayer.getUniqueId());
								
								if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Already.Own.Message")));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Already.Other.Message")));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								}
							} else {
								int respondTime = mainConfig.getFileConfiguration().getInt("Island.Invite.Time");
								
								if (respondTime < 60) {
									player.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Sender.Sent.Message").replace("%player", targetPlayer.getName()).replace("%time", respondTime + " " + configLoad.getString("Command.Island.Invite.Invited.Word.Second"))) + "   ", false, null, null, null).addExtra(new ChatComponent(configLoad.getString("Command.Island.Invite.Invited.Word.Cancel").toUpperCase(), true, ChatColor.RED, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island cancel " + targetPlayer.getName()), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial").replace("%action", configLoad.getString("Command.Island.Invite.Invited.Word.Cancel")))).create()))));
									targetPlayer.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Target.Received.Message").replace("%player", player.getName()).replace("%time", respondTime + " " + configLoad.getString("Command.Island.Invite.Invited.Word.Second"))) + "   ", false, null, null, null).addExtraChatComponent(new ChatComponent(configLoad.getString("Command.Island.Invite.Invited.Word.Accept").toUpperCase(), true, ChatColor.GREEN, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island accept " + player.getName()), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial").replace("%action", configLoad.getString("Command.Island.Invite.Invited.Word.Accept")))).create()))).addExtraChatComponent(new ChatComponent(" | ", false, ChatColor.DARK_GRAY, null, null)).addExtra(new ChatComponent(configLoad.getString("Command.Island.Invite.Invited.Word.Deny").toUpperCase(), true, ChatColor.RED, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island deny " + player.getName()), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial").replace("%action", configLoad.getString("Command.Island.Invite.Invited.Word.Deny")))).create()))));
								} else {
									player.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Sender.Sent.Message").replace("%player", targetPlayer.getName()).replace("%time", respondTime/60 + " " + configLoad.getString("Command.Island.Invite.Invited.Word.Minute"))) + "   ", false, null, null, null).addExtra(new ChatComponent(configLoad.getString("Command.Island.Invite.Invited.Word.Cancel").toUpperCase(), true, ChatColor.RED, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island cancel " + targetPlayer.getName()), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial").replace("%action", configLoad.getString("Command.Island.Invite.Invited.Word.Cancel")))).create()))));
									targetPlayer.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Target.Received.Message").replace("%player", player.getName()).replace("%time", respondTime/60 + " " + configLoad.getString("Command.Island.Invite.Invited.Word.Minute"))) + "   ", false, null, null, null).addExtraChatComponent(new ChatComponent(configLoad.getString("Command.Island.Invite.Invited.Word.Accept").toUpperCase(), true, ChatColor.GREEN, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island accept " + player.getName()), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial").replace("%action", configLoad.getString("Command.Island.Invite.Invited.Word.Accept")))).create()))).addExtraChatComponent(new ChatComponent(" | ", false, ChatColor.DARK_GRAY, null, null)).addExtra(new ChatComponent(configLoad.getString("Command.Island.Invite.Invited.Word.Deny").toUpperCase(), true, ChatColor.RED, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island deny " + player.getName()), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial").replace("%action", configLoad.getString("Command.Island.Invite.Invited.Word.Deny")))).create()))));
								}
								
								Invite invite = plugin.getInviteManager().createInvite(targetPlayer, player, island.getOwnerUUID(), respondTime);
								
								Bukkit.getServer().getPluginManager().callEvent(new IslandInviteEvent(targetPlayer, player, island, invite));
								
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
								soundManager.playSound(targetPlayer, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
							}
						}
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Permission.Message")));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Owner.Message")));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Invalid.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "invite";
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
