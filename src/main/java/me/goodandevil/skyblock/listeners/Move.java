package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Move implements Listener {

	private final SkyBlock skyblock;
	
 	public Move(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
			String netherWorldName = skyblock.getWorldManager().getWorld(me.goodandevil.skyblock.island.Location.World.Nether).getName();
			
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(me.goodandevil.skyblock.island.Location.World.Normal).getName()) || player.getWorld().getName().equals(netherWorldName)) {
				PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
				MessageManager messageManager = skyblock.getMessageManager();
				IslandManager islandManager = skyblock.getIslandManager();
				SoundManager soundManager = skyblock.getSoundManager();
				FileManager fileManager = skyblock.getFileManager();
				
				if (player.getWorld().getName().equals(netherWorldName)) {
					if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.World.Nether.Enable")) {
						messageManager.sendMessage(player, fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.World.Nether.Message"));
						
						if (playerDataManager.hasPlayerData(player)) {
							PlayerData playerData = playerDataManager.getPlayerData(player);
							
							if (playerData.getIsland() != null) {
								Island island = islandManager.getIsland(playerData.getIsland());
								
								if (island != null) {
									if (island.getVisit().isVisitor(player.getUniqueId())) {
										player.teleport(island.getLocation(me.goodandevil.skyblock.island.Location.World.Normal, me.goodandevil.skyblock.island.Location.Environment.Visitor));
									} else {
										player.teleport(island.getLocation(me.goodandevil.skyblock.island.Location.World.Normal, me.goodandevil.skyblock.island.Location.Environment.Main));
									}
									
									soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
							}
						}
						
						LocationUtil.teleportPlayerToSpawn(player);
						soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
					}
				}
				
				if (playerDataManager.hasPlayerData(player)) {
					PlayerData playerData = playerDataManager.getPlayerData(player);
					UUID islandOwnerUUID = playerData.getIsland();
					
					if (islandOwnerUUID != null) {
						Island island = islandManager.getIsland(islandOwnerUUID);
						
						if (island != null) {
							me.goodandevil.skyblock.island.Location.World world = null;
							
							if (LocationUtil.isLocationAtLocationRadius(to, island.getLocation(me.goodandevil.skyblock.island.Location.World.Normal, me.goodandevil.skyblock.island.Location.Environment.Island), island.getRadius())) {
								world = me.goodandevil.skyblock.island.Location.World.Normal;
							} else if (LocationUtil.isLocationAtLocationRadius(to, island.getLocation(me.goodandevil.skyblock.island.Location.World.Nether, me.goodandevil.skyblock.island.Location.Environment.Island), island.getRadius())) {
								world = me.goodandevil.skyblock.island.Location.World.Nether;
							}
							
							if (world != null) {
								Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
								FileConfiguration configLoad = config.getFileConfiguration();
								
								boolean keepItemsOnDeath;
								
					 			if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
					 				if (island.getSetting(IslandRole.Owner, "KeepItemsOnDeath").getStatus()) {
					 					keepItemsOnDeath = true;
					 				} else {
					 					keepItemsOnDeath = false;
					 				}
					 			} else {
					 				keepItemsOnDeath = true;
					 			}
								
								if (configLoad.getBoolean("Island.World." + world.name() + ".Liquid.Enable")) {
									if (to.getY() <= configLoad.getInt("Island.World." + world.name() + ".Liquid.Height")) {
										if (keepItemsOnDeath) {
											player.setFallDistance(0.0F);
											
											player.teleport(island.getLocation(world, me.goodandevil.skyblock.island.Location.Environment.Main));
											soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
										}
										
										return;
									}
								}
								
								if (configLoad.getBoolean("Island.Void.Teleport.Enable")) {
									if (to.getY() <= configLoad.getInt("Island.Void.Teleport.Offset")) {
										if (!keepItemsOnDeath) {
											player.getInventory().clear();
											player.setLevel(0);
											player.setExp(0.0F);
											
											if (NMSUtil.getVersionNumber() > 8) {
												player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
											} else {
												player.setHealth(player.getMaxHealth());
											}
											
											player.setFoodLevel(20);
											
											for (PotionEffect potionEffect : player.getActivePotionEffects()) {
												player.removePotionEffect(potionEffect.getType());
											}
										}
										
										player.setFallDistance(0.0F);
										
										player.teleport(island.getLocation(world, me.goodandevil.skyblock.island.Location.Environment.Main));
										soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
									}
								}
							} else {
								if (LocationUtil.isLocationAtLocationRadius(to, island.getLocation(me.goodandevil.skyblock.island.Location.World.Normal, me.goodandevil.skyblock.island.Location.Environment.Island), island.getRadius() + 2) || LocationUtil.isLocationAtLocationRadius(to, island.getLocation(me.goodandevil.skyblock.island.Location.World.Nether, me.goodandevil.skyblock.island.Location.Environment.Island), island.getRadius() + 2)) {
									player.teleport(player.getLocation().add(from.toVector().subtract(to.toVector()).normalize().multiply(2.0D)));
									soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
								} else {
									if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(me.goodandevil.skyblock.island.Location.World.Normal).getName())) {
										player.teleport(island.getLocation(me.goodandevil.skyblock.island.Location.World.Normal, me.goodandevil.skyblock.island.Location.Environment.Main));
									} else {
										player.teleport(island.getLocation(me.goodandevil.skyblock.island.Location.World.Nether, me.goodandevil.skyblock.island.Location.Environment.Main));
									}
									
									messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.WorldBorder.Outside.Message"));
									soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
								}
							}
							
							return;
						}
					}
					
					LocationUtil.teleportPlayerToSpawn(player);
					
					messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.WorldBorder.Disappeared.Message"));
					soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
				}
			}	
		}
	}
}
