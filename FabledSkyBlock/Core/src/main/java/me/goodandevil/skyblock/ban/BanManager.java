package me.goodandevil.skyblock.ban;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.goodandevil.skyblock.config.ConfigFile;
import me.goodandevil.skyblock.config.DataFolder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class BanManager {

	private final SkyBlock skyblock;
	private Map<UUID, Ban> banStorage = new HashMap<>();

	public BanManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		loadIslands();
	}

	public void onDisable() {
		Map<UUID, Ban> banIslands = getIslands();

		for (UUID banIslandList : banIslands.keySet()) {
			Ban ban = banIslands.get(banIslandList);
			ban.save();
		}
	}

	public void loadIslands() {
		FileManager fileManager = skyblock.getFileManager();

		if (!fileManager.getFileConfiguration(ConfigFile.CONFIG).getBoolean("Island.Visitor.Unload")) {
			File configFile = new File(skyblock.getDataFolder().toString() + "/island-data");

			if (configFile.exists()) {
				for (File fileList : configFile.listFiles()) {
					UUID islandOwnerUUID = UUID.fromString(fileList.getName().replaceFirst("[.][^.]+$", ""));
					createIsland(islandOwnerUUID);
				}
			}
		}
	}

	public void transfer(UUID uuid1, UUID uuid2) {
		FileManager fileManager = skyblock.getFileManager();

		Ban ban = getIsland(uuid1);
		ban.save();

		fileManager.unloadDataFile(DataFolder.BAN_DATA, uuid1);
		fileManager.unloadDataFile(DataFolder.BAN_DATA, uuid2);
		fileManager.renameDataFile(DataFolder.BAN_DATA, uuid1, uuid2);

		removeIsland(uuid1);
		addIsland(uuid2, ban);
	}

	public void removeVisitor(Island island) {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();

		for (UUID visitorList : skyblock.getIslandManager().getVisitorsAtIsland(island)) {
			Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);

			LocationUtil.teleportPlayerToSpawn(targetPlayer);

			messageManager.sendLangMessage(targetPlayer, "Island.Visit.Banned.Island.Message");
			soundManager.playSound(targetPlayer, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
		}
	}

	public boolean hasIsland(UUID islandOwnerUUID) {
		return banStorage.containsKey(islandOwnerUUID);
	}

	public Ban getIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			return banStorage.get(islandOwnerUUID);
		}

		return null;
	}

	public Map<UUID, Ban> getIslands() {
		return banStorage;
	}

	public void createIsland(UUID islandOwnerUUID) {
		banStorage.put(islandOwnerUUID, new Ban(islandOwnerUUID));
	}

	public void addIsland(UUID islandOwnerUUID, Ban ban) {
		banStorage.put(islandOwnerUUID, ban);
	}

	public void removeIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			banStorage.remove(islandOwnerUUID);
		}
	}

	public void unloadIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			skyblock.getFileManager().unloadDataFile(DataFolder.BAN_DATA, islandOwnerUUID);
			banStorage.remove(islandOwnerUUID);
		}
	}

	public void deleteIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			skyblock.getFileManager().deleteDataFile(DataFolder.BAN_DATA, islandOwnerUUID);
			banStorage.remove(islandOwnerUUID);
		}
	}
}
