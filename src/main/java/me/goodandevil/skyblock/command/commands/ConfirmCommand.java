package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.confirmation.Confirmation;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.structure.StructureManager;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class ConfirmCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public ConfirmCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		StructureManager structureManager = skyblock.getStructureManager();
		MessageManager messageManager = skyblock.getMessageManager();
		EconomyManager economyManager = skyblock.getEconomyManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);

			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			if (playerData.getConfirmationTime() > 0) {
				Island island = islandManager.getIsland(player);

				if (island == null) {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Confirmation.Owner.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				} else {
					Confirmation confirmation = playerData.getConfirmation();

					if (confirmation == Confirmation.Ownership || confirmation == Confirmation.Reset
							|| confirmation == Confirmation.Deletion) {
						if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
							if (confirmation == Confirmation.Ownership) {
								UUID targetPlayerUUID = playerData.getOwnership();

								if (island.hasRole(IslandRole.Member, targetPlayerUUID)
										|| island.hasRole(IslandRole.Operator, targetPlayerUUID)) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Confirmation.Confirmed.Message"));

									String targetPlayerName;
									Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerUUID);

									if (targetPlayer == null) {
										targetPlayerName = new OfflinePlayer(targetPlayerUUID).getName();
									} else {
										targetPlayerName = targetPlayer.getName();
										messageManager.sendMessage(targetPlayer, configLoad
												.getString("Command.Island.Confirmation.Ownership.Assigned.Message"));
										soundManager.playSound(targetPlayer, Sounds.ANVIL_USE.bukkitSound(), 1.0F,
												1.0F);
									}

									for (Player all : Bukkit.getOnlinePlayers()) {
										if ((island.hasRole(IslandRole.Member, all.getUniqueId())
												|| island.hasRole(IslandRole.Operator, all.getUniqueId())
												|| island.hasRole(IslandRole.Owner, all.getUniqueId())
												|| island.hasRole(IslandRole.Owner, all.getUniqueId()))
												&& (!all.getUniqueId().equals(targetPlayerUUID))) {
											all.sendMessage(ChatColor.translateAlternateColorCodes('&',
													configLoad.getString(
															"Command.Island.Ownership.Assigned.Broadcast.Message")
															.replace("%player", targetPlayerName)));
											soundManager.playSound(all, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
										}
									}

									playerData.setConfirmation(null);
									playerData.setConfirmationTime(0);

									islandManager.giveOwnership(island,
											Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));

									skyblock.getCooldownManager().createPlayer(CooldownType.Ownership,
											Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
								} else {
									messageManager.sendMessage(player, configLoad
											.getString("Command.Island.Confirmation.Ownership.Member.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								}
							} else if (confirmation == Confirmation.Reset) {
								playerData.setConfirmation(null);
								playerData.setConfirmationTime(0);
							} else if (confirmation == Confirmation.Deletion) {
								if (island.isOpen()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Confirmation.Deletion.Open.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								} else {
									Location spawnLocation = LocationUtil.getSpawnLocation();

									if (spawnLocation != null
											&& islandManager.isLocationAtIsland(island, spawnLocation)) {
										messageManager.sendMessage(player, configLoad
												.getString("Command.Island.Confirmation.Deletion.Spawn.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										return;
									}

									if (economyManager.isEconomy() && island.getStructure() != null
											&& !island.getStructure().isEmpty()
											&& structureManager.containsStructure(island.getStructure())) {
										Structure structure = structureManager.getStructure(island.getStructure());
										double deletionCost = structure.getDeletionCost();

										if (deletionCost != 0.0D) {
											if (economyManager.hasBalance(player, deletionCost)) {
												economyManager.withdraw(player, deletionCost);
											} else {
												messageManager.sendMessage(player,
														configLoad.getString(
																"Command.Island.Confirmation.Deletion.Money.Message")
																.replace("%cost", "" + deletionCost));
												soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
														1.0F);

												return;
											}
										}
									}

									playerData.setConfirmation(null);
									playerData.setConfirmationTime(0);

									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Confirmation.Confirmed.Message"));

									for (Player all : Bukkit.getOnlinePlayers()) {
										if (island.hasRole(IslandRole.Member, all.getUniqueId())
												|| island.hasRole(IslandRole.Operator, all.getUniqueId())) {
											all.sendMessage(
													ChatColor.translateAlternateColorCodes('&', configLoad.getString(
															"Command.Island.Confirmation.Deletion.Broadcast.Message")));
											soundManager.playSound(all, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
										}
									}

									island.setDeleted(true);
									islandManager.deleteIsland(island);

									messageManager.sendMessage(player, configLoad
											.getString("Command.Island.Confirmation.Deletion.Sender.Message"));
									soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
								}
							}
						} else {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Confirmation.Role.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Confirmation.Specified.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Pending.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		}

	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "confirm";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public SubCommand setInfo(String info) {
		this.info = info;

		return this;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "confirmation" };
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
