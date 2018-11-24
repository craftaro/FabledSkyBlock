package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class SettingsCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public SettingsCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		MessageManager messageManager = plugin.getMessageManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (islandManager.hasIsland(player)) {
			me.goodandevil.skyblock.island.Island island = islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
			
			if (island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId())) {
				if ((island.isRole(Role.Operator, player.getUniqueId()) && (island.getSetting(Settings.Role.Operator, "Visitor").getStatus() || island.getSetting(Settings.Role.Operator, "Member").getStatus())) || island.isRole(Role.Owner, player.getUniqueId())) {
					me.goodandevil.skyblock.menus.Settings.getInstance().open(player, me.goodandevil.skyblock.menus.Settings.Type.Categories, null, null);
					soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Permission.Default.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
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
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
