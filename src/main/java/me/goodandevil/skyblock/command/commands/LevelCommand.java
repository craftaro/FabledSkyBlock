package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.cooldown.Cooldown;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownPlayer;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.menus.Levelling;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.VisitManager;

public class LevelCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public LevelCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		LevellingManager levellingManager = skyblock.getLevellingManager();
		CooldownManager cooldownManager = skyblock.getCooldownManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		VisitManager visitManager = skyblock.getVisitManager();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			if (player.hasPermission("fabledskyblock.level") || player.hasPermission("fabledskyblock.*")) {
				Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
				UUID islandOwnerUUID = null;
				String targetPlayerName;

				if (targetPlayer == null) {
					OfflinePlayer targetOfflinePlayer = new OfflinePlayer(args[0]);
					islandOwnerUUID = targetOfflinePlayer.getOwner();
					targetPlayerName = targetOfflinePlayer.getName();
				} else {
					islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
					targetPlayerName = targetPlayer.getName();
				}

				if (islandOwnerUUID == null) {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Level.Owner.Other.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (!islandOwnerUUID.equals(playerDataManager.getPlayerData(player).getOwner())) {
					if (visitManager.hasIsland(islandOwnerUUID)) {
						me.goodandevil.skyblock.visit.Visit visit = visitManager.getIsland(islandOwnerUUID);

						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Level.Level.Message")
										.replace("%player", targetPlayerName).replace("%level",
												"" + NumberUtil.formatNumberByDecimal(visit.getLevel().getLevel())));
						soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

						return;
					}

					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Level.Owner.Other.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Permission.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

				return;
			}
		} else if (args.length != 0) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

			return;
		}

		Island island = islandManager.getIsland(player);

		if (island == null) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Owner.Yourself.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		} else {
			player.closeInventory();

			if (!island.getLevel().hasMaterials()) {
				org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());

				if (cooldownManager.hasPlayer(CooldownType.Levelling, offlinePlayer)) {
					CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Levelling,
							offlinePlayer);
					Cooldown cooldown = cooldownPlayer.getCooldown();

					long[] durationTime = NumberUtil.getDuration(cooldown.getTime());

					if (cooldown.getTime() >= 3600) {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
										durationTime[1] + " "
												+ configLoad.getString("Command.Island.Level.Cooldown.Word.Minute")
												+ " " + durationTime[2] + " "
												+ configLoad.getString("Command.Island.Level.Cooldown.Word.Minute")
												+ " " + durationTime[3] + " "
												+ configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
					} else if (cooldown.getTime() >= 60) {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
										durationTime[2] + " "
												+ configLoad.getString("Command.Island.Level.Cooldown.Word.Minute")
												+ " " + durationTime[3] + " "
												+ configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
										cooldown.getTime() + " "
												+ configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
					}

					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

					return;
				}

				messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Processing.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);

				cooldownManager.createPlayer(CooldownType.Levelling,
						Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
				levellingManager.calculatePoints(player, island);
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Loading.Message"));
				soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
				Levelling.getInstance().open(player);
			}
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "level";
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
		return new String[] { "levelling", "points" };
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
