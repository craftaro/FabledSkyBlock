package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.world.WorldManager;

public class Portal implements Listener {

	private final SkyBlock skyblock;

	public Portal(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();
		org.bukkit.block.Block block = event.getLocation().getBlock();

		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		WorldManager worldManager = skyblock.getWorldManager();
		FileManager fileManager = skyblock.getFileManager();

		if (!worldManager.isIslandWorld(player.getWorld())) {
			return;
		}

		IslandWorld world = worldManager.getIslandWorld(player.getWorld());
		Island island = islandManager.getIslandAtLocation(player.getLocation());

		if (island != null) {
			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			if (((block.getType() == Materials.NETHER_PORTAL.parseMaterial()
					&& configLoad.getBoolean("Island.World.Nether.Enable"))
					|| (block.getType() == Materials.END_PORTAL.parseMaterial()
							&& configLoad.getBoolean("Island.World.End.Enable")))
					&& islandManager.hasPermission(player, "Portal")) {
				if (configLoad.getBoolean("Island.Portal.Island")) {
					if (island.hasRole(IslandRole.Member, player.getUniqueId())
							|| island.hasRole(IslandRole.Operator, player.getUniqueId())
							|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
						if (world == IslandWorld.Normal) {
							if (block.getType() == Materials.NETHER_PORTAL.parseMaterial()) {
								player.teleport(island.getLocation(IslandWorld.Nether, IslandEnvironment.Main));
							} else if (block.getType() == Materials.END_PORTAL.parseMaterial()) {
								player.teleport(island.getLocation(IslandWorld.End, IslandEnvironment.Main));
							}
						} else {
							player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
						}
					} else {
						if (world == IslandWorld.Normal) {
							if (block.getType() == Materials.NETHER_PORTAL.parseMaterial()) {
								player.teleport(island.getLocation(IslandWorld.Nether, IslandEnvironment.Visitor));
							} else if (block.getType() == Materials.END_PORTAL.parseMaterial()) {
								player.teleport(island.getLocation(IslandWorld.End, IslandEnvironment.Visitor));
							}
						} else {
							player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
						}
					}

					soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
				} else if (block.getType() == Materials.NETHER_PORTAL.parseMaterial()
						&& Bukkit.getServer().getAllowNether()) {
					for (World worldList : Bukkit.getServer().getWorlds()) {
						if (worldList.getEnvironment() == Environment.NETHER) {
							player.teleport(LocationUtil.getRandomLocation(worldList, 5000, 5000, true, true));

							break;
						}
					}

					soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
				} else if (block.getType() == Materials.END_PORTAL.parseMaterial()
						&& Bukkit.getServer().getAllowEnd()) {
					for (World worldList : Bukkit.getServer().getWorlds()) {
						if (worldList.getEnvironment() == Environment.THE_END) {
							player.teleport(worldList.getSpawnLocation());

							break;
						}
					}

					soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
				} else {
					if (island.hasRole(IslandRole.Member, player.getUniqueId())
							|| island.hasRole(IslandRole.Operator, player.getUniqueId())
							|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
						player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
					} else {
						player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
					}

					messageManager.sendMessage(player,
							fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
									.getFileConfiguration().getString("Island.Portal.Destination.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				if (island.hasRole(IslandRole.Member, player.getUniqueId())
						|| island.hasRole(IslandRole.Operator, player.getUniqueId())
						|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
					player.teleport(island.getLocation(world, IslandEnvironment.Main));
				} else {
					player.teleport(island.getLocation(world, IslandEnvironment.Visitor));
				}

				messageManager.sendMessage(player,
						fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
								.getString("Island.Settings.Permission.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}

			player.setFallDistance(0.0F);

			return;
		}
	}
}
