package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.Location;

public class Food implements Listener {

	private final SkyBlock skyblock;

	public Food(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();

		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
					.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")
					&& !skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "Hunger")) {
				event.setCancelled(true);
			}
		}
	}
}
