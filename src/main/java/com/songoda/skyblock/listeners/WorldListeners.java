package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldListeners implements Listener {
    private final SkyBlock plugin;

    public WorldListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    // Hotfix for wrong biome in other worlds;
    @EventHandler(ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        IslandManager islandManager = this.plugin.getIslandManager();
        BiomeManager biomeManager = this.plugin.getBiomeManager();

        Location to = event.getPlayer().getLocation();
        Island island = islandManager.getIslandAtLocation(to);

        if (island != null) {
            switch (to.getWorld().getEnvironment()) {
                case NORMAL:
                    break;
                case NETHER:
                    if (to.getBlock().getBiome() != CompatibleBiome.NETHER_WASTES.getBiome()) {
                        biomeManager.setBiome(island, IslandWorld.NETHER, CompatibleBiome.NETHER_WASTES, null);
                    }
                    break;
                case THE_END:
                    if (to.getBlock().getBiome() != CompatibleBiome.THE_END.getBiome()) {
                        biomeManager.setBiome(island, IslandWorld.END, CompatibleBiome.THE_END, null);
                    }
                    break;
            }
        }
    }
}
