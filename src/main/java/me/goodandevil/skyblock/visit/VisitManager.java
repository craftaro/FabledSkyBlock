package me.goodandevil.skyblock.visit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Level;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class VisitManager {

	private final SkyBlock skyblock;
	private HashMap<UUID, Visit> visitStorage = new HashMap<>();

	public VisitManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		loadIslands();
	}

	public void onDisable() {
		HashMap<UUID, Visit> visitIslands = getIslands();

		for (UUID visitIslandList : visitIslands.keySet()) {
			Visit visit = visitIslands.get(visitIslandList);
			visit.save();
		}
	}

	public void loadIslands() {
		FileManager fileManager = skyblock.getFileManager();

		if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
				.getBoolean("Island.Visitor.Unload")) {
			File configFile = new File(skyblock.getDataFolder().toString() + "/island-data");

			if (configFile.exists()) {
				for (File fileList : configFile.listFiles()) {
					if (fileList != null && fileList.getName().contains(".yml") && fileList.getName().length() > 35) {
						try {
							Config config = new FileManager.Config(fileManager, fileList);
							FileConfiguration configLoad = config.getFileConfiguration();

							UUID islandOwnerUUID = UUID.fromString(fileList.getName().replace(".yml", ""));

							if (islandOwnerUUID == null) {
								islandOwnerUUID = UUID.fromString(fileList.getName().replaceFirst("[.][^.]+$", ""));

								if (islandOwnerUUID == null) {
									continue;
								}
							}

							List<String> islandSignature = new ArrayList<>();

							if (configLoad.getString("Visitor.Signature.Message") != null) {
								islandSignature = configLoad.getStringList("Visitor.Signature.Message");
							}

							int division = skyblock.getFileManager()
									.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
									.getInt("Island.Levelling.Division");

							if (division == 0) {
								division = 1;
							}

							int size = 100;

							if (configLoad.getString("Size") != null) {
								size = configLoad.getInt("Size");
							}

							createIsland(islandOwnerUUID,
									new Location[] { fileManager.getLocation(config, "Location.Normal.Island", true),
											fileManager.getLocation(config, "Location.Nether.Island", true) },
									size,
									configLoad.getStringList("Members").size()
											+ configLoad.getStringList("Operators").size() + 1,
									getIslandSafeLevel(islandOwnerUUID), new Level(islandOwnerUUID, skyblock),
									islandSignature, configLoad.getBoolean("Visitor.Open"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void transfer(UUID uuid1, UUID uuid2) {
		Visit visit = getIsland(uuid1);
		visit.setOwnerUUID(uuid2);
		visit.getLevel().setOwnerUUID(uuid2);
		visit.save();

		File oldVisitDataFile = new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
				uuid1.toString() + ".yml");
		File newVisitDataFile = new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
				uuid2.toString() + ".yml");

		skyblock.getFileManager().unloadConfig(oldVisitDataFile);
		skyblock.getFileManager().unloadConfig(newVisitDataFile);

		oldVisitDataFile.renameTo(newVisitDataFile);

		removeIsland(uuid1);
		addIsland(uuid2, visit);
	}

	public void removeVisitors(Island island, VisitManager.Removal removal) {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		for (UUID visitorList : skyblock.getIslandManager().getVisitorsAtIsland(island)) {
			Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);

			LocationUtil.teleportPlayerToSpawn(targetPlayer);

			messageManager.sendMessage(targetPlayer,
					configLoad.getString("Island.Visit." + removal.name() + ".Message"));
			soundManager.playSound(targetPlayer, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
		}
	}

	public int getIslandSafeLevel(UUID islandOwnerUUID) {
		FileManager fileManager = skyblock.getFileManager();

		Config settingDataConfig = new FileManager.Config(fileManager,
				new File(skyblock.getDataFolder().toString() + "/setting-data", islandOwnerUUID.toString() + ".yml"));
		FileConfiguration settingDataConfigLoad = settingDataConfig.getFileConfiguration();

		Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

		int safeLevel = 0;

		Map<String, Boolean> settings = new HashMap<>();
		settings.put("KeepItemsOnDeath", false);
		settings.put("PvP", true);
		settings.put("Damage", true);

		for (String settingList : settings.keySet()) {
			if (mainConfigLoad.getBoolean("Island.Settings." + settingList + ".Enable")
					&& settingDataConfigLoad.getString("Settings.Owner." + settingList) != null
					&& settingDataConfigLoad.getBoolean("Settings.Owner." + settingList) == settings.get(settingList)) {
				safeLevel++;
			}
		}

		return safeLevel;
	}

	public boolean hasIsland(UUID islandOwnerUUID) {
		return visitStorage.containsKey(islandOwnerUUID);
	}

	public Visit getIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			return visitStorage.get(islandOwnerUUID);
		}

		return null;
	}

	public HashMap<UUID, Visit> getIslands() {
		return visitStorage;
	}

	public Map<UUID, Visit> getOpenIslands() {
		Map<UUID, Visit> visitIslands = new ConcurrentHashMap<>();
		visitIslands.putAll(visitStorage);

		Iterator<UUID> it = visitIslands.keySet().iterator();

		while (it.hasNext()) {
			UUID islandOwnerUUID = it.next();
			me.goodandevil.skyblock.visit.Visit visit = visitIslands.get(islandOwnerUUID);

			if (!visit.isOpen()) {
				visitIslands.remove(islandOwnerUUID);
			}
		}

		return visitIslands;
	}

	public void createIsland(UUID islandOwnerUUID, Location[] islandLocations, int islandSize, int islandMembers,
			int safeLevel, Level islandLevel, List<String> islandSignature, boolean open) {
		visitStorage.put(islandOwnerUUID, new Visit(skyblock, islandOwnerUUID, islandLocations, islandSize,
				islandMembers, safeLevel, islandLevel, islandSignature, open));
	}

	public void addIsland(UUID islandOwnerUUID, Visit visit) {
		visitStorage.put(islandOwnerUUID, visit);
	}

	public void removeIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			visitStorage.remove(islandOwnerUUID);
		}
	}

	public void unloadIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			skyblock.getFileManager()
					.unloadConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
							islandOwnerUUID.toString() + ".yml"));
			visitStorage.remove(islandOwnerUUID);
		}
	}

	public void deleteIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			skyblock.getFileManager()
					.deleteConfig(new File(new File(skyblock.getDataFolder().toString() + "/visit-data"),
							islandOwnerUUID.toString() + ".yml"));
			visitStorage.remove(islandOwnerUUID);
		}
	}

	public enum Removal {

		Unloaded, Kicked, Deleted;

	}
}
