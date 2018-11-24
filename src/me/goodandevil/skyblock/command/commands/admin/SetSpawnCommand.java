package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class SetSpawnCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public SetSpawnCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		MessageManager messageManager = plugin.getMessageManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (player.hasPermission("skyblock.admin.setspawn") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
			for (Location.World worldList : Location.World.values()) {
				World world = plugin.getWorldManager().getWorld(worldList);
				
				if (world.getName().equals(player.getWorld().getName())) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.SetSpawn.World.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			}
			
			fileManager.setLocation(fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml")), "Location.Spawn", player.getLocation(), true);
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.SetSpawn.Set.Message"));
			soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.SetSpawn.Permission.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "setspawn";
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
		return CommandManager.Type.Admin;
	}
}
