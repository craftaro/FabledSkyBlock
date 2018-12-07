package me.goodandevil.skyblock.playerdata;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.confirmation.Confirmation;
import me.goodandevil.skyblock.generator.GeneratorLocation;
import me.goodandevil.skyblock.utils.structure.Area;

public class PlayerData {

	private UUID uuid;
	private UUID islandOwnerUUID;
	private UUID ownershipUUID;

	private int page;
	private int playTime;
	private int visitTime;
	private int confirmationTime;

	private Confirmation confirmation;

	private Object type;
	private Object sort;

	private Area area;

	private boolean chat;

	private Object viewer;

	private GeneratorLocation generatorLocation;

	public PlayerData(Player player) {
		uuid = player.getUniqueId();
		islandOwnerUUID = null;

		page = 1;
		confirmationTime = 0;
		playTime = getConfig().getFileConfiguration().getInt("Statistics.Island.Playtime");

		area = new Area();

		chat = false;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public Object getType() {
		return type;
	}

	public void setSort(Object sort) {
		this.sort = sort;
	}

	public Object getSort() {
		return sort;
	}

	public void setIsland(UUID islandOwnerUUID) {
		this.islandOwnerUUID = islandOwnerUUID;
	}

	public UUID getIsland() {
		return islandOwnerUUID;
	}

	public void setOwnership(UUID ownershipUUID) {
		this.ownershipUUID = ownershipUUID;
	}

	public UUID getOwnership() {
		return ownershipUUID;
	}

	public void setConfirmationTime(int confirmationTime) {
		this.confirmationTime = confirmationTime;
	}

	public int getConfirmationTime() {
		return confirmationTime;
	}

	public void setConfirmation(Confirmation confirmation) {
		this.confirmation = confirmation;
	}

	public Confirmation getConfirmation() {
		return confirmation;
	}

	public boolean hasConfirmation() {
		return confirmationTime > 0;
	}

	public void setPlaytime(int playTime) {
		this.playTime = playTime;
	}

	public int getPlaytime() {
		return playTime;
	}

	public void setVisitTime(int visitTime) {
		this.visitTime = visitTime;
	}

	public int getVisitTime() {
		return visitTime;
	}

	public String getMemberSince() {
		return getConfig().getFileConfiguration().getString("Statistics.Island.Join");
	}

	public void setMemberSince(String date) {
		getConfig().getFileConfiguration().set("Statistics.Island.Join", date);
	}

	public UUID getOwner() {
		String islandOwnerUUID = getConfig().getFileConfiguration().getString("Island.Owner");
		return (islandOwnerUUID == null) ? null : UUID.fromString(islandOwnerUUID);
	}

	public void setOwner(UUID islandOwnerUUID) {
		if (islandOwnerUUID == null) {
			getConfig().getFileConfiguration().set("Island.Owner", null);
		} else {
			getConfig().getFileConfiguration().set("Island.Owner", islandOwnerUUID.toString());
		}
	}

	public String[] getTexture() {
		FileConfiguration configLoad = getConfig().getFileConfiguration();

		return new String[] { configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value") };
	}

	public void setTexture(String signature, String value) {
		getConfig().getFileConfiguration().set("Texture.Signature", signature);
		getConfig().getFileConfiguration().set("Texture.Value", value);
	}

	public String getLastOnline() {
		return getConfig().getFileConfiguration().getString("Statistics.Island.LastOnline");
	}

	public void setLastOnline(String date) {
		getConfig().getFileConfiguration().set("Statistics.Island.LastOnline", date);
	}

	public Area getArea() {
		return area;
	}

	public boolean isChat() {
		return chat;
	}

	public void setChat(boolean chat) {
		this.chat = chat;
	}

	public Object getViewer() {
		return viewer;
	}

	public void setViewer(Object viewer) {
		this.viewer = viewer;
	}

	public GeneratorLocation getGenerator() {
		return generatorLocation;
	}

	public void setGenerator(GeneratorLocation generatorLocation) {
		this.generatorLocation = generatorLocation;
	}

	public void save() {
		Config config = getConfig();
		FileConfiguration configLoad = config.getFileConfiguration();
		configLoad.set("Statistics.Island.Playtime", getPlaytime());

		try {
			configLoad.save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Config getConfig() {
		SkyBlock skyblock = SkyBlock.getInstance();

		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
	}
}
