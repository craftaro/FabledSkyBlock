package me.goodandevil.skyblock.biome;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;

public class BiomeTask extends BukkitRunnable {

	private final Main plugin;
	
 	protected BiomeTask(Main plugin) {
		this.plugin = plugin;
	}
 	
	@Override
	public void run() {
		BiomeManager biomeManager = plugin.getBiomeManager();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (biomeManager.hasPlayer(all)) {
				me.goodandevil.skyblock.biome.Biome biome = biomeManager.getPlayer(all);
				biome.setTime(biome.getTime() - 1);
				
				if (biome.getTime() == 0) {
					biomeManager.removePlayer(all);
				}
			}
		}
	}
}
