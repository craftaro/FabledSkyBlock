package com.songoda.skyblock.tasks;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

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
        if (plugin.getConfiguration().getBoolean("Island.Nether.WaterDisappearWithNetherMobs", false)){
            for(World world : Bukkit.getServer().getWorlds()){
                if(plugin.getWorldManager().isIslandWorld(world) && plugin.getWorldManager().getIslandWorld(world).equals(IslandWorld.Nether)){
                    for(Entity ent : world.getEntities()) {
                        boolean witherSkeleton;
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                            witherSkeleton = ent.getType().equals(EntityType.WITHER_SKELETON);
                        } else {
                            witherSkeleton = ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType().equals(Skeleton.SkeletonType.WITHER);
                        }
                        if((((ent instanceof Blaze || ent instanceof MagmaCube) || ent instanceof Wither) || ent instanceof Ghast) || witherSkeleton){
                            Block block = ent.getLocation().getBlock();
                            removeWater(world, block);
                            removeWater(world, block.getRelative(BlockFace.UP));
                        } else {
                            if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)){
                                if(((ent instanceof Piglin || ent instanceof Hoglin) || ent instanceof Strider) || ent instanceof Zoglin) {
                                    Block block = ent.getLocation().getBlock();
                                    removeWater(world, block);
                                    removeWater(world, block.getRelative(BlockFace.UP));
                                }
                            } else {
                                if(ent instanceof PigZombie) {
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
        if (block.getType().equals(Material.WATER)) {
            block.setType(Material.AIR, true);
            block.getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
            world.playEffect(block.getLocation(), Effect.SMOKE, 1);
        }
    }

    public void onDisable() {
    }


}
