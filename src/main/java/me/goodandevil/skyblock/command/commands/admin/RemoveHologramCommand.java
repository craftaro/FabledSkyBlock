package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.hologram.Hologram;
import me.goodandevil.skyblock.hologram.HologramManager;
import me.goodandevil.skyblock.hologram.HologramType;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class RemoveHologramCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public RemoveHologramCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		HologramManager hologramManager = skyblock.getHologramManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();
		
		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (player.hasPermission("skyblock.admin.removehologram") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
			if (args.length == 1) {
				HologramType hologramType = null;
				
				if (args[0].equalsIgnoreCase("Level")) {
					hologramType = HologramType.Level;
				} else if (args[0].equalsIgnoreCase("Votes")) {
					hologramType = HologramType.Votes;
				}
				
				if (hologramType != null) {
					Config locationsConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "locations.yml"));
					FileConfiguration locationsConfigLoad = locationsConfig.getFileConfiguration();
					
					if (locationsConfigLoad.getString("Location.Hologram.Leaderboard." + hologramType.name()) == null) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.RemoveHologram.Set.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else {
						locationsConfigLoad.set("Location.Hologram.Leaderboard." + hologramType.name(), null);
						
						try {
							locationsConfigLoad.save(locationsConfig.getFile());
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
							@Override
							public void run() {
								HologramType hologramType = HologramType.valueOf(WordUtils.capitalize(args[0].toLowerCase()));
								Hologram hologram = hologramManager.getHologram(hologramType);
								
								if (hologram != null) {
									hologramManager.removeHologram(hologram);
								}
							}
						});
						
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.RemoveHologram.Removed.Message").replace("%type", hologramType.name()));
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					}
					
					return;
				}
			}
			
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.RemoveHologram.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.RemoveHologram.Permission.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "removehologram";
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
