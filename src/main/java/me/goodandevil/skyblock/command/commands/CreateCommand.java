package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.List;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.cooldown.Cooldown;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownPlayer;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.menus.Creator;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class CreateCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public CreateCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		CooldownManager cooldownManager = skyblock.getCooldownManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (islandManager.hasIsland(player)) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
			soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
		} else {
			Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));

			if (mainConfig.getFileConfiguration().getBoolean("Island.Creation.Menu.Enable")) {
				Creator.getInstance().open(player);
				soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
			} else {
				List<Structure> structures = skyblock.getStructureManager().getStructures();

				if (structures.size() == 0) {
					messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.None.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (!fileManager
						.isFileExist(new File(new File(skyblock.getDataFolder().toString() + "/structures"),
								structures.get(0).getOverworldFile()))) {
					messageManager.sendMessage(player,
							configLoad.getString("Island.Creator.Selector.File.Overworld.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (!fileManager
						.isFileExist(new File(new File(skyblock.getDataFolder().toString() + "/structures"),
								structures.get(0).getNetherFile()))) {
					messageManager.sendMessage(player,
							configLoad.getString("Island.Creator.Selector.File.Nether.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
						&& cooldownManager.hasPlayer(CooldownType.Creation, player)) {
					CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Creation, player);
					Cooldown cooldown = cooldownPlayer.getCooldown();

					if (cooldown.getTime() < 60) {
						messageManager.sendMessage(player,
								config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message")
										.replace("%time", cooldown.getTime() + " " + config.getFileConfiguration()
												.getString("Island.Creator.Selector.Cooldown.Word.Second")));
					} else {
						long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
						messageManager.sendMessage(player,
								config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message")
										.replace("%time", durationTime[2] + " "
												+ config.getFileConfiguration()
														.getString("Island.Creator.Selector.Cooldown.Word.Minute")
												+ " " + durationTime[3] + " " + config.getFileConfiguration()
														.getString("Island.Creator.Selector.Cooldown.Word.Second")));
					}

					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

					return;
				}

				if (islandManager.createIsland(player, structures.get(0))) {
					messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
					soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
				}
			}
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
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
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
