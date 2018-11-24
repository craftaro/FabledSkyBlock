package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.generator.GeneratorLocation;
import me.goodandevil.skyblock.generator.GeneratorManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Block implements Listener {

	private final Main plugin;
	
 	public Block(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		for (Location.World worldList : Location.World.values()) {
			if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
				PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
				GeneratorManager generatorManager = plugin.getGeneratorManager();
				IslandManager islandManager = plugin.getIslandManager();
				
				if (islandManager.hasPermission(player, "Destroy")) {
					for (UUID islandList : islandManager.getIslands().keySet()) {
						Island island = islandManager.getIslands().get(islandList);
						
						if (LocationUtil.isLocationAtLocationRadius(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
							if (generatorManager != null) {
								if (generatorManager.isGenerator(event.getBlock())) {
									if (playerDataManager.hasPlayerData(player)) {
										org.bukkit.block.Block block = event.getBlock();
										org.bukkit.block.Block liquid = null;
										
										if (NMSUtil.getVersionNumber() < 13) {
											BlockFace[] blockFaces = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
											
											for (BlockFace blockFaceList : blockFaces) {
												if (event.getBlock().getRelative(blockFaceList).getType() == Materials.LEGACY_STATIONARY_LAVA.getPostMaterial() || event.getBlock().getRelative(blockFaceList).getType() == Materials.LAVA.parseMaterial()) {
													liquid = event.getBlock().getRelative(blockFaceList);
													break;
												}
											}
										}
										
										playerDataManager.getPlayerData(player).setGenerator(new GeneratorLocation(worldList, block, liquid));
									}
								}
							}
							
							if (LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Main)) || LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Main).clone().add(0.0D, 1.0D, 0.0D)) || LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Main).clone().subtract(0.0D, 1.0D, 0.0D))) {
								if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
									event.setCancelled(true);	
								}
							}
							
							return;
						}
					}
					
					event.setCancelled(true);
				} else {
					event.setCancelled(true);
					
					plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					plugin.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
				
				break;
			}	
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		for (Location.World worldList : Location.World.values()) {
			if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
				IslandManager islandManager = plugin.getIslandManager();
				
				if (islandManager.hasPermission(player, "Place")) {
					for (UUID islandList : islandManager.getIslands().keySet()) {
						Island island = islandManager.getIslands().get(islandList);
						
						if (LocationUtil.isLocationAtLocationRadius(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
							Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
							FileConfiguration configLoad = config.getFileConfiguration();
							
							if (configLoad.getBoolean("Island.WorldBorder.Block")) {
								if (event.getBlock().getType() == Materials.PISTON.parseMaterial() || event.getBlock().getType() == Materials.STICKY_PISTON.parseMaterial()) {
									if (!LocationUtil.isLocationAtLocationRadius(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Island), 73)) {
										event.setCancelled(true);
									}
								} else if (event.getBlock().getType() == Material.DISPENSER) {
									if (!LocationUtil.isLocationAtLocationRadius(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Island), 83)) {
										event.setCancelled(true);
									}
								}
							}
							
							if (LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Main)) || LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Main).clone().add(0.0D, 1.0D, 0.0D)) || LocationUtil.isLocationLocation(event.getBlock().getLocation(), island.getLocation(worldList, Location.Environment.Main).clone().subtract(0.0D, 1.0D, 0.0D))) {
								if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
									event.setCancelled(true);	
								}
							}
							
							return;
						}
					}
					
					event.setCancelled(true);
				} else {
					event.setCancelled(true);
					
					plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					plugin.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
				
				break;
			}
		}
	}
	
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Piston.Connected.Extend")) {
			for (Location.World worldList : Location.World.values()) {
				if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
					for (org.bukkit.block.Block blockList : event.getBlocks()) {
						if (blockList.getType() == Materials.PISTON.parseMaterial() || blockList.getType() == Materials.STICKY_PISTON.parseMaterial()) {
							event.setCancelled(true);
							
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Piston.Connected.Retract")) {
			for (Location.World worldList : Location.World.values()) {
				if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
					for (org.bukkit.block.Block blockList : event.getBlocks()) {
						if (blockList.getType() == Materials.PISTON.parseMaterial() || blockList.getType() == Materials.STICKY_PISTON.parseMaterial()) {
							event.setCancelled(true);
							
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		for (Location.World worldList : Location.World.values()) {
			if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
				if (event.getBlock().getType() == Material.ICE || event.getBlock().getType() == Material.SNOW) {
					if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Weather.IceAndSnow")) {
						event.setCancelled(true);
					}
				} else {
					PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
					GeneratorManager generatorManager = plugin.getGeneratorManager();
					
					if (generatorManager != null) {
						org.bukkit.Location location = event.getBlock().getLocation();
						
						for (Player all : Bukkit.getOnlinePlayers()) {
							if (playerDataManager.hasPlayerData(all)) {
								PlayerData playerData = playerDataManager.getPlayerData(all);
								
								if (playerData.getGenerator() != null) {
									GeneratorLocation generatorLocation = playerData.getGenerator();
									
									if (generatorLocation.getWorld() == worldList) {
										if (location.getBlockX() == generatorLocation.getBlockX() && location.getBlockY() == generatorLocation.getBlockY() && location.getBlockZ() == generatorLocation.getBlockZ()) {
											event.setCancelled(true);
											generatorManager.generateBlock(all, event.getBlock());
											playerData.setGenerator(null);
											
											return;
										}
									}
								}
							}
						}
					}
				}
				
				return;
			}
		}
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if (NMSUtil.getVersionNumber() < 13) {
			for (Location.World worldList : Location.World.values()) {
				if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
					PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
					GeneratorManager generatorManager = plugin.getGeneratorManager();
					
					if (generatorManager != null) {
						org.bukkit.Location location = event.getBlock().getLocation();
						
						for (Player all : Bukkit.getOnlinePlayers()) {
							if (playerDataManager.hasPlayerData(all)) {
								PlayerData playerData = playerDataManager.getPlayerData(all);
								
								if (playerData.getGenerator() != null) {
									GeneratorLocation generatorLocation = playerData.getGenerator();
									
									if (generatorLocation.getWorld() == worldList) {
										if (location.getBlockX() == generatorLocation.getLiquidX() && location.getBlockY() == generatorLocation.getLiquidY() && location.getBlockZ() == generatorLocation.getLiquidZ()) {
											event.setCancelled(true);
											generatorManager.generateBlock(all, new org.bukkit.Location(location.getWorld(), generatorLocation.getBlockX(), generatorLocation.getBlockY(), generatorLocation.getBlockZ()).getBlock());
											playerData.setGenerator(null);
											
											return;
										}
									}
								}
							}
						}
					}
					
					return;
				}
			}	
		}
	}
	
	@EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		
		if (block.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || block.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			IslandManager islandManager = plugin.getIslandManager();
			
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(block.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						if (!island.getSetting(Settings.Role.Owner, "FireSpread").getStatus()) {
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
    public void onBlockSpread(BlockSpreadEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		
		if (block.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || block.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			IslandManager islandManager = plugin.getIslandManager();
			
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(block.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						if (!island.getSetting(Settings.Role.Owner, "FireSpread").getStatus()) {
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
	public void onLeavesDecay(LeavesDecayEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		
		if (block.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || block.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			IslandManager islandManager = plugin.getIslandManager();
			
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(block.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						if (!island.getSetting(Settings.Role.Owner, "LeafDecay").getStatus()) {
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
