package me.goodandevil.skyblock.ban;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
		
		if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Unload")) {
			File configFile = new File(skyblock.getDataFolder().toString() + "/island-data");
			
			if (configFile.exists()) {
				for (File fileList : configFile.listFiles()) {
					UUID islandOwnerUUID = UUID.fromString(fileList.getName().replaceFirst("[.][^.]+$", ""));
					createIsland(islandOwnerUUID);
				}
			}
		}
	}
	
	public void transfer(UUID uuid, UUID islandOwnerUUID) {
		FileManager fileManager = skyblock.getFileManager();
		
		Ban ban = getIsland(islandOwnerUUID);
		ban.save();
		
		File oldBanDataFile = new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml");
		File newBanDataFile = new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), uuid.toString() + ".yml");
		
		fileManager.unloadConfig(oldBanDataFile);
		fileManager.unloadConfig(newBanDataFile);
		
		oldBanDataFile.renameTo(newBanDataFile);
		
		removeIsland(islandOwnerUUID);
		addIsland(uuid, ban);
	}
	
	public void removeVisitor(Island island) {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();
		
		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		for (UUID visitorList : skyblock.getIslandManager().getVisitorsAtIsland(island)) {
			Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);
			
			LocationUtil.teleportPlayerToSpawn(targetPlayer);
			
			messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit.Banned.Message"));
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
			skyblock.getFileManager().unloadConfig(new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml"));
			banStorage.remove(islandOwnerUUID);
		}
	}
	
	public void deleteIsland(UUID islandOwnerUUID) {
		if (hasIsland(islandOwnerUUID)) {
			skyblock.getFileManager().deleteConfig(new File(new File(skyblock.getDataFolder().toString() + "/ban-data"), islandOwnerUUID.toString() + ".yml"));
			banStorage.remove(islandOwnerUUID);
		}
	}
}
