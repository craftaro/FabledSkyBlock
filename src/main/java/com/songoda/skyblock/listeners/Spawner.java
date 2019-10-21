package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Spawner implements Listener {

    private final SkyBlock skyblock;

    public Spawner(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onSpawnSpawn(SpawnerSpawnEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();

        CreatureSpawner spawner = event.getSpawner();
        org.bukkit.Location location = spawner.getBlock().getLocation();

        if (skyblock.getWorldManager().isIslandWorld(location.getWorld())) {
            Island island = islandManager.getIslandAtLocation(location);

            if (island != null) {
                List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Spawner);

                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()
                        && island.isUpgrade(Upgrade.Type.Spawner)) {
                    if (NMSUtil.getVersionNumber() > 12) {
                        if (spawner.getDelay() == 20) {
                            spawner.setDelay(10);
                        }

                        spawner.setMinSpawnDelay(100);
                        spawner.setMaxSpawnDelay(400);
                    } else {
                        try {
                            Object MobSpawner = null;

                            try {
                                Field TileEntityMobSpawnerField = spawner.getClass().getDeclaredField("spawner");
                                TileEntityMobSpawnerField.setAccessible(true);
                                Object TileEntityMobSpawner = TileEntityMobSpawnerField.get(spawner);
                                MobSpawner = TileEntityMobSpawner.getClass().getMethod("getSpawner")
                                        .invoke(TileEntityMobSpawner);
                            } catch (NoSuchFieldException e) {
                                Field snapshotField = spawner.getClass().getSuperclass().getDeclaredField("snapshot");
                                snapshotField.setAccessible(true);
                                Object snapshot = snapshotField.get(spawner);
                                MobSpawner = snapshot.getClass().getMethod("getSpawner").invoke(snapshot);
                            }

                            int spawnDelay = (int) MobSpawner.getClass().getSuperclass().getField("spawnDelay")
                                    .get(MobSpawner);

                            if (spawnDelay == 20) {
                                Field spawnDelayField = MobSpawner.getClass().getSuperclass().getField("spawnDelay");
                                spawnDelayField.setAccessible(true);
                                spawnDelayField.set(MobSpawner, 10);
                            }

                            Field minSpawnDelayField = MobSpawner.getClass().getSuperclass()
                                    .getDeclaredField("minSpawnDelay");
                            minSpawnDelayField.setAccessible(true);
                            int minSpawnDelay = (int) minSpawnDelayField.get(MobSpawner);

                            if (minSpawnDelay != 100) {
                                minSpawnDelayField.set(MobSpawner, 100);
                            }

                            Field maxSpawnDelayField = MobSpawner.getClass().getSuperclass()
                                    .getDeclaredField("maxSpawnDelay");
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

                return;
            }
        }
    }
}
