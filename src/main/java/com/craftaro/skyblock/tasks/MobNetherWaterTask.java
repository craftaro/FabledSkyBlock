package com.craftaro.skyblock.tasks;

import com.craftaro.core.compatibility.MajorServerVersion;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
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
        if (!plugin.getConfiguration().getBoolean("Island.Nether.WaterDisappearWithNetherMobs", false))
            return;

        for (World world : Bukkit.getServer().getWorlds()) {
            if (!plugin.getWorldManager().isIslandWorld(world) || plugin.getWorldManager().getIslandWorld(world) != IslandWorld.NETHER)
                continue;

            for (Entity ent : world.getEntities())
                if (isNetherMob(ent))
                    removeWaterAround(world, ent.getLocation().getBlock());
        }
    }

    private boolean isNetherMob(Entity ent) {
        if (ent instanceof Blaze || ent instanceof MagmaCube || ent instanceof Wither || ent instanceof Ghast)
            return true;

        if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_11)) {
            return ent.getType() == EntityType.WITHER_SKELETON;
        } else {
            return ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType() == Skeleton.SkeletonType.WITHER;
        }
    }

    private void removeWaterAround(World world, Block block) {
        removeWater(world, block);
        removeWater(world, block.getRelative(BlockFace.UP));
    }

    private void removeWater(World world, Block block) {
        if (block.getType() != Material.WATER)
            return;
        block.setType(Material.AIR, true);
        XSound.BLOCK_FIRE_EXTINGUISH.play(block.getLocation());
        world.playEffect(block.getLocation(), Effect.SMOKE, 1);
    }

    public void onDisable() {
    }
}
