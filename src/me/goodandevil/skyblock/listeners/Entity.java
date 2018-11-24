package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Entity implements Listener {
	
	private final Main plugin;
	
 	public Entity(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		MessageManager messageManager = plugin.getMessageManager();
		SoundManager soundManager = plugin.getSoundManager();
		
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			
			if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (event.getEntity() instanceof Player) {
					IslandManager islandManager = plugin.getIslandManager();
					
					for (UUID islandList : islandManager.getIslands().keySet()) {
						Island island = islandManager.getIslands().get(islandList);
						
						for (Location.World worldList : Location.World.values()) {
							if (LocationUtil.isLocationAtLocationRadius(event.getEntity().getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
								if (!island.getSetting(Settings.Role.Owner, "PvP").getStatus()) {
									event.setCancelled(true);
								}
								
								return;
							}
						}
					}
				} else {
					if (!plugin.getIslandManager().hasPermission(player, "MobHurting")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);	
						
						return;
					}
				}
			}
		}
		
		if (event.getEntity() instanceof ArmorStand) {
			if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				
				if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
					if (!plugin.getIslandManager().hasPermission(player, "Destroy")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
				}
			} else if (event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				
				if (projectile.getShooter() instanceof Player) {
					Player player = (Player) projectile.getShooter();
					
					if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
						if (!plugin.getIslandManager().hasPermission(player, "Destroy")) {
							event.setCancelled(true);
							
							messageManager.sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		Player player = event.getPlayer();
		
		if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (!plugin.getIslandManager().hasPermission(player, "Shearing")) {
				event.setCancelled(true);
				
				plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
				plugin.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		}
	}
	
	@EventHandler
	public void onEntityTaming(EntityTameEvent event) {
		if (Bukkit.getServer().getPlayer(event.getOwner().getUniqueId()) != null) {
			Player player = Bukkit.getServer().getPlayer(event.getOwner().getUniqueId());
			
			if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!plugin.getIslandManager().hasPermission(player, "MobTaming")) {
					event.setCancelled(true);
					
					plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					plugin.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		org.bukkit.entity.Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
			if (entity.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || entity.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
				IslandManager islandManager = plugin.getIslandManager();
				
				for (UUID islandList : islandManager.getIslands().keySet()) {
					Island island = islandManager.getIslands().get(islandList);
					
					for (Location.World worldList : Location.World.values()) {
						if (LocationUtil.isLocationAtLocationRadius(entity.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
							if (!island.getSetting(Settings.Role.Owner, "MobGriefing").getStatus()) {
								event.setCancelled(true);
							}
							
							return;
						}
					}
				}
				
				event.setCancelled(true);
			}	
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		org.bukkit.entity.Entity entity = event.getEntity();
		
		if (entity.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || entity.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			IslandManager islandManager = plugin.getIslandManager();
			
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(entity.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						if (!island.getSetting(Settings.Role.Owner, "Explosions").getStatus()) {
							event.setCancelled(true);
						}
						
						return;
					}
				}
			}
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.NATURAL) {
			LivingEntity livingEntity = event.getEntity();
			
			if (livingEntity.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || livingEntity.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!livingEntity.hasMetadata("SkyBlock")) {
					IslandManager islandManager = plugin.getIslandManager();
					
					for (UUID islandList : islandManager.getIslands().keySet()) {
						Island island = islandManager.getIslands().get(islandList);
						
						for (Location.World worldList : Location.World.values()) {
							if (LocationUtil.isLocationAtLocationRadius(livingEntity.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
								if (!island.getSetting(Settings.Role.Owner, "NaturalMobSpawning").getStatus()) {
									livingEntity.remove();
								}
								
								return;
							}
						}
					}
					
					livingEntity.remove();	
				}
			}
		}
	}
}
