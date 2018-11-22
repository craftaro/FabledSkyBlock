package me.goodandevil.skyblock.utils;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.goodandevil.skyblock.Main;

public class OfflinePlayer {
    
    private UUID uuid;
	
	private String name;
	private String memberSince;
	private String lastOnline;
	private UUID owner = null;
	private String[] texture;
	
	private int playtime;
	
	public OfflinePlayer(String name) {
    	@SuppressWarnings("deprecation")
		org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(name);
		
    	this.name = offlinePlayer.getName();
    	this.uuid = offlinePlayer.getUniqueId();
    	
		FileConfiguration configLoad = YamlConfiguration.loadConfiguration(new File(new File(Main.getInstance().getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
		texture = new String[] { configLoad.getString("Texture.Signature") , configLoad.getString("Texture.Value") };
		playtime = configLoad.getInt("Statistics.Island.Playtime");
		memberSince = configLoad.getString("Statistics.Island.Join");
		lastOnline = configLoad.getString("Statistics.Island.LastOnline");
		
		if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
			owner = UUID.fromString(configLoad.getString("Island.Owner"));
		}
	}
	
	public OfflinePlayer(UUID uuid) {
		org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
		
    	this.name = offlinePlayer.getName();
    	this.uuid = offlinePlayer.getUniqueId();
		
		FileConfiguration configLoad = YamlConfiguration.loadConfiguration(new File(new File(Main.getInstance().getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
		texture = new String[] { configLoad.getString("Texture.Signature") , configLoad.getString("Texture.Value") };
		playtime = configLoad.getInt("Statistics.Island.Playtime");
		memberSince = configLoad.getString("Statistics.Island.Join");
		lastOnline = configLoad.getString("Statistics.Island.LastOnline");
		
		if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
			owner = UUID.fromString(configLoad.getString("Island.Owner"));
		}
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMemberSince() {
		return memberSince;
	}
	
	public String getLastOnline() {
		return lastOnline;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	public String[] getTexture() {
		return texture;
	}
	
	public int getPlaytime() {
		return playtime;
	}
}
