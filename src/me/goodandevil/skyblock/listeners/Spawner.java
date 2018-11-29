package me.goodandevil.skyblock.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Spawner implements Listener {

	private final SkyBlock skyblock;
	
	public Spawner(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@EventHandler
	public void onSpawnSpawn(SpawnerSpawnEvent event) {
		CreatureSpawner spawner = event.getSpawner();
		org.bukkit.Location location = spawner.getBlock().getLocation();
		
		if (location.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || location.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			IslandManager islandManager = skyblock.getIslandManager();
			
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(location, island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Spawner);
				    	
				    	if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled() && island.isUpgrade(Upgrade.Type.Spawner)) {
							if (NMSUtil.getVersionNumber() > 12) {
								if (spawner.getDelay() == 20) {
									spawner.setDelay(10);
								}
								
								spawner.setMinSpawnDelay(100);
								spawner.setMaxSpawnDelay(400);
							} else {
					    		try {
									Field TileEntityMobSpawnerField = spawner.getClass().getDeclaredField("spawner");
									TileEntityMobSpawnerField.setAccessible(true);
									Object TileEntityMobSpawner = TileEntityMobSpawnerField.get(spawner);
									Object MobSpawner = TileEntityMobSpawner.getClass().getMethod("getSpawner").invoke(TileEntityMobSpawner);
									
									int spawnDelay = (int) MobSpawner.getClass().getSuperclass().getField("spawnDelay").get(MobSpawner);
									
									if (spawnDelay == 20) {
										Field spawnDelayField = MobSpawner.getClass().getSuperclass().getField("spawnDelay");
										spawnDelayField.setAccessible(true);
										spawnDelayField.set(MobSpawner, 10);
									}
									
									Field minSpawnDelayField = MobSpawner.getClass().getSuperclass().getDeclaredField("minSpawnDelay");
									minSpawnDelayField.setAccessible(true);
									int minSpawnDelay = (int) minSpawnDelayField.get(MobSpawner);
									
									if (minSpawnDelay != 100) {
										minSpawnDelayField.set(MobSpawner, 100);
									}
									
									Field maxSpawnDelayField = MobSpawner.getClass().getSuperclass().getDeclaredField("maxSpawnDelay");
									maxSpawnDelayField.setAccessible(true);
									int maxSpawnDelay = (int) maxSpawnDelayField.get(MobSpawner);
									
									if (maxSpawnDelay != 400) {
										maxSpawnDelayField.set(MobSpawner, 400);
									}
								} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
										| SecurityException | InvocationTargetException | NoSuchMethodException e) {
									e.printStackTrace();
								}	
							}
				    	}
				    	
				    	break;
					}
				}
			}
		}
	}
}
