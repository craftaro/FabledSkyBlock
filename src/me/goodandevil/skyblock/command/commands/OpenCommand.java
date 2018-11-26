package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class OpenCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public OpenCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (islandManager.hasIsland(player)) {
			me.goodandevil.skyblock.island.Island island = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
			
			if (island.isRole(Role.Owner, player.getUniqueId()) || (island.isRole(Role.Operator, player.getUniqueId()) && island.getSetting(Settings.Role.Operator, "Visitor").getStatus())) {
				if (island.isOpen()) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Open.Already.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				} else {
					island.setOpen(true);
					
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Open.Opened.Message"));
					soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Open.Permission.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Open.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "open";
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
