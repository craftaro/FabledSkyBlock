package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.events.IslandChatSwitchEvent;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;

public class ChatCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public ChatCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (islandManager.hasIsland(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);
			Island island = islandManager.getIsland(playerData.getOwner());
			
			if ((island.getRole(Role.Member).size() + island.getRole(Role.Operator).size()) == 0) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Team.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else if ((islandManager.getMembersOnline(island).size() - 1) <= 0) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Offline.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);	
			} else {
				if (playerData.isChat()) {
					Bukkit.getServer().getPluginManager().callEvent(new IslandChatSwitchEvent(player, island, false));
					playerData.setChat(false);
					
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Untoggled.Message"));
					soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
				} else {
					Bukkit.getServer().getPluginManager().callEvent(new IslandChatSwitchEvent(player, island, true));
					playerData.setChat(true);
						
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Toggled.Message"));
					soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
				}
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Chat.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}
	
	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "chat";
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
