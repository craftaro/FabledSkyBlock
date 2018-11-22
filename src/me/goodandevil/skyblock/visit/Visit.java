package me.goodandevil.skyblock.visit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager.Config;

public class Visit {

	private final Main plugin;
	
	private UUID islandOwnerUUID;
	
	private Location[] islandLocations;
	
	private int islandSize;
	private int islandMembers;
	private int islandLevel;
	
	private List<String> islandSignature;
	
	private boolean open;
	
	protected Visit(Main plugin, UUID islandOwnerUUID, Location[] islandLocations, int islandSize, int islandMembers, int islandLevel, List<String> islandSignature, boolean open) {
		this.plugin = plugin;
		this.islandOwnerUUID = islandOwnerUUID;
		this.islandLocations = islandLocations;
		this.islandSize = islandSize;
		this.islandMembers = islandMembers;
		this.islandLevel = islandLevel;
		this.islandSignature = islandSignature;
		this.open = open;
	}
	
	public UUID getOwnerUUID() {
		return islandOwnerUUID;
	}
	
	public void setOwnerUUID(UUID islandOwnerUUID) {
		this.islandOwnerUUID = islandOwnerUUID;
	}
	
	public Location getLocation(me.goodandevil.skyblock.island.Location.World world) {
		if (world == me.goodandevil.skyblock.island.Location.World.Normal) {
			return islandLocations[0];
		} else if (world == me.goodandevil.skyblock.island.Location.World.Nether) {
			return islandLocations[1];
		}
		
		return null;
	}
	
	public int getMembers() {
		return islandMembers;
	}
	
	public void setMembers(int islandMembers) {
		this.islandMembers = islandMembers;
	}
	
	public int getRadius() {
		return islandSize;
	}
	
	public void setSize(int islandSize) {
		this.islandSize = islandSize;
	}
	
	public int getLevel() {
		return islandLevel;
	}
	
	public void setLevel(int islandLevel) {
		this.islandLevel = islandLevel;
	}
	
	public boolean isVisitor(UUID uuid) {
		return getVisitors().contains(uuid);
	}
	
	public List<UUID> getVisitors() {
		List<UUID> islandVisitors = new ArrayList<>();
		
		for (String islandVisitorList : plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration().getStringList("Visitors")) {
			islandVisitors.add(UUID.fromString(islandVisitorList));
		}
		
		return islandVisitors;
	}
	
	public void addVisitor(UUID uuid) {
		List<String> islandVisitors = new ArrayList<>();
		FileConfiguration configLoad = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration();
		
		for (String islandVisitorList : configLoad.getStringList("Visitors")) {
			islandVisitors.add(islandVisitorList);
		}
		
		islandVisitors.add(uuid.toString());
		configLoad.set("Visitors", islandVisitors);
	}
	
	public boolean isVoter(UUID uuid) {
		return getVoters().contains(uuid);
	}
	
	public List<UUID> getVoters() {
		List<UUID> islandVoters = new ArrayList<>();
		
		for (String islandVisitorList : plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration().getStringList("Voters")) {
			islandVoters.add(UUID.fromString(islandVisitorList));
		}
		
		return islandVoters;
	}
	
	public void addVoter(UUID uuid) {
		List<String> islandVoters = new ArrayList<>();
		FileConfiguration configLoad = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration();
		
		for (String islandVoterList : configLoad.getStringList("Voters")) {
			islandVoters.add(islandVoterList);
		}
		
		islandVoters.add(uuid.toString());
		configLoad.set("Voters", islandVoters);
	}
	
	public void removeVoter(UUID uuid) {
		List<String> islandVoters = new ArrayList<>();
		FileConfiguration configLoad = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml")).getFileConfiguration();
		
		for (String islandVoterList : configLoad.getStringList("Voters")) {
			if (!uuid.toString().equals(islandVoterList)) {
				islandVoters.add(islandVoterList);
			}
		}
		
		configLoad.set("Voters", islandVoters);
	}
	
	public List<String> getSiganture() {
		return islandSignature;
	}
	
	public void setSignature(List<String> islandSignature) {
		this.islandSignature = islandSignature;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public void save() {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml"));
		
		try {
			config.getFileConfiguration().save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
