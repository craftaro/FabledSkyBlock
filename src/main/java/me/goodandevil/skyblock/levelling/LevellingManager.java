package me.goodandevil.skyblock.levelling;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.island.IslandLevelChangeEvent;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.stackable.Stackable;
import me.goodandevil.skyblock.stackable.StackableManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class LevellingManager {

    private final SkyBlock skyblock;

    private List<LevellingMaterial> materialStorage = new ArrayList<>();

    private Method getBlockTypeMethod = null;
    private Method getBlockTypeIdMethod = null;
    private Method getBlockTypeDataMethod = null;
    private Method getMaterialMethod = null;

    public LevellingManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        registerMaterials();
    }

    public void calculatePoints(Player player, Island island) {
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();
        StackableManager stackableManager = skyblock.getStackableManager();

        if (player != null && islandManager.getIslandPlayerAt(player) != island) {
            String message = ChatColor.translateAlternateColorCodes('&', this.skyblock.getFileManager()
                    .getConfig(new File(this.skyblock.getDataFolder(), "language.yml"))
                    .getFileConfiguration().getString("Command.Island.Level.Scanning.NotOnIsland.Message"));
            player.sendMessage(message);
            return;
        }

        Chunk chunk = new Chunk(skyblock, island);
        chunk.prepareInitial();

        int NMSVersion = NMSUtil.getVersionNumber();

        int height = 0;

        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            org.bukkit.World world = worldManager.getWorld(worldList);

            if (height == 0 || height > world.getMaxHeight()) {
                height = world.getMaxHeight();
            }
        }

        int worldMaxHeight = height;

        boolean isEpicSpawnersEnabled = Bukkit.getPluginManager().isPluginEnabled("EpicSpawners");
        boolean isWildStackerEnabled = Bukkit.getPluginManager().isPluginEnabled("WildStacker");

        Map<LevellingData, Long> levellingData = new HashMap<>();
        Set<Location> spawnerLocations = new HashSet<>(); // These have to be checked synchronously :(
        Set<Location> epicSpawnerLocations = new HashSet<>();

        List<Material> blacklistedMaterials = new ArrayList<>();
        blacklistedMaterials.add(Materials.AIR.getPostMaterial());
        blacklistedMaterials.add(Materials.WATER.getPostMaterial());
        blacklistedMaterials.add(Materials.LEGACY_STATIONARY_WATER.getPostMaterial());
        blacklistedMaterials.add(Materials.LAVA.getPostMaterial());
        blacklistedMaterials.add(Materials.LEGACY_STATIONARY_LAVA.getPostMaterial());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!chunk.isReadyToScan()) return;

                if (chunk.isFinished()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> finalizeMaterials(levellingData, spawnerLocations, epicSpawnerLocations, player, island), 1);
                    cancel();
                    return;
                }

                for (ChunkSnapshot chunkSnapshotList : chunk.getAvailableChunkSnapshots()) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < worldMaxHeight; y++) {
                                try {
                                    org.bukkit.Material blockMaterial = org.bukkit.Material.AIR;
                                    int blockData = 0;
                                    EntityType spawnerType = null;

                                    if (NMSVersion > 12) {
                                        if (getBlockTypeMethod == null) {
                                            getBlockTypeMethod = chunkSnapshotList.getClass()
                                                    .getMethod("getBlockType", int.class, int.class, int.class);
                                        }

                                        blockMaterial = (org.bukkit.Material) getBlockTypeMethod
                                                .invoke(chunkSnapshotList, x, y, z);
                                    } else {
                                        if (getBlockTypeIdMethod == null) {
                                            getBlockTypeIdMethod = chunkSnapshotList.getClass()
                                                    .getMethod("getBlockTypeId", int.class, int.class, int.class);
                                        }

                                        if (getBlockTypeDataMethod == null) {
                                            getBlockTypeDataMethod = chunkSnapshotList.getClass()
                                                    .getMethod("getBlockData", int.class, int.class, int.class);
                                        }

                                        if (getMaterialMethod == null) {
                                            getMaterialMethod = blockMaterial.getClass().getMethod("getMaterial",
                                                    int.class);
                                        }

                                        blockMaterial = (org.bukkit.Material) getMaterialMethod.invoke(
                                                blockMaterial,
                                                (int) getBlockTypeIdMethod.invoke(chunkSnapshotList, x, y, z));
                                        blockData = (int) getBlockTypeDataMethod.invoke(chunkSnapshotList, x, y, z);
                                    }

                                    if (blacklistedMaterials.contains(blockMaterial))
                                        continue;

                                    long amount = 1;

                                    if (blockMaterial == Materials.SPAWNER.parseMaterial()) {
                                        World world = Bukkit.getWorld(chunkSnapshotList.getWorldName());
                                        Location location = new Location(world, chunkSnapshotList.getX() * 16 + x, y, chunkSnapshotList.getZ() * 16 + z);

                                        if (isEpicSpawnersEnabled) {
                                            com.songoda.epicspawners.EpicSpawners epicSpawners = com.songoda.epicspawners.EpicSpawners.getInstance();
                                            if (epicSpawners.getSpawnerManager().isSpawner(location)) {
                                                com.songoda.epicspawners.spawners.spawner.Spawner spawner = epicSpawners.getSpawnerManager().getSpawnerFromWorld(location);
                                                if (spawner != null)
                                                    epicSpawnerLocations.add(location);
                                                continue;
                                            }
                                        }

//                                        if (isWildStackerEnabled) {
//                                            com.bgsoftware.wildstacker.api.handlers.SystemManager wildStacker = com.bgsoftware.wildstacker.api.WildStackerAPI.getWildStacker().getSystemManager();
//                                            com.bgsoftware.wildstacker.api.objects.StackedSpawner spawner = wildStacker.getStackedSpawner(location);
//                                            if (spawner != null) {
//                                                amount = spawner.getStackAmount();
//                                                spawnerType = spawner.getSpawnedType();
//                                            }
//                                        }

                                        if (spawnerType == null) {
                                            spawnerLocations.add(location);
                                            continue;
                                        }
                                    } else {
//                                        if (isWildStackerEnabled) {
//                                            World world = Bukkit.getWorld(chunkSnapshotList.getWorldName());
//                                            Location location = new Location(world, chunkSnapshotList.getX() * 16 + x, y, chunkSnapshotList.getZ() * 16 + z);
//                                            com.bgsoftware.wildstacker.api.handlers.SystemManager wildStacker = com.bgsoftware.wildstacker.api.WildStackerAPI.getWildStacker().getSystemManager();
//                                            com.bgsoftware.wildstacker.api.objects.StackedBarrel barrel = wildStacker.getStackedBarrel(location);
//                                            if (barrel != null) {
//                                                amount = barrel.getStackAmount();
//                                                blockMaterial = barrel.getType();
//                                                blockData = barrel.getData();
//                                                if (NMSUtil.getVersionNumber() > 12 && blockMaterial.name().startsWith("LEGACY_")) {
//                                                    blockMaterial = Material.matchMaterial(blockMaterial.name().replace("LEGACY_", ""));
//                                                    blockData = 0;
//                                                }
//                                            }
//                                        }

                                        if (stackableManager != null && stackableManager.getStackableMaterials().contains(blockMaterial) && amount == 1) {
                                            World world = Bukkit.getWorld(chunkSnapshotList.getWorldName());
                                            Location location = new Location(world, chunkSnapshotList.getX() * 16 + x, y, chunkSnapshotList.getZ() * 16 + z);
                                            if (stackableManager.isStacked(location)) {
                                                Stackable stackable = stackableManager.getStack(location, blockMaterial);
                                                if (stackable != null) {
                                                    amount = stackable.getSize();
                                                }
                                            }
                                        }
                                    }

                                    LevellingData data = new LevellingData(blockMaterial, (byte) blockData, spawnerType);
                                    Long totalAmountInteger = levellingData.get(data);
                                    long totalAmount = totalAmountInteger == null ? amount : totalAmountInteger + amount;
                                    levellingData.put(data, totalAmount);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                chunk.prepareNextChunkSnapshots();
            }
        }.runTaskTimerAsynchronously(skyblock, 0L, 1L);
    }

    private void finalizeMaterials(Map<LevellingData, Long> levellingData, Set<Location> spawnerLocations, Set<Location> epicSpawnerLocations, Player player, Island island) {
        for (Location location : spawnerLocations) {
            if (!(location.getBlock().getState() instanceof CreatureSpawner))
                continue;

            int amount = 1;
            EntityType spawnerType = ((CreatureSpawner) location.getBlock().getState()).getSpawnedType();

            LevellingData data = new LevellingData(Materials.SPAWNER.parseMaterial(), (byte) 0, spawnerType);
            Long totalAmountInteger = levellingData.get(data);
            long totalAmount = totalAmountInteger == null ? amount : totalAmountInteger + amount;
            levellingData.put(data, totalAmount);
        }

        for (Location location : epicSpawnerLocations) {
            com.songoda.epicspawners.EpicSpawners epicSpawners = com.songoda.epicspawners.EpicSpawners.getInstance();
            if (epicSpawners.getSpawnerManager().isSpawner(location)) {
                com.songoda.epicspawners.spawners.spawner.Spawner spawner = epicSpawners.getSpawnerManager().getSpawnerFromWorld(location);
                if (spawner == null)
                    continue;

                int amount = spawner.getFirstStack().getStackSize();
                EntityType spawnerType = spawner.getCreatureSpawner().getSpawnedType();

                LevellingData data = new LevellingData(Materials.SPAWNER.parseMaterial(), (byte) 0, spawnerType);
                Long totalAmountInteger = levellingData.get(data);
                long totalAmount = totalAmountInteger == null ? amount : totalAmountInteger + amount;
                levellingData.put(data, totalAmount);
            }
        }

        Map<String, Long> materials = new HashMap<>();
        for (LevellingData data : levellingData.keySet()) {
            long amount = levellingData.get(data);
            if (data.getMaterials() != null) {
                materials.put(data.getMaterials().name(), amount);
            }
        }

        if (materials.size() == 0) {
            if (player != null) {
                skyblock.getMessageManager().sendMessage(player, skyblock.getFileManager()
                        .getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                        .getFileConfiguration().getString("Command.Island.Level.Materials.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        } else {
            IslandLevel level = island.getLevel();
            level.setLastCalculatedPoints(level.getPoints());
            level.setLastCalculatedLevel(level.getLevel());
            level.setMaterials(materials);

            Bukkit.getServer().getPluginManager().callEvent(new IslandLevelChangeEvent(island.getAPIWrapper(), island.getAPIWrapper().getLevel()));

            if (player != null) {
                me.goodandevil.skyblock.menus.Levelling.getInstance().open(player);
            }
        }
    }

    public void registerMaterials() {
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Materials") != null) {
            for (String materialKey : configLoad.getConfigurationSection("Materials").getKeys(false)) {
                try {
                    Materials material = Materials.fromString(materialKey);
                    if (!material.isAvailable() || material.getPostItem() == null) continue;

                    if (!containsMaterial(material)) {
                        addMaterial(material, configLoad.getLong("Materials." + materialKey + ".Points"));
                    }
                } catch (Exception e) {
                    Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: The material '" + materialKey
                            + "' is not a Material type. Make sure the material name is a 1.13 material name. Please correct this in the 'levelling.yml' file.");
                }
            }
        }
    }

    public void unregisterMaterials() {
        materialStorage.clear();
    }

    public void addMaterial(Materials materials, long points) {
        materialStorage.add(new LevellingMaterial(materials, points));
    }

    public void removeMaterial(LevellingMaterial material) {
        materialStorage.remove(material);
    }

    public boolean containsMaterial(Materials materials) {
        for (LevellingMaterial materialList : materialStorage) {
            if (materialList.getMaterials().name().equals(materials.name())) {
                return true;
            }
        }

        return false;
    }

    public LevellingMaterial getMaterial(Materials materials) {
        for (LevellingMaterial materialList : materialStorage) {
            if (materialList.getMaterials().name().equals(materials.name())) {
                return materialList;
            }
        }

        return null;
    }

    public List<LevellingMaterial> getMaterials() {
        return materialStorage;
    }

    private class LevellingData {
        private final Material material;
        private final byte data;
        private final EntityType spawnerType;

        private LevellingData(Material material, byte data, EntityType spawnerType) {
            this.material = material;
            this.data = data;
            this.spawnerType = spawnerType;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof LevellingData)) return false;
            LevellingData data = (LevellingData) obj;
            if (this == obj) return true;
            return this.material == data.material && this.data == data.data && this.spawnerType == data.spawnerType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.material, this.data, this.spawnerType);
        }

        private Materials getMaterials() {
            if (this.spawnerType != null) {
                return Materials.getSpawner(this.spawnerType);
            }

            if (NMSUtil.getVersionNumber() > 12) {
                try {
                    return Materials.fromString(this.material.name());
                } catch (Exception ignored) {
                }
            }

            return Materials.getMaterials(this.material, this.data);
        }
    }
}
