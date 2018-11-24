package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.confirmation.Confirmation;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.menus.Ownership;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class OwnerCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public OwnerCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		MessageManager messageManager = plugin.getMessageManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (islandManager.hasIsland(player)) {
			me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());
			
			if (args.length == 0) {
				if (island.isRole(Role.Owner, player.getUniqueId())) {
					playerData.setType(Ownership.Visibility.Hidden);
					Ownership.getInstance().open(player);
					soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			} else if (args.length == 1) {
				if (island.isRole(Role.Owner, player.getUniqueId())) {
					if (playerData.getConfirmationTime() > 0) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Confirmation.Pending.Message"));
						soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
					} else {
						UUID targetPlayerUUID;
						String targetPlayerName;
						
						Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						
						if (targetPlayer == null) {
							OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
							targetPlayerUUID = offlinePlayer.getUniqueId();
							targetPlayerName = offlinePlayer.getName();
						} else {
							targetPlayerUUID = targetPlayer.getUniqueId();
							targetPlayerName = targetPlayer.getName();
						}
						
						if (targetPlayerUUID == null || (!island.isRole(Role.Member, targetPlayerUUID) && !island.isRole(Role.Operator, targetPlayerUUID) && !island.isRole(Role.Owner, targetPlayerUUID))) {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Member.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (targetPlayerUUID.equals(player.getUniqueId())) {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Yourself.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							int confirmationTime = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Confirmation.Timeout");
							
							playerData.setOwnership(targetPlayerUUID);
							playerData.setConfirmation(Confirmation.Ownership);
							playerData.setConfirmationTime(confirmationTime);
							
							player.spigot().sendMessage(new ChatComponent(configLoad.getString("Command.Island.Ownership.Confirmation.Confirm.Message").replace("%player", targetPlayerName).replace("%time", "" + confirmationTime) + "   ", false, null, null, null).addExtra(new ChatComponent(configLoad.getString("Command.Island.Ownership.Confirmation.Confirm.Word.Confirm").toUpperCase(), true, ChatColor.RED, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island confirm"), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ownership.Confirmation.Confirm.Word.Tutorial"))).create()))));
							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
						}
					}
				} else {
					if (island.hasPassword()) {
						if (args[0].equalsIgnoreCase(island.getPassword())) {
							for (Player all : Bukkit.getOnlinePlayers()) {
								if ((island.isRole(Role.Member, all.getUniqueId()) || island.isRole(Role.Operator, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId())) && (!all.getUniqueId().equals(player.getUniqueId()))) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ownership.Assigned.Broadcast.Message").replace("%player", player.getName())));
									soundManager.playSound(all, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
								}
							}
							
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Assigned.Sender.Message"));
							soundManager.playSound(player, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
							
							islandManager.giveIslandOwnership(player.getUniqueId());
						} else {
							messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Password.Incorrect.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					} else {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Password.Unset.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				}
				
				return;
			}
			
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "owner";
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
		return new String[] { "ownership", "transfer", "makeleader", "makeowner" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
