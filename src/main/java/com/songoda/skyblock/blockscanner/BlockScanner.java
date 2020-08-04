package com.songoda.skyblock.blockscanner;

import com.google.common.collect.Lists;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class BlockScanner extends BukkitRunnable {

    private static final Method ID_FIELD;

    static {
        Method temp = null;

        try {
            temp = ChunkSnapshot.class.getMethod("getBlockTypeId", int.class, int.class, int.class);
        } catch (NoSuchMethodException ignored) {}

        ID_FIELD = temp;
    }

    public static int getBlockTypeID(ChunkSnapshot snapshot, int x, int y, int z) {

        int id = 0;

        try {
            id = (Integer) ID_FIELD.invoke(snapshot, x, y, z);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return id;
    }

    private final AtomicInteger completedNum;

    private final int threadCount;
    private final Queue<BlockInfo> blocks;
    private final ScannerTasks tasks;
    
    private final Island island;
    
    private final boolean ignoreLiquids;
    private final boolean ignoreAir;

    private BlockScanner(Map<World, List<ChunkSnapshot>> snapshots,
                         Island island,
                         boolean ignoreLiquids,
                         boolean ignoreLiquidsY,
                         boolean ignoreAir,
                         boolean ignoreY,
                         ScannerTasks tasks) {
        this.ignoreLiquids = ignoreLiquids;
        this.ignoreAir = ignoreAir;
        this.blocks = new ConcurrentLinkedQueue<>();
        this.tasks = tasks;
        this.completedNum = new AtomicInteger();
        this.island = island;

        FileConfiguration config = SkyBlock.getInstance().getFileManager().getConfig(new File(SkyBlock.getInstance().getDataFolder(), "config.yml")).getFileConfiguration();

        int threadCount = 0;

        for (Entry<World, List<ChunkSnapshot>> entry : snapshots.entrySet()) {

            final List<List<ChunkSnapshot>> parts = Lists.partition(entry.getValue(), 16);
            
            threadCount += parts.size();

            World world = entry.getKey();
            final String env;

            switch (world.getEnvironment()) {
            case NETHER:
                env = "Nether";
                break;
            case THE_END:
                env = "End";
                break;
            default:
                env = "Normal";
                break;
            }

            final ConfigurationSection liquidSection = config.getConfigurationSection("Island.World." + env + ".Liquid");

            int startY;
            if(ignoreY){
                startY = 255;
            } else {
                startY = !ignoreLiquidsY && liquidSection.getBoolean("Enable") && !config.getBoolean("Island.Levelling.ScanLiquid") ? liquidSection.getInt("Height") + 1 : 0;
            }

            for (List<ChunkSnapshot> sub : parts) {
               queueWork(world, startY, sub);
            }
        }

        this.threadCount = threadCount;
    }

    private void queueWork(World world, int scanY, List<ChunkSnapshot> subList) {
        WorldManager worldManager = SkyBlock.getInstance().getWorldManager();
        
        Bukkit.getServer().getScheduler().runTaskAsynchronously(SkyBlock.getInstance(), () -> {
            LocationBounds bounds = null;
            if(island != null) {
                Location islandLocation = island.getLocation(worldManager.getIslandWorld(world), IslandEnvironment.Island);
                
                Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
                Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());
    
                int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
                int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());
    
                int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
                int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());
    
                bounds = new LocationBounds(minX, minZ, maxX, maxZ);
            }
            for (ChunkSnapshot shot : subList) {
                final int cX = shot.getX() << 4;
                final int cZ = shot.getZ() << 4;
                
                int initX = 0;
                int initZ = 0;
                int lastX = 15;
                int lastZ = 15;
    
                if(bounds != null) {
                    initX = Math.max(cX, bounds.getMinX())&0x000F;
                    initZ = Math.max(cZ, bounds.getMinZ())&0x000F;
    
                    lastX = Math.min(cX | 15, bounds.getMaxX()-1)&0x000F;
                    lastZ = Math.min(cZ | 15, bounds.getMaxZ()-1)&0x000F;
                }
    
                int finalInitX = initX;
                int finalInitZ = initZ;
                int finalLastZ = lastZ;
                int finalLastX = lastX;
                Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
                    world.getChunkAt(shot.getX(), shot.getZ()).getBlock(finalInitX, 80, finalInitZ).setType(CompatibleMaterial.GOLD_BLOCK.getBlockMaterial());
                    world.getChunkAt(shot.getX(), shot.getZ()).getBlock(finalLastX, 80, finalLastZ).setType(CompatibleMaterial.CRYING_OBSIDIAN.getBlockMaterial());
                    Bukkit.getScheduler().runTaskLater(SkyBlock.getInstance(), () -> {
                        world.getChunkAt(shot.getX(), shot.getZ()).getBlock(finalInitX, 80, finalInitZ).setType(CompatibleMaterial.AIR.getBlockMaterial());
                        world.getChunkAt(shot.getX(), shot.getZ()).getBlock(finalLastX, 80, finalLastZ).setType(CompatibleMaterial.AIR.getBlockMaterial());
                    }, 300L);
                });
                
                for (int x = initX; x <= lastX; x++) {
                    for (int z = initZ; z <= lastZ; z++) {
                        for (int y = scanY; y < world.getMaxHeight(); y++) {
                            final CompatibleMaterial type = CompatibleMaterial.getBlockMaterial(
                                    ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                    ? shot.getBlockType(x, y, z) :
                                            MaterialIDHelper.getLegacyMaterial(getBlockTypeID(shot, x, y, z)));
                            
                            if(type == null){
                                continue;
                            } else if(type.equals(CompatibleMaterial.AIR) && ignoreAir){
                                continue;
                            } else if(type.equals(CompatibleMaterial.WATER) && ignoreLiquids){
                                continue;
                            }

                            blocks.add(new BlockInfo(world, x + (cX), y, z + (cZ)));
                        }
                    }
                }
            }
            increment();
        });
    }

    private synchronized int increment() {
        return completedNum.getAndIncrement();
    }

    private synchronized int get() {
        return completedNum.get();
    }

    @Override
    public void run() {
        if (get() != threadCount) return;

        tasks.onComplete(blocks);
        cancel();
    }

    public static void startScanner(Map<World, List<ChunkSnapshot>> snapshots, Island island, boolean ignoreLiquids, boolean ignoreLiquidsY, boolean ignoreAir, boolean ignoreY, ScannerTasks tasks) {

        if (snapshots == null) throw new IllegalArgumentException("snapshots cannot be null");
        if (tasks == null) throw new IllegalArgumentException("tasks cannot be null");

        final BlockScanner scanner = new BlockScanner(snapshots, island, ignoreLiquids, ignoreLiquidsY, ignoreAir, ignoreY, tasks);

        scanner.runTaskTimer(SkyBlock.getInstance(), 5, 5);
    }

    public interface ScannerTasks {

        void onComplete(Queue<BlockInfo> blocks);

    }

}
