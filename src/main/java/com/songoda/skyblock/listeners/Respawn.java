package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.logging.Level;

public class Respawn implements Listener {

    private final SkyBlock plugin;

    public Respawn(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();
        FileManager fileManager = plugin.getFileManager();

        if (worldManager.isIslandWorld(player.getWorld())) {
            FileConfiguration configLoad = plugin.getConfiguration();

            if (configLoad.getBoolean("Island.Death.Respawn.Island")) {
                Location playerLocation = player.getLocation();
                Island island = islandManager.getIslandAtLocation(playerLocation);

                if (island != null) {
                    Location islandLocation;
                    IslandWorld world = worldManager.getIslandWorld(player.getWorld());

                    if (island.hasRole(IslandRole.Member, player.getUniqueId())
                            || island.hasRole(IslandRole.Operator, player.getUniqueId())
                            || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                        islandLocation = island.getLocation(world, IslandEnvironment.Main);
                    } else {
                        islandLocation = island.getLocation(world, IslandEnvironment.Visitor);
                    }

                    Bukkit.getServer().getPluginManager()
                            .callEvent(new PlayerTeleportEvent(player, playerLocation, islandLocation));
                    event.setRespawnLocation(islandLocation);

                    islandManager.updateFlight(player);

                    return;
                }
            }

            Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml"));

            if (config.getFileConfiguration().getString("Location.Spawn") == null) {
                Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: A spawn point hasn't been set.");
            } else {
                Location playerLocation = player.getLocation(), spawnLocation = fileManager.getLocation(config, "Location.Spawn", true);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerTeleportEvent(player, playerLocation, spawnLocation));
                event.setRespawnLocation(spawnLocation);
            }
        }
    }
}
