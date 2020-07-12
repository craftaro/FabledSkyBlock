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

public class World implements Listener {
    
    private final SkyBlock plugin;
    
    public World(SkyBlock plugin) {
        this.plugin = plugin;
    }
    
    // Hotfix for wrong biome in other worlds;
    @EventHandler(ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        IslandManager islandManager = plugin.getIslandManager();
        BiomeManager biomeManager = plugin.getBiomeManager();
        
        Location to = event.getPlayer().getLocation();
        Island island = islandManager.getIslandAtLocation(to);
        
        if(island != null) {
            switch (to.getWorld().getEnvironment()) {
                case NORMAL:
                    break;
                case NETHER:
                    if(!to.getBlock().getBiome().equals(CompatibleBiome.NETHER_WASTES.getBiome())) {
                        biomeManager.setBiome(island, IslandWorld.Nether, CompatibleBiome.NETHER_WASTES.getBiome(), null);
                    }
                    break;
                case THE_END:
                    if(!to.getBlock().getBiome().equals(CompatibleBiome.THE_END.getBiome())) {
                        biomeManager.setBiome(island, IslandWorld.End, CompatibleBiome.THE_END.getBiome(), null);
                    }
                    break;
            }
        }
    }
}
