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

    private Set<Island> activeIslandScans = new HashSet<>();
    private List<LevellingMaterial> materialStorage = new ArrayList<>();

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

        this.activeIslandScans.add(island);

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
        boolean isUltimateStackerEnabled = Bukkit.getPluginManager().isPluginEnabled("UltimateStacker");

        Map<LevellingData, Long> levellingData = new HashMap<>();
        Set<Location> spawnerLocations = new HashSet<>(); // These have to be checked synchronously :(
        Set<Location> epicSpawnerLocations = new HashSet<>();
        Set<Location> ultimateStackerSpawnerLocations = new HashSet<>();

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
                    Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> finalizeMaterials(levellingData, spawnerLocations, epicSpawnerLocations, ultimateStackerSpawnerLocations, player, island), 1);
                    cancel();
                    return;
                }

                for (LevelChunkSnapshotWrapper chunkSnapshotList : chunk.getAvailableChunkSnapshots()) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < worldMaxHeight; y++) {
                                ChunkSnapshot chunkSnapshot = chunkSnapshotList.getChunkSnapshot();

                                try {
                                    org.bukkit.Material blockMaterial;
                                    int blockData = 0;
                                    EntityType spawnerType = null;

                                    if (NMSVersion > 12) {
                                        blockMaterial = chunkSnapshot.getBlockType(x, y, z);
                                    } else {
                                        LegacyChunkSnapshotData data = LegacyChunkSnapshotFetcher.fetch(chunkSnapshot, x, y, z);

                                        blockMaterial = data.getMaterial();
                                        blockData = data.getData();
                                    }

                                    if (blacklistedMaterials.contains(blockMaterial))
                                        continue;

                                    long amount = 1;

                                    if (blockMaterial == Materials.SPAWNER.parseMaterial()) {
                                        World world = Bukkit.getWorld(chunkSnapshot.getWorldName());
                                        Location location = new Location(world, chunkSnapshot.getX() * 16 + x, y, chunkSnapshot.getZ() * 16 + z);

                                        if (isEpicSpawnersEnabled) {
                                            com.songoda.epicspawners.EpicSpawners epicSpawners = com.songoda.epicspawners.EpicSpawners.getInstance();
                                            if (epicSpawners.getSpawnerManager().isSpawner(location)) {
                                                com.songoda.epicspawners.spawners.spawner.Spawner spawner = epicSpawners.getSpawnerManager().getSpawnerFromWorld(location);
                                                if (spawner != null)
                                                    epicSpawnerLocations.add(location);
                                                continue;
                                            }
                                        } else if (isUltimateStackerEnabled) {
                                            com.songoda.ultimatestacker.spawner.SpawnerStack spawnerStack = com.songoda.ultimatestacker.UltimateStacker.getInstance().getSpawnerStackManager().getSpawner(location);
                                            if (spawnerStack != null)
                                                ultimateStackerSpawnerLocations.add(location);
                                            continue;
                                        }

                                        if (chunkSnapshotList.hasWildStackerData()) {
                                            com.bgsoftware.wildstacker.api.objects.StackedSnapshot snapshot = ((WildStackerChunkSnapshotWrapper)chunkSnapshotList).getStackedSnapshot();
                                            if (snapshot.isStackedSpawner(location)) {
                                                Map.Entry<Integer, EntityType> spawnerData = snapshot.getStackedSpawner(location);
                                                amount = spawnerData.getKey();
                                                spawnerType = spawnerData.getValue();
                                            }
                                        }

                                        if (spawnerType == null) {
                                            spawnerLocations.add(location);
                                            continue;
                                        }
                                    } else {
                                        if (chunkSnapshotList.hasWildStackerData()) {
                                            com.bgsoftware.wildstacker.api.objects.StackedSnapshot snapshot = ((WildStackerChunkSnapshotWrapper)chunkSnapshotList).getStackedSnapshot();
                                            World world = Bukkit.getWorld(chunkSnapshot.getWorldName());
                                            Location location = new Location(world, chunkSnapshot.getX() * 16 + x, y, chunkSnapshot.getZ() * 16 + z);
                                            if (snapshot.isStackedBarrel(location)) {
                                                Map.Entry<Integer, Material> barrelData = snapshot.getStackedBarrel(location);
                                                amount = barrelData.getKey();
                                                blockMaterial = barrelData.getValue();
                                                if (NMSUtil.getVersionNumber() > 12 && blockMaterial.name().startsWith("LEGACY_")) {
                                                    blockMaterial = Material.matchMaterial(blockMaterial.name().replace("LEGACY_", ""));
                                                }
                                            }
                                        }

                                        if (stackableManager != null && stackableManager.getStackableMaterials().contains(blockMaterial) && amount == 1) {
                                            World world = Bukkit.getWorld(chunkSnapshot.getWorldName());
                                            Location location = new Location(world, chunkSnapshot.getX() * 16 + x, y, chunkSnapshot.getZ() * 16 + z);
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

    private void finalizeMaterials(Map<LevellingData, Long> levellingData, Set<Location> spawnerLocations, Set<Location> epicSpawnerLocations, Set<Location> ultimateStackerSpawnerLocations, Player player, Island island) {
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

        for (Location location : ultimateStackerSpawnerLocations) {
            com.songoda.ultimatestacker.spawner.SpawnerStack spawnerStack = com.songoda.ultimatestacker.UltimateStacker.getInstance().getSpawnerStackManager().getSpawner(location);
            if (spawnerStack == null)
                continue;

            int amount = spawnerStack.getAmount();
            EntityType spawnerType = ((CreatureSpawner) location.getBlock().getState()).getSpawnedType();

            LevellingData data = new LevellingData(Materials.SPAWNER.parseMaterial(), (byte) 0, spawnerType);
            Long totalAmountInteger = levellingData.get(data);
            long totalAmount = totalAmountInteger == null ? amount : totalAmountInteger + amount;
            levellingData.put(data, totalAmount);
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

        this.activeIslandScans.remove(island);
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
                            + "' is not a Material type. Make sure the material name is a 1.14 material name. Please correct this in the 'levelling.yml' file.");
                }
            }
        }
    }

    public boolean isIslandLevelBeingScanned(Island island) {
        return this.activeIslandScans.contains(island);
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
