package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.limit.impl.EntityLimitaton;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class Entity implements Listener {

    private final SkyBlock skyblock;

    private Set<UUID> preventFireTicks = new HashSet<>();

    public Entity(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onFireWorkBoom(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.FIREWORK
                && skyblock.getWorldManager().isIslandWorld(event.getEntity().getWorld()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        if(event.getEntity() instanceof Blaze){
            WorldManager worldManager = skyblock.getWorldManager();

            Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if(configLoad.getBoolean("Island.Nether.BlazeImmuneToWaterInNether", false) &&
                    worldManager.getIslandWorld(event.getEntity().getWorld()).equals(IslandWorld.Nether)){
                if(event.getCause().equals(DamageCause.DROWNING)){
                    event.setCancelled(true);
                }
            }
        } else if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
                // Check permissions.
                skyblock.getPermissionManager().processPermission(event, player, islandManager.getIslandAtLocation(player.getLocation()));
            }

            // Fix a bug in minecraft where arrows with flame still apply fire ticks even if
            // the shot entity isn't damaged
            if (preventFireTicks.contains(player.getUniqueId()) && event.getCause() == DamageCause.FIRE_TICK) {
                player.setFireTicks(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            org.bukkit.entity.Entity entity = event.getEntity();

            if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {

                // Check permissions.
                skyblock.getPermissionManager()
                        .processPermission(event, player, islandManager.getIslandAtLocation(entity.getLocation()));
            }

            return;
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // Check permissions.
            skyblock.getPermissionManager()
                    .processPermission(event, player, islandManager.getIslandAtLocation(player.getLocation()));

        } else { // Make it work with all the entities, not just TNT
            org.bukkit.entity.Entity entity = event.getEntity();

            // Check permissions.
            skyblock.getPermissionManager()
                    .processPermission(event, islandManager.getIslandAtLocation(entity.getLocation()));
        }

        // Fix a bug in minecraft where arrows with flame still apply fire ticks even if
        // the shot entity isn't damaged
        if (event.isCancelled() && event.getDamager() instanceof Arrow
                && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getFireTicks() != 0) {
                preventFireTicks.add(event.getEntity().getUniqueId());
                new BukkitRunnable() {
                    public void run() {
                        preventFireTicks.remove(event.getEntity().getUniqueId());
                    }
                }.runTaskLater(SkyBlock.getInstance(), 5L);
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            IslandManager islandManager = skyblock.getIslandManager();
            Island island = islandManager.getIslandAtLocation(event.getEntity().getLocation());

            // Check permissions.
            skyblock.getPermissionManager().processPermission(event, player, island);
        }
    }

    /**
     * Checks that an entity is not targeting another entity on different islands.x
     *
     * @author LimeGlass
     */
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        org.bukkit.entity.Entity entity = event.getEntity();
        WorldManager worldManager = skyblock.getWorldManager();
        if (!worldManager.isIslandWorld(entity.getWorld())) return;

        org.bukkit.entity.Entity target = event.getTarget();
        // Somehow the target can be null, thanks Spigot.
        if (target == null) return;

        IslandManager islandManager = skyblock.getIslandManager();
        Island entityIsland = islandManager.getIslandAtLocation(entity.getLocation());
        Island targetIsland = islandManager.getIslandAtLocation(target.getLocation());
        // Event not related to Skyblock islands.
        if (entityIsland == null && targetIsland == null) return;
        // One entity is on an island, and the other isn't.
        if (entityIsland == null || targetIsland == null) {
            event.setCancelled(true);
            return;
        }
        // Both entities are on different islands.
        if (!entityIsland.getIslandUUID().equals(targetIsland.getIslandUUID())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onStackableInteract(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        if (!skyblock.getWorldManager().isIslandWorld(player.getWorld())) return;
        IslandManager islandManager = skyblock.getIslandManager();

        // Check permissions.
        if (!skyblock.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getRightClicked().getLocation())))
            return;

        if (NMSUtil.getVersionNumber() != 8) return;

        StackableManager stackableManager = SkyBlock.getInstance().getStackableManager();
        if (stackableManager == null) return;

        ArmorStand armorStand = event.getRightClicked();
        for (Location stackLocation : stackableManager.getStacks().keySet()) {
            if (stackLocation.getWorld().equals(armorStand.getWorld()) && armorStand.getLocation().distanceSquared(stackLocation) <= 1.5) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (!skyblock.getWorldManager().isIslandWorld(player.getWorld())) return;
        IslandManager islandManager = skyblock.getIslandManager();

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getEntity().getLocation()));
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        Hanging hanging = event.getEntity();

        if (!skyblock.getWorldManager().isIslandWorld(hanging.getWorld())) return;
        IslandManager islandManager = skyblock.getIslandManager();

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, null,
                islandManager.getIslandAtLocation(hanging.getLocation()));
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        Hanging hanging = event.getEntity();

        if (!skyblock.getWorldManager().isIslandWorld(hanging.getWorld())) return;
        IslandManager islandManager = skyblock.getIslandManager();

        Player p = null;
        if(event.getRemover() instanceof Player){
            p = (Player) event.getRemover();
        }
        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, p,
                islandManager.getIslandAtLocation(hanging.getLocation()));
    }

    @EventHandler
    public void onEntityTaming(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player))
            return;

        LivingEntity entity = event.getEntity();

        if (!skyblock.getWorldManager().isIslandWorld(entity.getWorld())) return;
        IslandManager islandManager = skyblock.getIslandManager();

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, (Player) event.getOwner(),
                islandManager.getIslandAtLocation(entity.getLocation()));
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        org.bukkit.entity.Entity entity = event.getEntity();

        if (entity instanceof Player) {
            return;
        }

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());

        if (island == null || !skyblock.getWorldManager().isIslandWorld(entity.getWorld())) return;

        if (event.isCancelled()) return;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        org.bukkit.block.Block block = event.getBlock();

        // Check spawn block falling, this can be a bit glitchy, but it's better than
        // nothing
        if ((LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Main).clone().subtract(0, 1, 0))
                || LocationUtil.isLocationLocation(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Visitor).clone().subtract(0, 1, 0)))
                && skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Spawn.Protection")) {
            event.setCancelled(true);
            return;
        }

        if ((event.getEntityType() == EntityType.FALLING_BLOCK)
                && LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(world, IslandEnvironment.Main).clone())
                && configLoad.getBoolean("Island.Spawn.Protection")) {
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            if (fallingBlock.getDropItem()) {
                if (NMSUtil.getVersionNumber() > 12) {
                    fallingBlock.getWorld().dropItemNaturally(fallingBlock.getLocation(),
                            new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));
                } else {
                    try {
                        Method getBlockDataMethod = FallingBlock.class.getMethod("getBlockData");
                        byte data = (byte) getBlockDataMethod.invoke(fallingBlock);
                        if (fallingBlock.getMaterial().name().endsWith("ANVIL")) {
                            data = (byte) Math.ceil(data / 4.0);
                        }
                        fallingBlock.getWorld().dropItemNaturally(fallingBlock.getLocation(), new ItemStack(fallingBlock.getMaterial(), 1, data));
                    } catch (Exception ignored) {
                    }
                }
            }
            event.setCancelled(true);
        }

        if (entity instanceof FallingBlock) return;

        // Check entities interacting with spawn
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && skyblock.getFileManager()
                .getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
            event.setCancelled(true);
            return;
        }

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, null, island);


        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Block.Level.Enable"))
            return;

        CompatibleMaterial materials = CompatibleMaterial.getBlockMaterial(block.getType());

        if (materials != null) {
            IslandLevel level = island.getLevel();

            if (level.hasMaterial(materials.name())) {
                long materialAmount = level.getMaterialAmount(materials.name());

                if (materialAmount - 1 <= 0) {
                    level.removeMaterial(materials.name());
                } else {
                    level.setMaterialAmount(materials.name(), materialAmount - 1);
                }
            }
        }

        if (event.getTo() != null && event.getTo() != Material.AIR) {
            materials = CompatibleMaterial.getBlockMaterial(event.getTo());
            ;

            if (materials != null) {
                long materialAmount = 0;
                IslandLevel level = island.getLevel();

                if (level.hasMaterial(materials.name())) {
                    materialAmount = level.getMaterialAmount(materials.name());
                }

                level.setMaterialAmount(materials.name(), materialAmount + 1);
            }
        }

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        org.bukkit.entity.Entity entity = event.getEntity();

        WorldManager worldManager = skyblock.getWorldManager();
        IslandManager islandManager = skyblock.getIslandManager();

        if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
            // Check permissions.
            Island island = islandManager.getIslandAtLocation(entity.getLocation());

            skyblock.getPermissionManager().processPermission(event, null, island);

            if (!event.isCancelled()) {
                if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                        .getBoolean("Island.Block.Level.Enable")) {
                    for (org.bukkit.block.Block blockList : event.blockList()) {
                        @SuppressWarnings("deprecation")
                        CompatibleMaterial materials = CompatibleMaterial.getBlockMaterial(blockList.getType());

                        if (materials != null) {
                            IslandLevel level = island.getLevel();

                            if (level.hasMaterial(materials.name())) {
                                long materialAmount = level.getMaterialAmount(materials.name());

                                if (materialAmount - 1 <= 0) {
                                    level.removeMaterial(materials.name());
                                } else {
                                    level.setMaterialAmount(materials.name(), materialAmount - 1);
                                }
                            }
                        }
                    }
                }

                if (SkyBlock.getInstance().getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                        .getBoolean("Island.Spawn.Protection")) {
                    IslandWorld world = worldManager.getIslandWorld(event.getEntity().getWorld());
                    for (org.bukkit.block.Block block : event.blockList()) {
                        if (LocationUtil.isLocationLocation(block.getLocation(),
                                island.getLocation(world, IslandEnvironment.Main).clone().subtract(0.0D, 1.0D, 0.0D))) {
                            event.blockList().remove(block);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();

        // Certain entities shouldn't drop twice the amount
        if (livingEntity instanceof Player || livingEntity instanceof ArmorStand || livingEntity instanceof Horse) {
            return;
        }

        if (NMSUtil.getVersionNumber() > 9) {
            if (livingEntity instanceof Donkey || livingEntity instanceof Mule || livingEntity instanceof ElderGuardian)
                return;
        }

        if (NMSUtil.getVersionNumber() > 10) {
            if (livingEntity instanceof Evoker) return;
        }

        if (NMSUtil.getVersionNumber() > 11) {
            if (livingEntity instanceof Llama) return;
        }

        if (NMSUtil.getVersionNumber() > 13) {
            if (livingEntity instanceof Ravager || livingEntity instanceof Illager) return;
        }

        if (livingEntity.hasMetadata("SkyBlock")) return;

        IslandManager islandManager = skyblock.getIslandManager();

        if (skyblock.getWorldManager().isIslandWorld(livingEntity.getWorld())) {
            Island island = islandManager.getIslandAtLocation(livingEntity.getLocation());

            if (island != null) {
                List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Drops);

                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled() && island.isUpgrade(Upgrade.Type.Drops)) {
                    Set<ItemStack> dontMultiply = new HashSet<>();

                    if (NMSUtil.getVersionNumber() > 8) {
                        EntityEquipment equipment = livingEntity.getEquipment();
                        if (equipment != null) {
                            for (ItemStack item : event.getDrops()) {
                                if (item.equals(equipment.getHelmet()) || item.equals(equipment.getChestplate())
                                        || item.equals(equipment.getLeggings()) || item.equals(equipment.getBoots())
                                        || item.equals(equipment.getItemInMainHand()) || item.equals(equipment.getItemInOffHand())) {
                                    dontMultiply.add(item);
                                }
                            }
                        }

                        if (livingEntity instanceof Pig) {
                            Pig pig = (Pig) livingEntity;
                            if (pig.hasSaddle()) dontMultiply.add(new ItemStack(Material.SADDLE, 1));
                        }
                    }

                    for (ItemStack is : event.getDrops())
                        if (!dontMultiply.contains(is))
                            livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), is);
                }
            }
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player))
            return;

        Player player = (Player) event.getTarget();
        if (!skyblock.getWorldManager().isIslandWorld(player.getWorld())) return;
        IslandManager islandManager = skyblock.getIslandManager();

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getEntity().getLocation()));
    }

    private static final Set<SpawnReason> CHECKED_REASONS;

    static {
        CHECKED_REASONS = EnumSet.of(SpawnReason.NATURAL, SpawnReason.JOCKEY, SpawnReason.MOUNT);

        final SpawnReason raid = getSpawnReason("RAID");
        final SpawnReason patrol = getSpawnReason("PATROL");

        if (patrol != null) CHECKED_REASONS.add(patrol);
        if (raid != null) CHECKED_REASONS.add(raid);

    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof ArmorStand) return;
        // if (entity.hasMetadata("SkyBlock")) return;
        // Doesn't appear this is ever set by our plugin and it is extremely intensive.

        Location entityLocation = entity.getLocation();

        Island island = skyblock.getIslandManager().getIslandAtLocation(entityLocation);

        if (island == null) return;

        EntityLimitaton limits = skyblock.getLimitationHandler().getInstance(EntityLimitaton.class);
        EntityType type = entity.getType();

        if (limits.isBeingTracked(type)) {
            FileManager fileManager = skyblock.getFileManager();
            Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            boolean isSplit = event.getSpawnReason().equals(SpawnReason.SLIME_SPLIT);
            boolean splitBypass = configLoad.getBoolean("Island.Challenge.PerIsland", true);

            if(!isSplit || !splitBypass){
                long count = limits.getEntityCount(island, skyblock.getWorldManager().getIslandWorld(entityLocation.getWorld()), type);
                if (limits.hasTooMuch(count + 1, type)) {
                    entity.remove();
                    event.setCancelled(true);
                    return;
                }
            }
        }

        SpawnReason spawnReason = event.getSpawnReason();

        if (!CHECKED_REASONS.contains(spawnReason)) return;

        if (!skyblock.getWorldManager().isIslandWorld(entity.getWorld())) return;
        if (skyblock.getPermissionManager().hasPermission(null, island, "NaturalMobSpawning")) return;
        if (spawnReason != SpawnReason.JOCKEY && spawnReason != SpawnReason.MOUNT) {
            entity.remove(); // Older versions ignore the event being cancelled, so this fixes that issue.
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> {
            if (NMSUtil.getVersionNumber() > 10) { // getPassengers() was added in 1.11
                for (org.bukkit.entity.Entity passenger : entity.getPassengers())
                    passenger.remove();
            } else {
                if (entity.getPassenger() != null) entity.getPassenger().remove();
            }
            entity.remove();
        });
        event.setCancelled(true); // For other plugin API reasons.
    }

    @EventHandler
    public void onDamageVehicle(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            IslandManager islandManager = skyblock.getIslandManager();
            skyblock.getPermissionManager().processPermission(event, null, islandManager.getIslandAtLocation(event.getVehicle().getLocation()));
        }
    }

    @EventHandler
    public void onDestroyVehicle(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            IslandManager islandManager = skyblock.getIslandManager();
            skyblock.getPermissionManager().processPermission(event, null, islandManager.getIslandAtLocation(event.getVehicle().getLocation()));
        }
    }

    private static SpawnReason getSpawnReason(String reason) {
        try {
            return SpawnReason.valueOf(reason);
        } catch (Exception e) {
            return null;
        }
    }

}
