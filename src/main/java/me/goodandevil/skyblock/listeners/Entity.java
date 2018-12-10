package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

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

		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
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

			if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
					.getFileConfiguration().getBoolean("Island.Settings.PvP.Enable")) {
				if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getBoolean("Island.Settings.PvP.Enable")
						|| !skyblock.getIslandManager().hasSetting(event.getEntity().getLocation(), IslandRole.Owner,
								"Damage")) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);

				return;
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();

			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
					|| player.getWorld().getName()
							.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (event.getEntity() instanceof Player) {
					if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Settings.PvP.Enable")) {
						if (!skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "PvP")) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);

						return;
					}
				} else if (event.getEntity() instanceof ArmorStand) {
					if (player.getWorld().getName()
							.equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
							|| player.getWorld().getName()
									.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
						if (!skyblock.getIslandManager().hasPermission(player, "Destroy")) {
							event.setCancelled(true);

							messageManager.sendMessage(player,
									skyblock.getFileManager()
											.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
											.getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						}
					}
				} else {
					if (!skyblock.getIslandManager().hasPermission(player, "MobHurting")) {
						event.setCancelled(true);

						messageManager.sendMessage(player,
								skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
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

			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
					|| player.getWorld().getName()
							.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (event.getEntity() instanceof Player) {
					if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Settings.PvP.Enable")) {
						if (!skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "PvP")) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				} else {
					if (!skyblock.getIslandManager().hasPermission(player, "MobHurting")) {
						event.setCancelled(true);

						messageManager.sendMessage(player,
								skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
										.getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

						return;
					}
				}
			}
		} else {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();

				if (player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
						|| player.getWorld().getName()
								.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
					if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Settings.Damage.Enable")) {
						if (!skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner, "Damage")) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		Player player = event.getPlayer();

		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (!skyblock.getIslandManager().hasPermission(player, "Shearing")) {
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

		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (!skyblock.getIslandManager().hasPermission(player, "MobTaming")) {
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

		if (entity instanceof FallingBlock) {
			return;
		}

		if (!(entity instanceof Player)) {
			if (entity.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
					|| entity.getWorld().getName()
							.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!skyblock.getIslandManager().hasSetting(entity.getLocation(), IslandRole.Owner, "MobGriefing")) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		org.bukkit.entity.Entity entity = event.getEntity();

		if (entity.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| entity.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (!skyblock.getIslandManager().hasSetting(event.getEntity().getLocation(), IslandRole.Owner,
					"Explosions")) {
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

		if (livingEntity.getWorld().getName()
				.equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| livingEntity.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (!livingEntity.hasMetadata("SkyBlock")) {
				IslandManager islandManager = skyblock.getIslandManager();

				for (UUID islandList : islandManager.getIslands().keySet()) {
					Island island = islandManager.getIslands().get(islandList);

					for (Location.World worldList : Location.World.values()) {
						if (LocationUtil.isLocationAtLocationRadius(livingEntity.getLocation(),
								island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
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

		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (!skyblock.getIslandManager().hasPermission(player, "ExperienceOrbPickup")) {
				event.setTarget(null);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.NATURAL) {
			LivingEntity livingEntity = event.getEntity();

			if (event.getEntity() instanceof ArmorStand || event.getEntity() instanceof FallingBlock
					|| event.getEntity() instanceof org.bukkit.entity.Item) {
				return;
			}

			if (livingEntity.getWorld().getName()
					.equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
					|| livingEntity.getWorld().getName()
							.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!livingEntity.hasMetadata("SkyBlock")) {
					if (!skyblock.getIslandManager().hasSetting(event.getEntity().getLocation(), IslandRole.Owner,
							"NaturalMobSpawning")) {
						livingEntity.remove();
					}
				}
			}
		}
	}
}
