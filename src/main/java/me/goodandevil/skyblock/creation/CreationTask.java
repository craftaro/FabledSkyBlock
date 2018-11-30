package me.goodandevil.skyblock.creation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;

public class CreationTask extends BukkitRunnable {

	private final SkyBlock skyblock;
	
 	protected CreationTask(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
 	
	@Override
	public void run() {
		CreationManager creationManager = skyblock.getCreationManager();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (creationManager.hasPlayer(all)) {
				Creation creation = creationManager.getPlayer(all);
				creation.setTime(creation.getTime() - 1);
				
				if (creation.getTime() <= 0) {
					creationManager.removePlayer(all);
				}
			}
		}
	}
}
