package me.goodandevil.skyblock.ownership;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class OwnershipManager {

	private final SkyBlock skyblock;
	private Map<UUID, Ownership> ownershipStorage = new HashMap<>();

	public OwnershipManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		new OwnershipTask(this, skyblock).runTaskTimerAsynchronously(skyblock, 0L, 20L);

		IslandManager islandManager = skyblock.getIslandManager();
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();

		for (Player all : Bukkit.getOnlinePlayers()) {
			if (islandManager.hasIsland(all)) {
				Island island = islandManager.getIsland(playerDataManager.getPlayerData(all).getOwner());

				if (!hasOwnership(island.getOwnerUUID())) {
					loadOwnership(island.getOwnerUUID());
				}
			}
		}
	}

	public void onDisable() {
		IslandManager islandManager = skyblock.getIslandManager();

		for (UUID islandList : islandManager.getIslands().keySet()) {
			Island island = islandManager.getIslands().get(islandList);

			if (hasOwnership(island.getOwnerUUID())) {
				saveOwnership(island.getOwnerUUID());
				unloadOwnership(island.getOwnerUUID());
			}
		}
	}

	public void createOwnership(UUID uuid) {
		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();

		configLoad.set("Ownership.Cooldown",
				skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getInt("Island.Ownership.Cooldown"));

		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void transferOwnership(UUID uuid1, UUID uuid2) {
		if (ownershipStorage.containsKey(uuid1)) {
			ownershipStorage.put(uuid2, ownershipStorage.get(uuid1));
			ownershipStorage.remove(uuid1);
		}
	}

	public void removeOwnership(UUID uuid) {
		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();

		configLoad.set("Ownership.Cooldown", null);

		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveOwnership(UUID uuid) {
		if (ownershipStorage.containsKey(uuid)) {
			Config config = skyblock.getFileManager().getConfig(
					new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
			File configFile = config.getFile();
			FileConfiguration configLoad = config.getFileConfiguration();

			configLoad.set("Ownership.Cooldown", getOwnership(uuid).getTime());

			try {
				configLoad.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadOwnership(UUID uuid) {
		if (!ownershipStorage.containsKey(uuid)) {
			Config config = skyblock.getFileManager().getConfig(
					new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			if (configLoad.getString("Ownership.Cooldown") != null) {
				ownershipStorage.put(uuid, new Ownership(configLoad.getInt("Ownership.Cooldown")));
			}
		}
	}

	public void unloadOwnership(UUID uuid) {
		if (ownershipStorage.containsKey(uuid)) {
			ownershipStorage.remove(uuid);
		}
	}

	public Ownership getOwnership(UUID uuid) {
		if (ownershipStorage.containsKey(uuid)) {
			return ownershipStorage.get(uuid);
		}

		return null;
	}

	public boolean hasOwnership(UUID uuid) {
		return ownershipStorage.containsKey(uuid);
	}
}
