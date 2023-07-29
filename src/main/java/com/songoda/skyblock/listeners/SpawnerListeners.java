package com.songoda.skyblock.listeners;

import com.craftaro.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.upgrade.Upgrade;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class SpawnerListeners implements Listener {
    private final SkyBlock plugin;

    public SpawnerListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnSpawn(SpawnerSpawnEvent event) {
        IslandManager islandManager = this.plugin.getIslandManager();

        CreatureSpawner spawner = event.getSpawner();
        org.bukkit.Location location = spawner.getBlock().getLocation();

        if (!this.plugin.getWorldManager().isIslandWorld(location.getWorld())) {
            return;
        }

        Island island = islandManager.getIslandAtLocation(location);
        if (island == null) {
            return;
        }

        List<Upgrade> upgrades = this.plugin.getUpgradeManager().getUpgrades(Upgrade.Type.SPAWNER);

        if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()
                && island.isUpgrade(Upgrade.Type.SPAWNER)) {
            if (ServerVersion.isServerVersionAbove(ServerVersion.V1_12)) {
                if (spawner.getDelay() == 20) {
                    spawner.setDelay(10);
                }

                spawner.setMinSpawnDelay(100);
                spawner.setMaxSpawnDelay(400);
            } else {
                try {
                    Object mobSpawner;

                    try {
                        Field tileEntityMobSpawnerField = spawner.getClass().getDeclaredField("spawner");
                        tileEntityMobSpawnerField.setAccessible(true);
                        Object tileEntityMobSpawner = tileEntityMobSpawnerField.get(spawner);
                        mobSpawner = tileEntityMobSpawner.getClass().getMethod("getSpawner")
                                .invoke(tileEntityMobSpawner);
                    } catch (NoSuchFieldException ignored) {
                        Field snapshotField = spawner.getClass().getSuperclass().getDeclaredField("snapshot");
                        snapshotField.setAccessible(true);
                        Object snapshot = snapshotField.get(spawner);
                        mobSpawner = snapshot.getClass().getMethod("getSpawner").invoke(snapshot);
                    }

                    int spawnDelay = (int) mobSpawner.getClass().getSuperclass().getField("spawnDelay")
                            .get(mobSpawner);

                    if (spawnDelay == 20) {
                        Field spawnDelayField = mobSpawner.getClass().getSuperclass().getField("spawnDelay");
                        spawnDelayField.setAccessible(true);
                        spawnDelayField.set(mobSpawner, 10);
                    }

                    Field minSpawnDelayField = mobSpawner.getClass().getSuperclass()
                            .getDeclaredField("minSpawnDelay");
                    minSpawnDelayField.setAccessible(true);
                    int minSpawnDelay = (int) minSpawnDelayField.get(mobSpawner);

                    if (minSpawnDelay != 100) {
                        minSpawnDelayField.set(mobSpawner, 100);
                    }

                    Field maxSpawnDelayField = mobSpawner.getClass().getSuperclass()
                            .getDeclaredField("maxSpawnDelay");
                    maxSpawnDelayField.setAccessible(true);
                    int maxSpawnDelay = (int) maxSpawnDelayField.get(mobSpawner);

                    if (maxSpawnDelay != 400) {
                        maxSpawnDelayField.set(mobSpawner, 400);
                    }
                } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                         | SecurityException | InvocationTargetException | NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
            }

            spawner.update();
        }
    }
}
