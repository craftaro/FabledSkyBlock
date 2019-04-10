package me.goodandevil.skyblock.listeners;

import com.bgsoftware.wildstacker.api.events.BarrelUnstackEvent;
import com.bgsoftware.wildstacker.api.events.SpawnerUnstackEvent;
import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class WildStacker implements Listener {

    private final SkyBlock skyblock;

    public WildStacker(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onBarrelUnstack(BarrelUnstackEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Location location = event.getBarrel().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        Material material = event.getBarrel().getType();
        byte data = (byte) event.getBarrel().getData();

        if (NMSUtil.getVersionNumber() > 12 && material.name().startsWith("LEGACY_")) {
            material = Material.matchMaterial(material.name().replace("LEGACY_", ""));
            data = 0;
        }

        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            Materials materials = Materials.getMaterials(material, data);
            if (materials != null) {
                IslandLevel level = island.getLevel();

                if (level.hasMaterial(materials.name())) {
                    long materialAmount = level.getMaterialAmount(materials.name());

                    if (materialAmount - event.getAmount() <= 0) {
                        level.removeMaterial(materials.name());
                    } else {
                        level.setMaterialAmount(materials.name(), materialAmount - event.getAmount());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawnerUnstack(SpawnerUnstackEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        EntityType spawnerType = event.getSpawner().getSpawnedType();

        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            Materials materials = Materials.getSpawner(spawnerType);
            if (materials != null) {
                IslandLevel level = island.getLevel();

                if (level.hasMaterial(materials.name())) {
                    long materialAmount = level.getMaterialAmount(materials.name());

                    if (materialAmount - event.getAmount() <= 0) {
                        level.removeMaterial(materials.name());
                    } else {
                        level.setMaterialAmount(materials.name(), materialAmount - event.getAmount());
                    }
                }
            }
        }
    }

    // TODO: Readd after WildStacker 2.7.4 is released. A feature is being added that will let us multiply the drops by 2x.
//    @EventHandler
//    public void onMobStack(EntityStackEvent event) {
//        LivingEntity livingEntity = event.getEntity().getLivingEntity();
//
//        // Certain entities shouldn't drop twice the amount
//        if (livingEntity instanceof Player ||
//            livingEntity instanceof ArmorStand ||
//            livingEntity instanceof Horse) {
//            return;
//        }
//
//        if (NMSUtil.getVersionNumber() > 8) {
//            if (livingEntity instanceof Donkey || livingEntity instanceof Mule)
//                return;
//        }
//
//        if (livingEntity.hasMetadata("SkyBlock")) {
//            return;
//        }
//
//        IslandManager islandManager = skyblock.getIslandManager();
//
//        if (skyblock.getWorldManager().isIslandWorld(livingEntity.getWorld())) {
//            Island island = islandManager.getIslandAtLocation(livingEntity.getLocation());
//
//            if (island != null) {
//                List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Drops);
//
//                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()
//                        && island.isUpgrade(Upgrade.Type.Drops)) {
//                    StackedEntity entity = event.getEntity();
//                    StackedEntity target = event.getTarget();
//
//                    List<ItemStack> drops = target.getDrops(0);
//                    for (ItemStack item : drops) {
//                        item.setAmount(item.getAmount() * 2);
//                    }
//
//                    List<ItemStack> newDrops = entity.getDrops(0);
//                    newDrops.addAll(drops);
//
//                    entity.setTempLootTable(newDrops);
//                }
//            }
//        }
//    }

}
