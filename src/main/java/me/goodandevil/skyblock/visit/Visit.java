package me.goodandevil.skyblock.visit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.ban.Ban;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Level;

public class Visit {

	private final SkyBlock skyblock;

	private UUID islandOwnerUUID;

	private String islandOwnerName;

	private Location[] islandLocations;

	private int islandSize;
	private int islandMembers;
	private int safeLevel;

	private final Level islandLevel;

	private List<String> islandSignature;

	private boolean open;

	protected Visit(SkyBlock skyblock, UUID islandOwnerUUID, Location[] islandLocations, int islandSize,
			int islandMembers, int safeLevel, Level islandLevel, List<String> islandSignature, boolean open) {
		this.skyblock = skyblock;
		this.islandOwnerUUID = islandOwnerUUID;
		// this.islandOwnerName = new
		// OfflinePlayer(islandOwnerUUID).getNames()[0].getName();
		this.islandLocations = islandLocations;
		this.islandSize = islandSize;
		this.islandMembers = islandMembers;
		this.safeLevel = safeLevel;
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

	public String getOwnerName() {
		return islandOwnerName;
	}

	public void setOwnerName(String islandOwnerName) {
		this.islandOwnerName = islandOwnerName;
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

	public int getSafeLevel() {
		return safeLevel;
	}

	public void setSafeLevel(int safeLevel) {
		this.safeLevel = safeLevel;
	}

	public int getRadius() {
		return islandSize;
	}

	public void setSize(int islandSize) {
		this.islandSize = islandSize;
	}

	public Level getLevel() {
		return islandLevel;
	}

	public boolean isVisitor(UUID uuid) {
		return getVisitors().contains(uuid);
	}

	public Set<UUID> getVisitors() {
		Set<UUID> islandVisitors = new HashSet<>();

		for (String islandVisitorList : skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
						islandOwnerUUID.toString() + ".yml"))
				.getFileConfiguration().getStringList("Visitors")) {
			islandVisitors.add(UUID.fromString(islandVisitorList));
		}

		return islandVisitors;
	}

	public void addVisitor(UUID uuid) {
		List<String> islandVisitors = new ArrayList<>();
		FileConfiguration configLoad = skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
						islandOwnerUUID.toString() + ".yml"))
				.getFileConfiguration();

		for (String islandVisitorList : configLoad.getStringList("Visitors")) {
			islandVisitors.add(islandVisitorList);
		}

		islandVisitors.add(uuid.toString());
		configLoad.set("Visitors", islandVisitors);
	}

	public void removeVisitor(UUID uuid) {
		List<String> islandVisitors = new ArrayList<>();
		FileConfiguration configLoad = skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
						islandOwnerUUID.toString() + ".yml"))
				.getFileConfiguration();

		for (String islandVisitorList : configLoad.getStringList("Visitors")) {
			islandVisitors.add(islandVisitorList);
		}

		islandVisitors.remove(uuid.toString());
		configLoad.set("Visitors", islandVisitors);
	}

	public boolean isVoter(UUID uuid) {
		return getVoters().contains(uuid);
	}

	public Set<UUID> getVoters() {
		Set<UUID> islandVoters = new HashSet<>();

		for (String islandVisitorList : skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
						islandOwnerUUID.toString() + ".yml"))
				.getFileConfiguration().getStringList("Voters")) {
			islandVoters.add(UUID.fromString(islandVisitorList));
		}

		return islandVoters;
	}

	public void addVoter(UUID uuid) {
		List<String> islandVoters = new ArrayList<>();
		FileConfiguration configLoad = skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
						islandOwnerUUID.toString() + ".yml"))
				.getFileConfiguration();

		for (String islandVoterList : configLoad.getStringList("Voters")) {
			islandVoters.add(islandVoterList);
		}

		islandVoters.add(uuid.toString());
		configLoad.set("Voters", islandVoters);
	}

	public void removeVoter(UUID uuid) {
		List<String> islandVoters = new ArrayList<>();
		FileConfiguration configLoad = skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
						islandOwnerUUID.toString() + ".yml"))
				.getFileConfiguration();

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

	public Ban getBan() {
		return skyblock.getBanManager().getIsland(getOwnerUUID());
	}

	public void save() {
		Config config = skyblock.getFileManager().getConfig(new File(
				new File(skyblock.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml"));

		try {
			config.getFileConfiguration().save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
