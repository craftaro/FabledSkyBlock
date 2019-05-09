package me.goodandevil.skyblock.listeners;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.*;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.stackable.StackableManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    public void onStackableInteract(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        if (!skyblock.getIslandManager().hasPermission(player, event.getRightClicked().getLocation(), "ArmorStandUse")) {
            event.setCancelled(true);

            skyblock.getMessageManager().sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Island.Settings.Permission.Message"));
            skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
        }

        if (NMSUtil.getVersionNumber() != 8)
            return;
        
        StackableManager stackableManager = SkyBlock.getInstance().getStackableManager();
        if (stackableManager == null)
            return;
        
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
    public void onHangingInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Hanging)) {
            return;
        }
        
        Player player = event.getPlayer();
        Hanging hanging = (Hanging) event.getRightClicked();
        
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
    public void onHangingRemoveItem(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Hanging)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        Hanging hanging = (Hanging) event.getEntity();
        
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

        // Check spawn block falling, this can be a bit glitchy, but it's better than nothing
        Location islandLocation = island.getLocation(world, IslandEnvironment.Main);
        if (LocationUtil.isLocationLocation(block.getLocation(), islandLocation.clone().subtract(0, 1, 0)) &&
                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
            event.setCancelled(true);
            return;
        }

        if ((event.getEntityType() == EntityType.FALLING_BLOCK) && LocationUtil.isLocationLocation(event.getBlock().getLocation(),
                    island.getLocation(world, IslandEnvironment.Main)
                            .clone())
                && configLoad.getBoolean("Island.Spawn.Protection")) {
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            if (fallingBlock.getDropItem()) {
                if (NMSUtil.getVersionNumber() > 12) {
                    fallingBlock.getWorld().dropItemNaturally(fallingBlock.getLocation(), new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));
                } else {
                    try {
                        Method getBlockDataMethod = FallingBlock.class.getMethod("getBlockData");
                        byte data = (byte) getBlockDataMethod.invoke(fallingBlock);
                        if (fallingBlock.getMaterial().name().endsWith("ANVIL")) {
                            data = (byte) Math.ceil(data / 4.0);
                        }
                        fallingBlock.getWorld().dropItemNaturally(fallingBlock.getLocation(), new ItemStack(fallingBlock.getMaterial(), 1, (byte) data));
                    } catch (Exception ignored) { }
                }
            }
            event.setCancelled(true);
        }

        if (entity instanceof FallingBlock)
            return;

        // Check entities interacting with spawn
        if (LocationUtil.isLocationAffectingLocation(block.getLocation(), islandLocation) &&
                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
            event.setCancelled(true);
            return;
        }

        if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "MobGriefing")) {
            event.setCancelled(true);
        }

        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Block.Level.Enable")) return;

        Materials materials = Materials.getMaterials(block.getType(), block.getData());

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
            materials = null;

            if (NMSUtil.getVersionNumber() > 12) {
                materials = Materials.fromString(event.getTo().name());
            } else {
                try {
                    materials = Materials.requestMaterials(event.getTo().name(),
                            (byte) event.getClass().getMethod("getData").invoke(event));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }

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
                    
                    if (SkyBlock.getInstance().getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
                        IslandWorld world = worldManager.getIslandWorld(event.getEntity().getWorld());
                        for (org.bukkit.block.Block block : event.blockList()) {
                            if (LocationUtil.isLocationLocation(block.getLocation(),
                                    island.getLocation(world, IslandEnvironment.Main)
                                            .clone()
                                            .subtract(0.0D, 1.0D, 0.0D))) {
                                event.blockList().remove(block);
                                break;
                            }
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
        if (livingEntity instanceof Player
                || livingEntity instanceof ArmorStand
                || livingEntity instanceof Horse) {
            return;
        }

        if (NMSUtil.getVersionNumber() > 8) {
            if (livingEntity instanceof Donkey || livingEntity instanceof Mule || livingEntity instanceof ElderGuardian)
                return;
        }

        if (NMSUtil.getVersionNumber() > 10) {
            if (livingEntity instanceof Evoker)
                return;
        }

        if (NMSUtil.getVersionNumber() > 13) {
            if (livingEntity instanceof Ravager || livingEntity instanceof Illager)
                return;
        }
        
        if (livingEntity.hasMetadata("SkyBlock"))
            return;

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
                                if (item.equals(equipment.getHelmet())
                                        || item.equals(equipment.getChestplate())
                                        || item.equals(equipment.getLeggings())
                                        || item.equals(equipment.getBoots())
                                        || item.equals(equipment.getItemInMainHand())
                                        || item.equals(equipment.getItemInOffHand())) {
                                    dontMultiply.add(item);
                                }
                            }
                        }

                        if (livingEntity instanceof Pig) {
                            Pig pig = (Pig) livingEntity;
                            if (pig.hasSaddle())
                                dontMultiply.add(new ItemStack(Material.SADDLE, 1));
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
            if (!skyblock.getIslandManager().hasSetting(livingEntity.getLocation(), IslandRole.Owner, "NaturalMobSpawning")) {
                livingEntity.remove();
            }
        }
    }
}
