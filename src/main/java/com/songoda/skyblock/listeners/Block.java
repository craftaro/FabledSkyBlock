package com.songoda.skyblock.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.generator.Generator;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.LevellingManager;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.version.Sounds;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;

public class Block implements Listener {

	private final SkyBlock skyblock;

	public Block(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		org.bukkit.block.Block block = event.getBlock();

		IslandManager islandManager = skyblock.getIslandManager();
		StackableManager stackableManager = skyblock.getStackableManager();
		WorldManager worldManager = skyblock.getWorldManager();

		if (!worldManager.isIslandWorld(block.getWorld())) return;

		IslandWorld world = worldManager.getIslandWorld(block.getWorld());
		
		Location blockLocation = block.getLocation();
		
		Island island = islandManager.getIslandAtLocation(blockLocation);

		if (island == null) {
			event.setCancelled(true);
			return;
		}

		if (!islandManager.hasPermission(player, blockLocation, "Destroy")) {
			event.setCancelled(true);
			skyblock.getMessageManager().sendMessage(player,
					skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
							.getFileConfiguration().getString("Island.Settings.Permission.Message"));
			skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			return;
		}

		if (stackableManager != null
				&& stackableManager.isStacked(blockLocation)) {
			Stackable stackable = stackableManager.getStack(block.getLocation(), block.getType());
			if (stackable != null) {
				Material material = block.getType();
				byte data = block.getData();

				int droppedAmount = 0;
				if (event.getPlayer().isSneaking()) {
					Location dropLoc = blockLocation.clone().add(0.5, 0.5, 0.5);
					int count = stackable.getSize();
					droppedAmount = count;
					while (count > 64) {
						dropLoc.getWorld().dropItemNaturally(dropLoc, new ItemStack(material, 64, data));
						count -= 64;
					}
					dropLoc.getWorld().dropItemNaturally(dropLoc, new ItemStack(material, count, block.getData()));
					block.setType(Material.AIR);
					stackable.setSize(0);
				} else {
					block.getWorld().dropItemNaturally(blockLocation.clone().add(.5, 1, .5), new ItemStack(material, 1, data));
					stackable.takeOne();
					droppedAmount = 1;
				}

				if (stackable.getSize() <= 1) {
					stackableManager.removeStack(stackable);
				}

				Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
				FileConfiguration configLoad = config.getFileConfiguration();

				if (configLoad.getBoolean("Island.Block.Level.Enable")) {
					Materials materials = Materials.getMaterials(material, data);
					if (materials != null) {
						IslandLevel level = island.getLevel();

						if (level.hasMaterial(materials.name())) {
							long materialAmount = level.getMaterialAmount(materials.name());

							if (materialAmount - droppedAmount <= 0) {
								level.removeMaterial(materials.name());
							} else {
								level.setMaterialAmount(materials.name(), materialAmount - droppedAmount);
							}
						}
					}
				}

				event.setCancelled(true);
			}
		}

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Main).clone().subtract(0.0D, 1.0D, 0.0D))
				|| LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Main).clone())) {
			if (configLoad.getBoolean("Island.Spawn.Protection")) {
				event.setCancelled(true);
				skyblock.getMessageManager().sendMessage(player,
						skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
								.getFileConfiguration().getString("Island.SpawnProtection.Break.Message"));
				skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		}

		if (event.isCancelled() || !configLoad.getBoolean("Island.Block.Level.Enable")) return;

		Materials materials = Materials.getMaterials(block.getType(), block.getData());

		if (materials == null) return;

		IslandLevel level = island.getLevel();

		if (!level.hasMaterial(materials.name())) return;

		long materialAmount = level.getMaterialAmount(materials.name());

		if (materialAmount - 1 <= 0) {
			level.removeMaterial(materials.name());
		} else {
			level.setMaterialAmount(materials.name(), materialAmount - 1);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		org.bukkit.block.Block block = event.getBlock();

		IslandManager islandManager = skyblock.getIslandManager();
		WorldManager worldManager = skyblock.getWorldManager();
		LevellingManager levellingManager = skyblock.getLevellingManager();
		if (!worldManager.isIslandWorld(block.getWorld())) return;

		IslandWorld world = worldManager.getIslandWorld(block.getWorld());
		Island island = islandManager.getIslandAtLocation(block.getLocation());

		if (island == null) {
			event.setCancelled(true);
			return;
		}

		if (levellingManager.isIslandLevelBeingScanned(island)) {
			skyblock.getMessageManager().sendMessage(player,
					skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
							.getFileConfiguration().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
			event.setCancelled(true);
			return;
		}

		if (!islandManager.hasPermission(player, block.getLocation(), "Place")) {
			event.setCancelled(true);
			skyblock.getMessageManager().sendMessage(player,
					skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
							.getFileConfiguration().getString("Island.Settings.Permission.Message"));
			skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			return;
		}
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (configLoad.getBoolean("Island.WorldBorder.Block") && block.getType() == Material.DISPENSER) {
			if (!islandManager.isLocationAtIsland(island, block.getLocation(), world)) {
				event.setCancelled(true);
			}
		}

		// Check spawn protection
		if (configLoad.getBoolean("Island.Spawn.Protection")) {
			boolean isObstructing = false;
			// Directly on the block
			if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
				isObstructing = true;
			}

			// Specific check for beds
			if (!isObstructing && event.getBlock().getState().getData() instanceof org.bukkit.material.Bed) {
				BlockFace bedDirection = ((org.bukkit.material.Bed) event.getBlock().getState().getData()).getFacing();
				org.bukkit.block.Block bedBlock = block.getRelative(bedDirection);
				if (LocationUtil.isLocationAffectingIslandSpawn(bedBlock.getLocation(), island, world))
					isObstructing = true;
			}

			if (isObstructing) {
				skyblock.getMessageManager().sendMessage(player,
						skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
								.getFileConfiguration().getString("Island.SpawnProtection.Place.Message"));
				skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

				event.setCancelled(true);
				return;
			}
		}

		BlockLimitation limits = skyblock.getLimitationHandler().getInstance(BlockLimitation.class);
		
		long limit = limits.getBlockLimit(player, block);
		
		if (limits.isBlockLimitExceeded(player, block, limit)) {
			Materials material = Materials.getMaterials(block.getType(), block.getData());

			skyblock.getMessageManager().sendMessage(player,
					skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
							.getFileConfiguration().getString("Island.Limit.Block.Exceeded.Message")
							.replace("%type", WordUtils.capitalizeFully(material.name().replace("_", " ")))
							.replace("%limit", NumberUtil.formatNumber(limit)));
			skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

			event.setCancelled(true);
			return;
		}

		if (!configLoad.getBoolean("Island.Block.Level.Enable"))
			return;

		if (event.getBlock().getType() == Materials.END_PORTAL_FRAME.parseMaterial()
			&& event.getPlayer().getItemInHand().getType() == Materials.ENDER_EYE.parseMaterial()) return;

		// Fix a bug in Paper 1.8.8 when using ViaVersion on a 1.12.2 client.
		// BUG: Player can infinitely increase their level by placing a block at their feet.
		// It doesn't take the block away but still increments the level.
		// This doesn't happen in Spigot, but does happen in PaperSpigot due to a BlockPlaceEvent being incorrectly fired.
		// The solution is to wait a tick to make sure that the block was actually placed.
		// This shouldn't cause any issues besides the task number being increased insanely fast.
		Bukkit.getScheduler().runTask(skyblock, () -> {
			Materials materials = Materials.getMaterials(block.getType(), block.getData());
			if (materials == null || materials == Materials.AIR)
				return;

			if (materials.equals(Materials.SPAWNER)) {
				if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners") || Bukkit.getPluginManager().isPluginEnabled("WildStacker"))
					return;

				CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
				EntityType spawnerType = creatureSpawner.getSpawnedType();
				materials = Materials.getSpawner(spawnerType);
			}

			long materialAmount = 0;
			IslandLevel level = island.getLevel();

			if (level.hasMaterial(materials.name())) {
				materialAmount = level.getMaterialAmount(materials.name());
			}

			level.setMaterialAmount(materials.name(), materialAmount + 1);
		});
	}


	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if (!skyblock.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

		GeneratorManager generatorManager = skyblock.getGeneratorManager();
		IslandManager islandManager = skyblock.getIslandManager();
		WorldManager worldManager = skyblock.getWorldManager();

		Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
		IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (island == null) return;

		org.bukkit.block.Block block = event.getToBlock();

		// Protect outside of border
		if (!islandManager.isLocationAtIsland(island, block.getLocation(), world)) {
			event.setCancelled(true);
			return;
		}

		// Protect spawn
		if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && configLoad.getBoolean("Island.Spawn.Protection")) {
			event.setCancelled(true);
			return;
		}

		if (NMSUtil.getVersionNumber() < 12) {
			if (generatorManager != null && generatorManager.getGenerators().size() > 0 && generatorManager.isGenerator(block)) {
				List<Generator> generators = new ArrayList<>(generatorManager.getGenerators());
				Collections.reverse(generators); // Use the highest generator available

				// Filter valid players on the island
				Set<Player> possiblePlayers = new HashSet<>();
				for (Player p : Bukkit.getOnlinePlayers()) {
					boolean isMember = island.hasRole(IslandRole.Owner, p.getUniqueId()) ||
							island.hasRole(IslandRole.Member, p.getUniqueId()) ||
							island.hasRole(IslandRole.Coop, p.getUniqueId()) ||
							island.hasRole(IslandRole.Operator, p.getUniqueId());
					if (isMember && islandManager.isLocationAtIsland(island, p.getLocation(), world)) {
						possiblePlayers.add(p);
					}
				}

				// Find highest generator available
				for (Generator generator : generators) {
					for (Player p : possiblePlayers) {
						if (generator.isPermission()) {
							if (!p.hasPermission(generator.getPermission())
									&& !p.hasPermission("fabledskyblock.generator.*")
									&& !p.hasPermission("fabledskyblock.*")) {
								continue;
							}
						}

						org.bukkit.block.BlockState genState = generatorManager.generateBlock(generator, block);
						event.getToBlock().getState().setType(genState.getType());
						event.getToBlock().getState().setData(genState.getData());

						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		WorldManager worldManager = skyblock.getWorldManager();
		if (!worldManager.isIslandWorld(event.getBlock().getWorld()))
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
		if (island == null)
			return;

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());
		for (org.bukkit.block.Block block : event.getBlocks()) {
			if (!islandManager.isLocationAtIsland(island, block.getLocation(), world) || !islandManager.isLocationAtIsland(island, block.getRelative(event.getDirection()).getLocation(), world)) {
				event.setCancelled(true);
				return;
			}

			if (skyblock.getStackableManager() != null && skyblock.getStackableManager().isStacked(block.getLocation())) {
				event.setCancelled(true);
				return;
			}

			if (configLoad.getBoolean("Island.Spawn.Protection")) {
				// Check exact block
				if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
					event.setCancelled(true);
					return;
				}

				// Check block in direction
				if (LocationUtil.isLocationAffectingIslandSpawn(block.getRelative(event.getDirection()).getLocation(), island, world)) {
					event.setCancelled(true);
					return;
				}
			}

			if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Extend")) {
				if (block.getType() == Materials.PISTON.parseMaterial() || block.getType() == Materials.STICKY_PISTON.parseMaterial()) {
					event.setCancelled(true);
					return;
				}
			}
		}

		// Check piston head
		if (configLoad.getBoolean("Island.Spawn.Protection")) {
			if (LocationUtil.isLocationAffectingIslandSpawn(event.getBlock().getRelative(event.getDirection()).getLocation(), island, world)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		WorldManager worldManager = skyblock.getWorldManager();
		if (!skyblock.getWorldManager().isIslandWorld(event.getBlock().getWorld()))
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
		if (island == null)
			return;

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());
		for (org.bukkit.block.Block block : event.getBlocks()) {
			if (!islandManager.isLocationAtIsland(island, block.getLocation(), world)) {
				event.setCancelled(true);
				return;
			}

			if (skyblock.getStackableManager() != null && skyblock.getStackableManager().isStacked(block.getLocation())) {
				event.setCancelled(true);
				return;
			}

			if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && configLoad.getBoolean("Island.Spawn.Protection")) {
				event.setCancelled(true);
				return;
			}

			if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Retract")) {
				if (block.getType() == Materials.PISTON.parseMaterial() || block.getType() == Materials.STICKY_PISTON.parseMaterial()) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		WorldManager worldManager = skyblock.getWorldManager();
		if (!worldManager.isIslandWorld(block.getWorld()))
			return;

		// Check ice/snow forming
		if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
			if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Weather.IceAndSnow"))
				event.setCancelled(true);
			return;
		}

		IslandManager islandManager = skyblock.getIslandManager();
		Island island = islandManager.getIslandAtLocation(block.getLocation());
		if (island == null)
			return;

		// Check spawn block protection
		IslandWorld world = worldManager.getIslandWorld(block.getWorld());
		if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
			if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
				event.setCancelled(true);
				return;
			}
		}

		Material material = block.getType();
		if (material != Materials.WATER.parseMaterial() &&
				material != Materials.LEGACY_STATIONARY_WATER.parseMaterial() &&
				material != Materials.LAVA.parseMaterial() &&
				material != Materials.LEGACY_STATIONARY_LAVA.parseMaterial())
			return;

		BlockState state = event.getNewState();
		Material type = state.getType();
		if (type != Material.COBBLESTONE && type != Material.STONE)
			return;

		GeneratorManager generatorManager = skyblock.getGeneratorManager();
        if(generatorManager == null) return;
        
		List<Generator> generators = Lists.newArrayList(generatorManager.getGenerators());
        if(generators == null || generators.isEmpty()) return;
		Collections.reverse(generators); // Use the highest generator available

		// Filter valid players on the island.
		Set<Player> possiblePlayers = new HashSet<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			boolean isMember = island.hasRole(IslandRole.Owner, player.getUniqueId()) ||
					island.hasRole(IslandRole.Member, player.getUniqueId()) ||
					island.hasRole(IslandRole.Coop, player.getUniqueId()) ||
					island.hasRole(IslandRole.Operator, player.getUniqueId());
			if (isMember && islandManager.isLocationAtIsland(island, player.getLocation(), world)) {
				possiblePlayers.add(player);
			}
		}

		// Find highest generator available
		for (Generator generator : generators) {
			for (Player player : possiblePlayers) {
				if (generator.isPermission()) {
					if (!player.hasPermission(generator.getPermission())
							&& !player.hasPermission("fabledskyblock.generator.*")
							&& !player.hasPermission("fabledskyblock.*")) {
						continue;
					}
				}

				org.bukkit.block.BlockState genState = generatorManager.generateBlock(generator, block);
				state.setType(genState.getType());

				if (NMSUtil.getVersionNumber() < 13)
					state.setData(genState.getData());
				return;
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		WorldManager worldManager = skyblock.getWorldManager();
		if (!worldManager.isIslandWorld(block.getWorld()))
			return;
		
		IslandManager islandManager = skyblock.getIslandManager();
		if (!islandManager.hasSetting(block.getLocation(), IslandRole.Owner, "FireSpread"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPortalCreate(PortalCreateEvent event) {
		if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
			return;

		WorldManager worldManager = skyblock.getWorldManager();
		IslandManager islandManager = skyblock.getIslandManager();
		// PortalCreateEvent.getBlocks() changed from ArrayList<Block> to ArrayList<BlockState> in 1.14.1
		if (NMSUtil.getVersionNumber() > 13) {
			List<BlockState> blocks = event.getBlocks();
			if (event.getBlocks().isEmpty())
				return;

			Island island = islandManager.getIslandAtLocation(event.getBlocks().get(0).getLocation());
			if (island == null)
				return;

			// Check spawn block protection
			IslandWorld world = worldManager.getIslandWorld(event.getBlocks().get(0).getWorld());

			for (BlockState block : blocks) {
				if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
					event.setCancelled(true);
					return;
				}
			}
		} else {
			try {
				@SuppressWarnings("unchecked")
				List<org.bukkit.block.Block> blocks = (List<org.bukkit.block.Block>) event.getClass().getMethod("getBlocks").invoke(event);
				if (blocks.isEmpty())
					return;

				Island island = islandManager.getIslandAtLocation(blocks.get(0).getLocation());
				if (island == null)
					return;

				// Check spawn block protection
				IslandWorld world = worldManager.getIslandWorld(blocks.get(0).getWorld());
				for (org.bukkit.block.Block block : blocks) {
					if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
						event.setCancelled(true);
						return;
					}
				}
			} catch (ReflectiveOperationException ex) {
				ex.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onDispenserDispenseBlock(BlockDispenseEvent event) {
		if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
			return;

		WorldManager worldManager = skyblock.getWorldManager();
		IslandManager islandManager = skyblock.getIslandManager();
		@SuppressWarnings("deprecation")
		BlockFace dispenserDirection = ((org.bukkit.material.Dispenser) event.getBlock().getState().getData()).getFacing();
		org.bukkit.block.Block placeLocation = event.getBlock().getRelative(dispenserDirection);

		Island island = islandManager.getIslandAtLocation(placeLocation.getLocation());
		if (island == null)
			return;

		// Check spawn block protection
		IslandWorld world = worldManager.getIslandWorld(placeLocation.getWorld());

		if (LocationUtil.isLocationAffectingIslandSpawn(placeLocation.getLocation(), island, world))
			event.setCancelled(true);
	}

}
