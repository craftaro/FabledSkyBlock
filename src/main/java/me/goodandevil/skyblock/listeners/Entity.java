package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Entity implements Listener {

	private final SkyBlock skyblock;

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

			if (configLoad.getBoolean("Island.Settings.PvP.Enable")) {
				if (!configLoad.getBoolean("Island.Settings.PvP.Enable")
						|| !skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "Damage")) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
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
					if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Settings.PvP.Enable")) {
						if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "PvP")) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);

						return;
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
					if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Settings.PvP.Enable")) {
						if (!islandManager.hasSetting(entity.getLocation(), IslandRole.Owner, "PvP")) {
							event.setCancelled(true);
						}
					} else {
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
				if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
						.getBoolean("Island.Settings.Damage.Enable")) {
					if (!islandManager.hasSetting(player.getLocation(), IslandRole.Owner, "Damage")
							|| (event.getDamager() instanceof TNTPrimed && !islandManager
									.hasSetting(player.getLocation(), IslandRole.Owner, "Explosions"))) {
						event.setCancelled(true);
					}
				} else {
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

		if (entity instanceof FallingBlock || entity instanceof Player) {
			return;
		}

		if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
			if (!skyblock.getIslandManager().hasSetting(entity.getLocation(), IslandRole.Owner, "MobGriefing")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		org.bukkit.entity.Entity entity = event.getEntity();

		if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
			if (!skyblock.getIslandManager().hasSetting(entity.getLocation(), IslandRole.Owner, "Explosions")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
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
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);

				if (islandManager.isLocationAtIsland(island, livingEntity.getLocation())) {
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

		if (event.getSpawnReason() != SpawnReason.NATURAL) {
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
