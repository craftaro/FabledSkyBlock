package me.goodandevil.skyblock.invite;

import java.util.UUID;

import org.bukkit.entity.Player;

public class Invite {

	private UUID playerUUID;
	private UUID senderUUID;
	private UUID islandOwnerUUID;
	private String senderName;
	private int time;
	
	public Invite(Player player, Player sender, UUID islandOwnerUUID, int time) {
		this.playerUUID = player.getUniqueId();
		this.senderUUID = sender.getUniqueId();
		this.senderName = sender.getName();
		this.islandOwnerUUID = islandOwnerUUID;
		this.time = time;
	}
	
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	
	public UUID getSenderUUID() {
		return senderUUID;
	}
	
	public UUID getOwnerUUID() {
		return islandOwnerUUID;
	}
	
	public void setOwnerUUID(UUID islandOwnerUUID) {
		this.islandOwnerUUID = islandOwnerUUID;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
}
