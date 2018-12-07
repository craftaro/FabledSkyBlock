package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class SettingsCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public SettingsCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (islandManager.hasIsland(player)) {
			me.goodandevil.skyblock.island.Island island = islandManager
					.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());

			if (island.hasRole(IslandRole.Operator, player.getUniqueId())
					|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				if ((island.hasRole(IslandRole.Operator, player.getUniqueId())
						&& (island.getSetting(IslandRole.Operator, "Visitor").getStatus()
								|| island.getSetting(IslandRole.Operator, "Member").getStatus()))
						|| island.hasRole(IslandRole.Owner, player.getUniqueId())) {
					me.goodandevil.skyblock.menus.Settings.getInstance().open(player,
							me.goodandevil.skyblock.menus.Settings.Type.Categories, null, null);
					soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
				} else {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Settings.Permission.Default.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Role.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "settings";
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
		return new String[] { "permissions", "perms", "p" };
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
