package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.creation.Creation;
import me.goodandevil.skyblock.creation.CreationManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.menus.Creator;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class CreateCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public CreateCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		CreationManager creationManager = plugin.getCreationManager();
		MessageManager messageManager = plugin.getMessageManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (islandManager.hasIsland(player)) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				} else {
					Config mainConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
					
					if (mainConfig.getFileConfiguration().getBoolean("Island.Creation.Menu.Enable")) {
						Creator.getInstance().open(player);
						soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
					} else {
						List<Structure> structures = plugin.getStructureManager().getStructures();
						
						if (structures.size() == 0) {
							messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.None.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							
							return;
						} else if (!fileManager.isFileExist(new File(new File(plugin.getDataFolder().toString() + "/structures"), structures.get(0).getFile()))) {
							messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.File.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							
							return;
						} else if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable") && creationManager.hasPlayer(player)) {
							Creation creation = creationManager.getPlayer(player);
							
							if (creation.getTime() < 60) {
								messageManager.sendMessage(player, config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message").replace("%time", creation.getTime() + " " + config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Word.Second")));
							} else {
								long[] durationTime = NumberUtil.getDuration(creation.getTime());
								messageManager.sendMessage(player, config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message").replace("%time", durationTime[2] + " " + config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Word.Minute") + " " + durationTime[3] + " " + config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Word.Second")));
							}
							
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
						
						islandManager.createIsland(player, structures.get(0));
						
						messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					}
				}
			}
		});
	}

	@Override
	public String getName() {
		return "create";
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
		return new String[] { "new" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
