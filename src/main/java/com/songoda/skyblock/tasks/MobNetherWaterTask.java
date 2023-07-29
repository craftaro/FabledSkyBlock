package com.songoda.skyblock.tasks;

import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Strider;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zoglin;
import org.bukkit.scheduler.BukkitRunnable;

public class MobNetherWaterTask extends BukkitRunnable {
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
        if (plugin.getConfiguration().getBoolean("Island.Nether.WaterDisappearWithNetherMobs", false)) {
            for (World world : Bukkit.getServer().getWorlds()) {
                if (plugin.getWorldManager().isIslandWorld(world) && plugin.getWorldManager().getIslandWorld(world) == IslandWorld.NETHER) {
                    for (Entity ent : world.getEntities()) {
                        boolean witherSkeleton;
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                            witherSkeleton = ent.getType() == EntityType.WITHER_SKELETON;
                        } else {
                            witherSkeleton = ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType() == Skeleton.SkeletonType.WITHER;
                        }
                        if ((((ent instanceof Blaze || ent instanceof MagmaCube) || ent instanceof Wither) || ent instanceof Ghast) || witherSkeleton) {
                            Block block = ent.getLocation().getBlock();
                            removeWater(world, block);
                            removeWater(world, block.getRelative(BlockFace.UP));
                        } else {
                            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
                                if (((ent instanceof Piglin || ent instanceof Hoglin) || ent instanceof Strider) || ent instanceof Zoglin) {
                                    Block block = ent.getLocation().getBlock();
                                    removeWater(world, block);
                                    removeWater(world, block.getRelative(BlockFace.UP));
                                }
                            } else {
                                if (ent instanceof PigZombie) {
                                    Block block = ent.getLocation().getBlock();
                                    removeWater(world, block);
                                    removeWater(world, block.getRelative(BlockFace.UP));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeWater(World world, Block block) {
        if (block.getType() == Material.WATER) {
            block.setType(Material.AIR, true);
            XSound.BLOCK_FIRE_EXTINGUISH.play(block.getLocation());
            world.playEffect(block.getLocation(), Effect.SMOKE, 1);
        }
    }

    public void onDisable() {
    }
}
