package me.goodandevil.skyblock.creation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;

public class CreationTask extends BukkitRunnable {

	private final Main plugin;
	
 	protected CreationTask(Main plugin) {
		this.plugin = plugin;
	}
 	
	@Override
	public void run() {
		/*CreationManager creationManager = plugin.getCreationManager();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (creationManager.hasPlayer(all)) {
				Creation creation = creationManager.getPlayer(all);
				creation.setTime(creation.getTime() - 1);
				
				if (creation.getTime() == 0) {
					creationManager.removePlayer(all);
				}
			}
		}*/
	}
}
