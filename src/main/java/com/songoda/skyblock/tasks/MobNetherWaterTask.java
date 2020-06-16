package com.songoda.skyblock.tasks;

import com.songoda.core.compatibility.CompatibleSound;
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
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
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
                    for(Entity ent : world.getEntities()) {
                        boolean witherSkeleton;
                        if (NMSUtil.getVersionNumber() > 10) {
                            witherSkeleton = ent.getType().equals(EntityType.WITHER_SKELETON);
                        } else {
                            witherSkeleton = ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType().equals(Skeleton.SkeletonType.WITHER);
                        }
                        if (ent.getType().equals(EntityType.PIG_ZOMBIE) ||
                                ent.getType().equals(EntityType.BLAZE) ||
                                ent.getType().equals(EntityType.MAGMA_CUBE) ||
                                ent.getType().equals(EntityType.WITHER) ||
                                ent.getType().equals(EntityType.GHAST) ||
                                witherSkeleton) {
                            Block block = ent.getLocation().getBlock();
                            removeWater(world, block);
                            removeWater(world, block.getRelative(BlockFace.UP));
                        }
                    }
                }
            }
        }
    }

    private void removeWater(World world, Block block) {
        if (block.getType().equals(Material.WATER)) {
            block.setType(Material.AIR, true);
            block.getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
            world.playEffect(block.getLocation(), Effect.SMOKE, 1);
        }
    }

    public void onDisable() {
    }


}
