package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class AddUpgradeCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public AddUpgradeCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		onCommand(player, args);
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		onCommand(sender, args);
	}

	public void onCommand(CommandSender sender, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (player == null || player.hasPermission("fabledskyblock.admin.addupgrade")
				|| player.hasPermission("fabledskyblock.admin.*") || player.hasPermission("fabledskyblock.*")) {
			if (args.length == 2) {
				Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
				UUID islandOwnerUUID;
				String targetPlayerName;

				if (targetPlayer == null) {
					OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
					islandOwnerUUID = targetPlayerOffline.getOwner();
					targetPlayerName = targetPlayerOffline.getName();
				} else {
					islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
					targetPlayerName = targetPlayer.getName();
				}

				Upgrade.Type upgrade = null;

				for (Upgrade.Type upgradeList : Upgrade.Type.values()) {
					if (upgradeList != Upgrade.Type.Size) {
						if (args[1].toUpperCase().equals(upgradeList.name().toUpperCase())) {
							upgrade = upgradeList;

							break;
						}
					}
				}

				if (islandOwnerUUID == null) {
					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.AddUpgrade.Island.Owner.Message"));
					soundManager.playSound(sender, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				} else if (upgrade == null) {
					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Exist.Message"));
					soundManager.playSound(sender, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				} else {
					if (islandManager.containsIsland(islandOwnerUUID)) {
						Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));

						if (island.hasUpgrade(upgrade)) {
							messageManager.sendMessage(sender,
									configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Already.Message"));
							soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						island.setUpgrade(null, upgrade, true);
					} else {
						File islandDataFile = new File(skyblock.getDataFolder().toString() + "/island-data",
								islandOwnerUUID.toString() + ".yml");

						if (!fileManager.isFileExist(islandDataFile)) {
							messageManager.sendMessage(sender,
									configLoad.getString("Command.Island.Admin.AddUpgrade.Island.Data.Message"));
							soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						FileConfiguration islandDataConfigLoad = YamlConfiguration.loadConfiguration(islandDataFile);

						if (islandDataConfigLoad.getString("Upgrade." + upgrade.name()) != null) {
							messageManager.sendMessage(sender,
									configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Already.Message"));
							soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						islandDataConfigLoad.set("Upgrade." + upgrade.name(), true);

						try {
							islandDataConfigLoad.save(islandDataFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.AddUpgrade.Added.Message")
									.replace("%player", targetPlayerName).replace("%upgrade", upgrade.name()));
					soundManager.playSound(sender, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(sender,
						configLoad.getString("Command.Island.Admin.AddUpgrade.Invalid.Message"));
				soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(sender,
					configLoad.getString("Command.Island.Admin.AddUpgrade.Permission.Message"));
			soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "addupgrade";
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
		return new String[0];
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Admin;
	}
}
