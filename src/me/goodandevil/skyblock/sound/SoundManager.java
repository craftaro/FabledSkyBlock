package me.goodandevil.skyblock.sound;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;

public class SoundManager {

	private final SkyBlock skyblock;
	
	public SoundManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	public void playSound(Player player, Sound sound, float volume, float pitch) {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (configLoad.getBoolean("Sound.Enable")) {
			player.playSound(player.getLocation(), sound, volume, pitch);
		}
	}
	
	public void playSound(Location location, Sound sound, float volume, float pitch) {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (configLoad.getBoolean("Sound.Enable")) {
			location.getWorld().playSound(location, sound, volume, pitch);
		}
	}
}
