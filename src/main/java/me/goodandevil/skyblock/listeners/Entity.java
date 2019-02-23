package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.world.WorldManager;

public class Entity implements Listener {

    private final SkyBlock skyblock;
    
    private Set<UUID> preventFireTicks = new HashSet<>();

    public Entity(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        FileManager fileManager = skyblock.getFileManager();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            if (event.getCause() != null) {
                if (event.getCause() == DamageCause.VOID) {
                    return;
                } else if (event.getCause() == DamageCause.ENTITY_ATTACK) {
                    EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

                    if (entityDamageByEntityEvent.getDamager() != null
                            && entityDamageByEntityEvent.getDamager() instanceof Player) {
                        return;
                    }
                } else {
                    if (NMSUtil.getVersionNumber() > 11) {
                        if (event.getCause() == DamageCause.valueOf("ENTITY_SWEEP_ATTACK")) {
                            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

                            if (entityDamageByEntityEvent.getDamager() != null
                                    && entityDamageByEntityEvent.getDamager() instanceof Player) {
                                return;
                            }
                        }
                    }
                }
            }

            Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getBoolean("Island.Settings.Damage.Enable")) {
                if (!skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "Damage")) {
                    event.setCancelled(true);
                }
            } else if (!configLoad.getBoolean("Island.Damage.Enable")) {
                event.setCancelled(true);
            }
        }
        
        // Fix a bug in spigot where arrows with flame still apply flame even if the event is cancelled
        if (preventFireTicks.contains(player.getUniqueId()) && event.getCause() == DamageCause.FIRE_TICK) {
            player.setFireTicks(0);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            org.bukkit.entity.Entity entity = event.getEntity();

            if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
                if (entity instanceof Player) {
                    Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                    FileConfiguration configLoad = config.getFileConfiguration();

                    if (configLoad.getBoolean("Island.Settings.PvP.Enable")) {
                        if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "PvP")) {
                            event.setCancelled(true);
                        }
                    } else if (!configLoad.getBoolean("Island.PvP.Enable")) {
                        event.setCancelled(true);
                    }
                } else if (entity instanceof ArmorStand) {
                    if (!islandManager.hasPermission(player, entity.getLocation(), "Destroy")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                        .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
                    }
                } else {
                    if (!islandManager.hasPermission(player, entity.getLocation(), "MobHurting")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                        .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

                        return;
                    }
                }
            }

            return;
        }

        if (event.getDamager() instanceof Projectile
                && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            Player player = (Player) ((Projectile) event.getDamager()).getShooter();
            org.bukkit.entity.Entity entity = event.getEntity();

            if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
                if (event.getEntity() instanceof Player) {
                    Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                    FileConfiguration configLoad = config.getFileConfiguration();

                    if (configLoad.getBoolean("Island.Settings.PvP.Enable")) {
                        if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "PvP")) {
                            event.setCancelled(true);
                        }
                    } else if (!configLoad.getBoolean("Island.PvP.Enable")) {
                        event.setCancelled(true);
                    }
                } else {
                    if (!islandManager.hasPermission(player, entity.getLocation(), "MobHurting")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                        .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

                        return;
                    }
                }
            }
        } else if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
                Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                if (configLoad.getBoolean("Island.Settings.Damage.Enable")) {
                    if (!islandManager.hasSetting(player.getLocation(), IslandRole.Owner, "Damage")
                            || (event.getDamager() instanceof TNTPrimed && !islandManager
                            .hasSetting(player.getLocation(), IslandRole.Owner, "Explosions"))) {
                        event.setCancelled(true);
                    }
                } else if (!configLoad.getBoolean("Island.Damage.Enable")) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof TNTPrimed) {
            org.bukkit.entity.Entity entity = event.getEntity();

            if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
                if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "Explosions")) {
                    event.setCancelled(true);
                }
            }
        }
        
        // Fix a bug in spigot where arrows with flame still apply flame even if the event is cancelled
        if (event.isCancelled() && event.getEntity() != null && event.getDamager() instanceof Arrow && ((Arrow)event.getDamager()).getShooter() instanceof Player) {
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
            if (!skyblock.getIslandManager().hasPermission(player, event.getEntity().getLocation(), "Shearing")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            if (!skyblock.getIslandManager().hasPermission(player, event.getEntity().getLocation(),
                    "EntityPlacement")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        Hanging hanging = event.getEntity();

        if (event.getCause() != RemoveCause.EXPLOSION) {
            return;
        }

        if (skyblock.getWorldManager().isIslandWorld(hanging.getWorld())) {
            if (!skyblock.getIslandManager().hasSetting(hanging.getLocation(), IslandRole.Owner, "Explosions")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        Hanging hanging = event.getEntity();

        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getRemover();

        if (skyblock.getWorldManager().isIslandWorld(hanging.getWorld())) {
            if (!skyblock.getIslandManager().hasPermission(player, hanging.getLocation(), "HangingDestroy")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onEntityTaming(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getOwner();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            if (!skyblock.getIslandManager().hasPermission(player, event.getEntity().getLocation(), "MobTaming")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        org.bukkit.entity.Entity entity = event.getEntity();

        if (entity instanceof Player) {
            return;
        }

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());

        if (!skyblock.getWorldManager().isIslandWorld(entity.getWorld())) return;

        if (event.isCancelled()) return;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        if ((event.getEntityType() == EntityType.FALLING_BLOCK) && LocationUtil.isLocationLocation(event.getBlock().getLocation(),
                    island.getLocation(world, IslandEnvironment.Main)
                            .clone())
                && configLoad.getBoolean("Island.Spawn.Protection")) {
            event.getEntity().remove();
            event.setCancelled(true);
        }

        if (entity instanceof FallingBlock)
            return;

        if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "MobGriefing")) {
            event.setCancelled(true);
        }


        if (island == null) return;
        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Block.Level.Enable")) return;
        org.bukkit.block.Block block = event.getBlock();

        @SuppressWarnings("deprecation")
        Materials materials = Materials.getMaterials(block.getType(), block.getData());

        if (materials != null) {
            IslandLevel level = island.getLevel();

            if (level.hasMaterial(materials.name())) {
                int materialAmount = level.getMaterialAmount(materials.name());

                if (materialAmount - 1 <= 0) {
                    level.removeMaterial(materials.name());
                } else {
                    level.setMaterialAmount(materials.name(), materialAmount - 1);
                }
            }
        }

        if (event.getTo() != null && event.getTo() != Material.AIR) {
            materials = null;

            if (NMSUtil.getVersionNumber() > 12) {
                materials = Materials.fromString(event.getTo().name());
            } else {
                try {
                    materials = Materials.requestMaterials(event.getTo().name(),
                            (byte) event.getClass().getMethod("getData", byte.class).invoke(event));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }

            if (materials != null) {
                int materialAmount = 0;
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

        IslandManager islandManager = skyblock.getIslandManager();

        if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
            if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "Explosions")) {
                event.setCancelled(true);
            }

            if (!event.isCancelled()) {
                Island island = islandManager.getIslandAtLocation(entity.getLocation());

                if (island != null) {
                    if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                            .getFileConfiguration().getBoolean("Island.Block.Level.Enable")) {
                        for (org.bukkit.block.Block blockList : event.blockList()) {
                            @SuppressWarnings("deprecation")
                            Materials materials = Materials.getMaterials(blockList.getType(), blockList.getData());

                            if (materials != null) {
                                IslandLevel level = island.getLevel();

                                if (level.hasMaterial(materials.name())) {
                                    int materialAmount = level.getMaterialAmount(materials.name());

                                    if (materialAmount - 1 <= 0) {
                                        level.removeMaterial(materials.name());
                                    } else {
                                        level.setMaterialAmount(materials.name(), materialAmount - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.hasMetadata("SkyBlock")) {
            return;
        }

        IslandManager islandManager = skyblock.getIslandManager();

        if (skyblock.getWorldManager().isIslandWorld(livingEntity.getWorld())) {
            Island island = islandManager.getIslandAtLocation(livingEntity.getLocation());

            if (island != null) {
                List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Drops);

                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()
                        && island.isUpgrade(Upgrade.Type.Drops)) {
                    List<ItemStack> entityDrops = event.getDrops();

                    if (entityDrops != null) {
                        for (int i = 0; i < entityDrops.size(); i++) {
                            ItemStack is = entityDrops.get(i);
                            is.setAmount(is.getAmount() * 2);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof ExperienceOrb)) {
            return;
        }

        Player player = (Player) event.getTarget();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            if (!skyblock.getIslandManager().hasPermission(player, "ExperienceOrbPickup")) {
                event.setTarget(null);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof ArmorStand || event.getEntity() instanceof FallingBlock
                || event.getEntity() instanceof org.bukkit.entity.Item) {
            return;
        }

        if (!(event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.NATURAL)) {
            return;
        }

        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.hasMetadata("SkyBlock")) {
            return;
        }

        if (skyblock.getWorldManager().isIslandWorld(livingEntity.getWorld())) {
            if (!skyblock.getIslandManager().hasSetting(livingEntity.getLocation(), IslandRole.Owner,
                    "NaturalMobSpawning")) {
                livingEntity.remove();
            }
        }
    }
}
