package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
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

public class RespawnListeners implements Listener {
    private final SkyBlock plugin;

    public RespawnListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();
        FileManager fileManager = this.plugin.getFileManager();

        if (worldManager.isIslandWorld(player.getWorld())) {
            FileConfiguration configLoad = this.plugin.getConfiguration();

            if (configLoad.getBoolean("Island.Death.Respawn.Island")) {
                Location playerLocation = player.getLocation();
                Island island = islandManager.getIslandAtLocation(playerLocation);

                if (island != null) {
                    Location islandLocation;
                    IslandWorld world = worldManager.getIslandWorld(player.getWorld());

                    if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                            || island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                            || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                        islandLocation = island.getLocation(world, IslandEnvironment.MAIN);
                    } else {
                        islandLocation = island.getLocation(world, IslandEnvironment.VISITOR);
                    }

                    Bukkit.getServer().getPluginManager()
                            .callEvent(new PlayerTeleportEvent(player, playerLocation, islandLocation));
                    event.setRespawnLocation(islandLocation);

                    islandManager.updateFlight(player);

                    return;
                }
            }

            Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "locations.yml"));

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
