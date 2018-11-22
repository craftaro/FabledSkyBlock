package me.goodandevil.skyblock.creation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager.Config;

public class CreationManager {

	private final Main plugin;
	private Map<UUID, Creation> playerCreationStorage = new HashMap<>();
	
	public CreationManager(Main plugin) {
		this.plugin = plugin;
		
		new CreationTask(plugin).runTaskTimerAsynchronously(plugin, 0L, 20L);
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			loadPlayer(all);
		}
	}
	
	public void onDisable() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			savePlayer(all);
		}
	}
	
	public void createPlayer(Player player, int time) {
		savePlayer(player.getUniqueId(), time);
		
		Creation creation = new Creation(time);
		playerCreationStorage.put(player.getUniqueId(), creation);
	}
	
	public void createOfflinePlayer(UUID uuid, int time) {
		savePlayer(uuid, time);
	}
	
	private void savePlayer(UUID uuid, int time) {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();
		
		configLoad.set("Island.Creation.Cooldown", time);
		
		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayer(Player player) {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (configLoad.getString("Island.Creation.Cooldown") != null) {
			Creation creation = new Creation(configLoad.getInt("Island.Creation.Cooldown"));
			playerCreationStorage.put(player.getUniqueId(), creation);
		}
	}
	
	public void removePlayer(Player player) {
		if (hasPlayer(player)) {
			Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
			File configFile = config.getFile();
			FileConfiguration configLoad = config.getFileConfiguration();
			
			configLoad.set("Island.Creation.Cooldown", null);
			
			try {
				configLoad.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			playerCreationStorage.remove(player.getUniqueId());
		}
	}
	
	public void savePlayer(Player player) {
		if (hasPlayer(player)) {
			Creation creation = getPlayer(player);
			
			Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
			File configFile = config.getFile();
			FileConfiguration configLoad = config.getFileConfiguration();
			
			configLoad.set("Island.Creation.Cooldown", creation.getTime());
			
			try {
				configLoad.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unloadPlayer(Player player) {
		if (hasPlayer(player)) {
			playerCreationStorage.remove(player.getUniqueId());
		}
	}

	public Creation getPlayer(Player player) {
		if (hasPlayer(player)) {
			return playerCreationStorage.get(player.getUniqueId());
		}
		
		return null;
	}
	
	public boolean hasPlayer(Player player) {
		return playerCreationStorage.containsKey(player.getUniqueId());
	}
}
