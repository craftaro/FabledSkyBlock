package com.songoda.skyblock.tasks;

import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.hologram.Hologram;
import com.songoda.skyblock.hologram.HologramType;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MobNetherWaterTask  extends BukkitRunnable {

    private static MobNetherWaterTask instance;
    private static SkyBlock plugin;

    public MobNetherWaterTask(SkyBlock plug) {
        plugin = plug;
    }

    public static MobNetherWaterTask startTask(SkyBlock plug) {
        plugin = plug;
        if (instance == null) {
            instance = new MobNetherWaterTask(plugin);
            instance.runTaskTimer(plugin, 0, 2L);
        }

        return instance;
    }

    @Override
    public void run() {
        FileManager fileManager = plugin.getFileManager();
        if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Nether.WaterDisappearWithNetherMobs", false)){
            for(World world : Bukkit.getServer().getWorlds()){
                if(plugin.getWorldManager().isIslandWorld(world) && plugin.getWorldManager().getIslandWorld(world).equals(IslandWorld.Nether)){
                    for(Entity ent : world.getEntities()){
                        switch(ent.getType()){
                            case PIG_ZOMBIE:
                            case BLAZE:
                            case MAGMA_CUBE:
                            case WITHER_SKELETON:
                            case WITHER:
                            case GHAST:
                                Block block = ent.getLocation().getBlock();
                                if(block.getType().equals(Material.WATER)){
                                    block.setType(Material.AIR, true);
                                    world.playSound(block.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
                                    world.playEffect(block.getLocation(), Effect.SMOKE, 1);
                                }
                                block = block.getRelative(BlockFace.UP);
                                if(block.getType().equals(Material.WATER)){
                                    block.setType(Material.AIR, true);
                                    world.playSound(block.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
                                    world.playEffect(block.getLocation(), Effect.SMOKE, 1);
                                }
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public void onDisable() {
    }


}
