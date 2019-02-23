package me.goodandevil.skyblock.levelling;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.island.IslandLevelChangeEvent;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.stackable.Stackable;
import me.goodandevil.skyblock.stackable.StackableManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.world.WorldManager;

public class LevellingManager {

    private final SkyBlock skyblock;

    private List<Material> materialStorage = new ArrayList<>();

    public LevellingManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        registerMaterials();
    }

    public void calculatePoints(Player player, Island island) {
        WorldManager worldManager = skyblock.getWorldManager();
        StackableManager stackableManager = skyblock.getStackableManager();

        Chunk chunk = new Chunk(skyblock, island);
        chunk.prepare();

        int NMSVersion = NMSUtil.getVersionNumber();

        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (!chunk.isComplete()) return;
                cancel();

                Map<String, Integer> materials = new HashMap<>();

                Method getBlockTypeMethod = null;
                Method getBlockTypeIdMethod = null;
                Method getBlockTypeDataMethod = null;
                Method getMaterialMethod = null;

                int worldMaxHeight = 0;

                for (IslandWorld worldList : IslandWorld.values()) {
                    org.bukkit.World world = worldManager.getWorld(worldList);

                    if (worldMaxHeight == 0 || worldMaxHeight > world.getMaxHeight()) {
                        worldMaxHeight = world.getMaxHeight();
                    }
                }

                for (ChunkSnapshot chunkSnapshotList : chunk.getChunkSnapshots()) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < worldMaxHeight; y++) {
                                try {
                                    org.bukkit.Material blockMaterial = org.bukkit.Material.AIR;
                                    int blockData = 0;

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

                                    if (blockMaterial == org.bukkit.Material.AIR) continue;

                                    for (Material materialList : materialStorage) {
                                        if (materialList == null) continue;
                                        ItemStack is;
                                        try {
                                            is = materialList.getItemStack();
                                        } catch (Exception ignored) {
                                            continue;
                                        }

                                        if (blockMaterial != materialList.getItemStack().getType()) continue;
                                        if (NMSVersion < 13) {
                                            if (!(blockData == is.getDurability())) {
                                                continue;
                                            }
                                        }

                                        int amount = 1;
                                        if (blockMaterial == Materials.SPAWNER.parseMaterial()
                                                && Bukkit.getPluginManager().isPluginEnabled("EpicSpawners")) {
                                            World world = Bukkit.getWorld(chunkSnapshotList.getWorldName());
                                            com.songoda.epicspawners.api.EpicSpawners epicSpawners =
                                                    com.songoda.epicspawners.api.EpicSpawnersAPI.getImplementation();
                                            Location location = new Location(world, chunkSnapshotList.getX() * 16 + x,  y, chunkSnapshotList.getZ() * 16 + z);
                                            if (epicSpawners.getSpawnerManager().isSpawner(location)) {
                                                amount = epicSpawners.getSpawnerManager()
                                                        .getSpawnerFromWorld(location).getSpawnerDataCount();
                                            }
                                        } else if (stackableManager != null && stackableManager.getStackableMaterials().contains(blockMaterial)) {
                                            World world = Bukkit.getWorld(chunkSnapshotList.getWorldName());
                                            Location location = new Location(world, chunkSnapshotList.getX() * 16 + x,  y, chunkSnapshotList.getZ() * 16 + z);
                                            if (stackableManager.isStacked(location)) {
                                                Stackable stackable = stackableManager.getStack(location, blockMaterial);
                                                amount = stackable.getSize();
                                            }
                                        }

                                        if (materials.containsKey(materialList.getMaterials().name())) {
                                            materials.put(materialList.getMaterials().name(),
                                                    materials.get(materialList.getMaterials().name()) + amount);
                                        } else {
                                            materials.put(materialList.getMaterials().name(), amount);
                                        }
                                    }
                                } catch (IllegalAccessException | IllegalArgumentException
                                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
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

                    Bukkit.getServer().getPluginManager().callEvent(
                            new IslandLevelChangeEvent(island.getAPIWrapper(), island.getAPIWrapper().getLevel()));

                    if (player != null) {
                        me.goodandevil.skyblock.menus.Levelling.getInstance().open(player);
                    }
                }
            }
        }.runTaskTimerAsynchronously(skyblock, 0L, 1L);
    }

    public void registerMaterials() {
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        
        if (configLoad.getString("Materials") != null) {
            for (String materialKey : configLoad.getConfigurationSection("Materials").getKeys(false)) {
                try {
                    Materials material = Materials.fromString(materialKey);
                    
                    if (!containsMaterial(material)) {
                        addMaterial(material, configLoad.getInt("Materials." + materialKey + ".Points"));
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

    public void addMaterial(Materials materials, int points) {
        materialStorage.add(new Material(materials, points));
    }

    public void removeMaterial(Material material) {
        materialStorage.remove(material);
    }

    public boolean containsMaterial(Materials materials) {
        for (Material materialList : materialStorage) {
            if (materialList.getMaterials().name().equals(materials.name())) {
                return true;
            }
        }

        return false;
    }

    public Material getMaterial(Materials materials) {
        for (Material materialList : materialStorage) {
            if (materialList.getMaterials().name().equals(materials.name())) {
                return materialList;
            }
        }

        return null;
    }

    public List<Material> getMaterials() {
        return materialStorage;
    }
}
