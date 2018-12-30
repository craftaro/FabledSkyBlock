package me.goodandevil.skyblock.cooldown;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownTask extends BukkitRunnable {

	private final CooldownManager cooldownManager;

	public CooldownTask(CooldownManager cooldownManager) {
		this.cooldownManager = cooldownManager;
	}

	@Override
	public void run() {
		for (CooldownType cooldownTypeList : CooldownType.values()) {
			if (cooldownManager.hasCooldownType(cooldownTypeList)) {
				List<CooldownPlayer> cooldownPlayers = cooldownManager.getCooldownPlayers(cooldownTypeList);

				for (int i = 0; i < cooldownPlayers.size(); i++) {
					CooldownPlayer cooldownPlayer = cooldownPlayers.get(i);
					Cooldown cooldown = cooldownPlayer.getCooldown();

					cooldown.setTime(cooldown.getTime() - 1);

					if (cooldown.getTime() <= 0) {
						cooldownManager.deletePlayer(cooldownTypeList,
								Bukkit.getServer().getOfflinePlayer(cooldownPlayer.getUUID()));
					}
				}
			}
		}
	}
}
