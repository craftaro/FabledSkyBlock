package me.goodandevil.skyblock.ban;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.island.IslandBanEvent;
import me.goodandevil.skyblock.api.event.island.IslandUnbanEvent;
import me.goodandevil.skyblock.config.DataFolder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

	public Set<UUID> getBans() {
		SkyBlock skyblock = SkyBlock.getInstance();

		Set<UUID> islandBans = new HashSet<>();

		FileConfiguration bansFile = skyblock.getFileManager().getDataFileConfiguration(DataFolder.BAN_DATA, islandOwnerUUID.toString() + ".yml");
		for (String islandBanList :bansFile.getStringList("Bans"))
			islandBans.add(UUID.fromString(islandBanList));

		return islandBans;
	}

	public void addBan(UUID issuer, UUID banned) {
		SkyBlock skyblock = SkyBlock.getInstance();

		IslandBanEvent islandBanEvent = new IslandBanEvent(
				skyblock.getIslandManager().getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID))
						.getAPIWrapper(),
				Bukkit.getServer().getOfflinePlayer(issuer), Bukkit.getServer().getOfflinePlayer(banned));
		Bukkit.getScheduler().runTask(skyblock, () -> Bukkit.getServer().getPluginManager().callEvent(islandBanEvent));

		if (!islandBanEvent.isCancelled()) {
			List<String> islandBans = new ArrayList<>();
			FileConfiguration bansFile = skyblock.getFileManager().getDataFileConfiguration(DataFolder.BAN_DATA, islandOwnerUUID.toString() + ".yml");

			for (String islandBanList : bansFile.getStringList("Bans")) {
				islandBans.add(islandBanList);
			}

			islandBans.add(banned.toString());
			bansFile.set("Bans", islandBans);
		}
	}

	public void removeBan(UUID uuid) {
		SkyBlock skyblock = SkyBlock.getInstance();

		List<String> islandBans = new ArrayList<>();
		FileConfiguration bansFile = skyblock.getFileManager().getDataFileConfiguration(DataFolder.BAN_DATA, islandOwnerUUID.toString() + ".yml");

		for (String islandBanList : bansFile.getStringList("Bans")) {
			if (!uuid.toString().equals(islandBanList)) {
				islandBans.add(islandBanList);
			}
		}

		bansFile.set("Bans", islandBans);

		Bukkit.getServer().getPluginManager()
				.callEvent(new IslandUnbanEvent(skyblock.getIslandManager()
						.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)).getAPIWrapper(),
						Bukkit.getServer().getOfflinePlayer(uuid)));
	}

	public void save() {
		SkyBlock.getInstance().getFileManager().getDataFile(DataFolder.BAN_DATA, islandOwnerUUID.toString() + ".yml").save();
	}
}
