package me.goodandevil.skyblock.ban;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.events.IslandBanEvent;
import me.goodandevil.skyblock.events.IslandUnbanEvent;

public class Ban {

	private UUID islandOwnerUUID;
	
	public Ban(UUID islandOwnerUUID) {
		this.islandOwnerUUID = islandOwnerUUID;
	}
	
	public UUID getOwnerUUID() {
		return islandOwnerUUID;
	}
	
	public void setOwnerUUID(UUID islandOwnerUUID) {
		this.islandOwnerUUID = islandOwnerUUID;
	}
	
	public boolean isBanned(UUID uuid) {
		return getBans().contains(uuid);
	}
	
	public List<UUID> getBans() {
		SkyBlock skyblock = SkyBlock.getInstance();
		
		List<UUID> islandBans = new ArrayList<>();
		
		for (String islandBanList : skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration().getStringList("Bans")) {
			islandBans.add(UUID.fromString(islandBanList));
		}
		
		return islandBans;
	}
	
	public void addBan(UUID uuid) {
		SkyBlock skyblock = SkyBlock.getInstance();
		
		List<String> islandBans = new ArrayList<>();
		FileConfiguration configLoad = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration();
		
		for (String islandBanList : configLoad.getStringList("Bans")) {
			islandBans.add(islandBanList);
		}
		
		islandBans.add(uuid.toString());
		configLoad.set("Bans", islandBans);
		
		Bukkit.getServer().getPluginManager().callEvent(new IslandBanEvent(uuid, this));
	}
	
	public void removeBan(UUID uuid) {
		SkyBlock skyblock = SkyBlock.getInstance();
		
		List<String> islandBans = new ArrayList<>();
		FileConfiguration configLoad = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration();
		
		for (String islandBanList : configLoad.getStringList("Bans")) {
			if (!uuid.toString().equals(islandBanList)) {
				islandBans.add(islandBanList);
			}
		}
		
		configLoad.set("Bans", islandBans);
		
		Bukkit.getServer().getPluginManager().callEvent(new IslandUnbanEvent(uuid, this));
	}
	
	public void save() {
		SkyBlock skyblock = SkyBlock.getInstance();
		
		Config config = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml"));
		
		try {
			config.getFileConfiguration().save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
