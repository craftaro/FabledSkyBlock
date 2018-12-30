package me.goodandevil.skyblock.cooldown;

import java.util.UUID;

public class CooldownPlayer {

	private UUID uuid;
	private Cooldown cooldown;

	public CooldownPlayer(UUID uuid, Cooldown cooldown) {
		this.uuid = uuid;
		this.cooldown = cooldown;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public Cooldown getCooldown() {
		return cooldown;
	}

	public void setCooldown(Cooldown cooldown) {
		this.cooldown = cooldown;
	}
}
