package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.IslandRole;

public class Food implements Listener {

	private final SkyBlock skyblock;

	public Food(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();

		if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
			if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
					.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")
					&& !skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "Hunger")) {
				event.setCancelled(true);
			}
		}
	}
}
