package me.goodandevil.skyblock.command.commands.island;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.*;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class SetSpawnCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			Island island = islandManager.getIsland(player);

			if (island == null) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else {
				IslandEnvironment environment;

				if (args[0].equalsIgnoreCase("Main")) {
					environment = IslandEnvironment.Main;
				} else if (args[0].equalsIgnoreCase("Visitor")) {
					environment = IslandEnvironment.Visitor;
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Spawn.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				}

				if (island.hasRole(IslandRole.Operator, player.getUniqueId())
						|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
					if ((island.hasRole(IslandRole.Operator, player.getUniqueId())
							&& (island.getSetting(IslandRole.Operator, environment.name() + "Spawn").getStatus()))
							|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
						if (islandManager.isPlayerAtIsland(island, player)) {
							IslandWorld world = skyblock.getWorldManager().getIslandWorld(player.getWorld());
							Location location = player.getLocation();

							if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
									.getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
								if (location.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.AIR
										|| location.clone().subtract(0.0D, 1.0D, 0.0D).getBlock()
												.getType() == Materials.LEGACY_PISTON_MOVING_PIECE.getPostMaterial()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.SetSpawn.Protection.Block.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else if (!player.getLocation().clone().subtract(0, 0.1, 0).getBlock().getType().isSolid()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.SetSpawn.Protection.Ground.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else if (location.getBlock().isLiquid()
										|| location.clone().add(0.0D, 1.0D, 0.0D).getBlock().isLiquid()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.SetSpawn.Protection.Liquid.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else if (location.getBlock().getType() == Materials.NETHER_PORTAL.parseMaterial()
										|| location.clone().add(0.0D, 1.0D, 0.0D).getBlock()
												.getType() == Materials.NETHER_PORTAL.parseMaterial()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.SetSpawn.Protection.Portal.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else {
									Material type = location.getBlock().getType();
									if (type.isSolid() && type.isOccluding()) {
										location.getBlock().breakNaturally();
									}

									Material typeBelow = location.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType();
									if (typeBelow.isSolid() && type.isOccluding()) {
										location.clone().add(0.0D, 1.0D, 0.0D).getBlock().breakNaturally();
									}

									islandManager.removeSpawnProtection(island.getLocation(world, environment));
								}
							}

							Location newSpawnLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
							island.setLocation(world, environment, newSpawnLocation);

							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.SetSpawn.Set.Message").replace("%spawn",
											environment.name().toLowerCase()));
							soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.SetSpawn.Island.Message").replace("%spawn",
										environment.name().toLowerCase()));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.SetSpawn.Permission.Message").replace("%spawn",
										environment.name().toLowerCase()));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Role.Message")
							.replace("%spawn", environment.name().toLowerCase()));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "setspawn";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.SetSpawn.Info.Message";
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public String[] getArguments() {
		return new String[] { "main", "visitor" };
	}
}
