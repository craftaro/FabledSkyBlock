package com.songoda.skyblock.utils.world;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.math.VectorUtil;
import com.songoda.skyblock.utils.world.block.BlockDegreesType;
import com.songoda.skyblock.world.WorldManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public final class LocationUtil {

    public static void removeWaterFromLoc(Location loc) {
        Location tempLoc = LocationUtil.getDefinitiveLocation(loc.clone());
        if(tempLoc.getBlock().getType().equals(Material.WATER)){
            tempLoc.getBlock().setType(Material.AIR);
        } else if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)){
            LocationUtil113.removeWaterLoggedFromLocation(tempLoc);
        }
    }

    public static @Nullable Location getSafeLocation(@Nonnull Location loc){
        Location locChecked = null;
        boolean found = false;
        if(loc.getWorld() != null){
            locChecked = loc.clone();
            loc.getWorld().loadChunk(loc.getWorld().getChunkAt(loc));
            for(int i=loc.getBlockY(); i>=0 && !found; i--){
                locChecked = locChecked.subtract(0d, 1d, 0d);
                found = checkBlock(locChecked);
            }
            if(!found){
                for(int i=loc.getBlockY(); i<256 && !found; i++){
                    locChecked = locChecked.add(0d, 1d, 0d);
                    found = checkBlock(locChecked);
                }
            }
            if (found) {
                locChecked = locChecked.add(0d,1d,0d);
            } else {
                locChecked = null;
            }
        }
        return locChecked;
    }

    public static @Nonnull Location getDefinitiveLocation(@Nonnull Location loc){
        Location locWorking = loc.clone();
        for(locWorking.setY(locWorking.getBlockY()); locWorking.getBlockY()>=0; locWorking.setY(locWorking.getBlockY()-1)){
            if(!locWorking.getBlock().isEmpty()){
                if(locWorking.getBlock().getType().equals(CompatibleMaterial.WATER.getMaterial()) ||
                        (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) && 
                                locWorking.getBlock().getBlockData() instanceof org.bukkit.block.data.Waterlogged)){
                    loc = locWorking;
                }
                break;
            }
        }
        return loc;
    }

    private static boolean checkBlock(Location locChecked) {
        boolean safe = false;
        if(!locChecked.getBlock().isEmpty() &&
                !locChecked.getBlock().isLiquid() &&
                locChecked.getBlock().getType().isSolid() &&
                locChecked.getBlock().getType().isBlock() &&
                locChecked.add(0d,1d,0d).getBlock().getType().equals(CompatibleMaterial.AIR.getMaterial()) &&
                locChecked.add(0d,2d,0d).getBlock().getType().equals(CompatibleMaterial.AIR.getMaterial()) &&
                !(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) && locChecked.getBlock().getBlockData() instanceof org.bukkit.block.data.Waterlogged)){
            safe = true;
            switch(CompatibleMaterial.getMaterial(locChecked.getBlock())){
                case ACACIA_DOOR: // <= 1.8.8
                case ACACIA_FENCE_GATE:
                case BIRCH_DOOR:
                case BIRCH_FENCE_GATE:
                case CACTUS:
                case CAKE:
                case DARK_OAK_DOOR:
                case DARK_OAK_FENCE_GATE:
                case IRON_TRAPDOOR:
                case JUNGLE_DOOR:
                case JUNGLE_FENCE_GATE:
                case LADDER:
                case SPRUCE_DOOR:
                case SPRUCE_FENCE_GATE:
                case ACACIA_BUTTON:
                case ACACIA_TRAPDOOR:
                case BIRCH_TRAPDOOR:
                case CAMPFIRE:
                case COBWEB:
                case DARK_OAK_TRAPDOOR:
                case JUNGLE_TRAPDOOR:
                case MAGMA_BLOCK:
                case NETHER_PORTAL:
                case OAK_DOOR:
                case OAK_FENCE_GATE:
                    safe = false;
                    break;
            }
        }

        return safe;
    }

    public static boolean isLocationLocation(Location location1, Location location2) {
        return location1.getBlockX() == location2.getBlockX() && location1.getBlockY() == location2.getBlockY() && location1.getBlockZ() == location2.getBlockZ();
    }

    public static boolean isLocationAffectingIslandSpawn(Location location, Island island, IslandWorld world) {
        return isLocationAffectingLocation(location, island.getLocation(world, IslandEnvironment.Main))
                || isLocationAffectingLocation(location, island.getLocation(world, IslandEnvironment.Visitor));
    }

    private static boolean isLocationAffectingLocation(Location location1, Location location2) {
        location2 = location2.clone();

        /*
         * First isLocationLocation() call is HeadHeight, second feetHeight, and the
         * final one is GroundHeight.
         */
        return isLocationLocation(location2.add(0, 1, 0), location1) || isLocationLocation(location2.subtract(0, 1, 0), location1)
                || isLocationLocation(location2.subtract(0, 1, 0), location1);
    }

    public static boolean isLocationInLocationRadius(Location location1, Location location2, double radius) {
        if (location1 == null || location2 == null || location1.getWorld() == null || location2.getWorld() == null
                || !location1.getWorld().getName().equals(location2.getWorld().getName())) {
            return false;
        }
        double x = Math.abs(location1.getX() - location2.getX());
        double z = Math.abs(location1.getZ() - location2.getZ());
        
        return x < radius && z < radius;
    }

    public static List<Location> getLocations(Location minLocation, Location maxLocation) {

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        int minY = Math.min(maxLocation.getBlockY(), minLocation.getBlockY());
        int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        int maxY = Math.max(maxLocation.getBlockY(), minLocation.getBlockY());
        int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        List<Location> locations = new ArrayList<>(maxX + maxY + maxZ + 3);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    locations.add(new Location(minLocation.getWorld(), x, y, z));
                }
            }
        }

        return locations;
    }

    public static boolean isInsideArea(Location targetLocation, Location minLocation, Location maxLocation) {
        int MinX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        int MinY = Math.min(maxLocation.getBlockY(), minLocation.getBlockY());
        int MinZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        int MaxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        int MaxY = Math.max(maxLocation.getBlockY(), minLocation.getBlockY());
        int MaxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        return MinX < targetLocation.getX() && MaxX > targetLocation.getX() && MinY < targetLocation.getY() && MaxY > targetLocation.getY() && MinZ < targetLocation.getZ()
                && MaxZ > targetLocation.getZ();
    }

    public static Location getHighestBlock(Location location) {
        for (int y = location.getWorld().getMaxHeight(); y > 0; y--) {
            location.setY(y);

            Block block = location.getBlock();

            if (!(block.getType() == Material.AIR)) {
                return location;
            }
        }

        return location;
    }

    public static int getYSurface(Location location, boolean isNether) {
        int maxY = 0;
        boolean followY = false;

        final int blockX = location.getBlockX();
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();

        for (int y = 0; y < location.getWorld().getMaxHeight(); y++) {
            final Block block = world.getBlockAt(blockX, y, blockZ).getRelative(BlockFace.UP);

            if (isNether) {
                if (y < 127 && (block.getType() == Material.LAVA || block.getType() == CompatibleMaterial.LAVA.getMaterial() || block.getType() == Material.AIR)) {
                    maxY = y;
                    break;
                }
            } else {
                if (block.getType() == CompatibleMaterial.OAK_LEAVES.getMaterial() || block.getType() == CompatibleMaterial.ACACIA_LEAVES.getMaterial()) {
                    break;
                }

                if (block.getType() == Material.AIR || block.getType() == CompatibleMaterial.WATER.getMaterial() || block.getType() == Material.WATER
                        || block.getType() == CompatibleMaterial.LAVA.getMaterial() || block.getType() == Material.LAVA) {
                    if (!followY) {
                        maxY = y;
                        followY = true;
                    }
                } else {
                    followY = false;
                    maxY = 0;
                }
            }
        }

        return maxY;
    }

    public static double rotateYaw(double a, double b) throws Exception {
        if (a < -180 || a > 180) {
            throw new Exception();
        }

        double c = a + b;
        return (c > 180) ? -(c - 180) : 180 - c;
    }

    public static double rotatePitch(double a, double b) throws Exception {
        if (a < -90 || a > 90) {
            throw new Exception();
        }

        double c = a + b;
        return (c > 90) ? -(c - 90) : 90 - c;
    }

    public static Location rotateLocation(Location location, BlockDegreesType blockTypeDegrees) {
        if (blockTypeDegrees == BlockDegreesType.ROTATE_90) {
            return VectorUtil.rotateAroundAxisY(location.toVector(), 90).toLocation(location.getWorld());
        } else if (blockTypeDegrees == BlockDegreesType.ROTATE_180) {
            return VectorUtil.rotateAroundAxisY(location.toVector(), 180).toLocation(location.getWorld());
        } else if (blockTypeDegrees == BlockDegreesType.ROTATE_270) {
            return VectorUtil.rotateAroundAxisY(location.toVector(), 270).toLocation(location.getWorld());
        } else {
            return location;
        }
    }

    public static void teleportPlayerToSpawn(Player player) {
        SkyBlock plugin = SkyBlock.getInstance();

        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml"));

        if (config.getFileConfiguration().getString("Location.Spawn") == null) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: A spawn point hasn't been set.");
        } else {
            Location spawnLocation = getSpawnLocation();

            if (spawnLocation == null) {
                Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: The location for the spawn point could not be found.");

                return;
            }

            // If the spawn point is at an island, load that island
            if (worldManager.isIslandWorld(spawnLocation.getWorld())) {
                Island island = islandManager.getIslandAtLocation(spawnLocation);
                if (island == null) {
                    islandManager.loadIslandAtLocation(spawnLocation);
                }
            }

            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                PaperLib.teleportAsync(player, spawnLocation);
                player.setFallDistance(0.0F);
            });
        }
    }

    public static Location getSpawnLocation() {
        SkyBlock plugin = SkyBlock.getInstance();

        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml"));

        if (config.getFileConfiguration().getString("Location.Spawn") != null) {
            Location location = fileManager.getLocation(config, "Location.Spawn", true);

            if (location != null && location.getWorld() != null) {
                return location;
            }
        }

        return null;
    }

    public static Location getRandomLocation(World world, int xRange, int zRange, boolean loadChunk, boolean ignoreLiquid) {
        Random rnd = new Random();

        int rndX = rnd.nextInt(xRange);
        int rndZ = rnd.nextInt(zRange);

        if (loadChunk) world.getChunkAt(new Location(world, rndX, 10, rndZ));

        double rndY = -1;

        if (world.getEnvironment() == Environment.NETHER) {

            Location rndLoc = new Location(world, rndX, 0, rndZ);

            for (int i = 120; i > 0; i--) {
                rndLoc.setY(i);

                if (rndLoc.getBlock().getType() != Material.AIR && rndLoc.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.AIR
                        && rndLoc.clone().add(0.0D, 2.0D, 0.0D).getBlock().getType() == Material.AIR && rndLoc.clone().add(0.0D, 3.0D, 0.0D).getBlock().getType() == Material.AIR
                        && rndLoc.clone().add(0.0D, 4.0D, 0.0D).getBlock().getType() == Material.AIR) {
                    rndY = i;
                    break;
                }
            }

            if (rndY == -1) {
                return getRandomLocation(world, xRange, zRange, loadChunk, ignoreLiquid);
            }
        } else {
            rndY = world.getHighestBlockYAt(rndX, rndZ);
        }

        Location rndLoc = new Location(world, rndX, rndY, rndZ);

        if (ignoreLiquid && rndLoc.getBlock().isLiquid() || rndLoc.getBlock().getRelative(BlockFace.DOWN).isLiquid()) {
            return getRandomLocation(world, xRange, zRange, loadChunk, ignoreLiquid);
        } else {
            return rndLoc;
        }
    }
    
    public static Location toCenterLocation(Location loc) {
        Location centerLoc = loc.clone();
        centerLoc.setX((double)loc.getBlockX() + 0.5D);
        centerLoc.setY((double)loc.getBlockY() + 0.5D);
        centerLoc.setZ((double)loc.getBlockZ() + 0.5D);
        return centerLoc;
    }
    
    public static Location toBlockLocation(Location loc) {
        Location blockLoc = loc.clone();
        blockLoc.setX(loc.getBlockX());
        blockLoc.setY(loc.getBlockY());
        blockLoc.setZ(loc.getBlockZ());
        return blockLoc;
    }
}
