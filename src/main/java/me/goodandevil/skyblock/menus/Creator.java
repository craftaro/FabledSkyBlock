package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.cooldown.Cooldown;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownPlayer;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Creator {

	private static Creator instance;

	public static Creator getInstance() {
		if (instance == null) {
			instance = new Creator();
		}

		return instance;
	}

	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();

		CooldownManager cooldownManager = skyblock.getCooldownManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		List<Structure> availableStructures = new ArrayList<>();

		for (Structure structureList : skyblock.getStructureManager().getStructures()) {
			if (structureList.getDisplayname() == null || structureList.getDisplayname().isEmpty()
					|| structureList.getOverworldFile() == null || structureList.getOverworldFile().isEmpty()
					|| structureList.getNetherFile() == null || structureList.getNetherFile().isEmpty()) {
				continue;
			}

			if (structureList.isPermission()) {
				if (!player.hasPermission(structureList.getPermission()) && !player.hasPermission("skyblock.island.*")
						&& !player.hasPermission("skyblock.*")) {
					continue;
				}
			}

			availableStructures.add(structureList);
		}

		int inventoryRows = 0;

		if (availableStructures.size() == 0) {
			skyblock.getMessageManager().sendMessage(player,
					configLoad.getString("Island.Creator.Selector.None.Message"));
			skyblock.getSoundManager().playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

			return;
		} else if (availableStructures.size() <= 9) {
			inventoryRows = 1;
		} else if (availableStructures.size() <= 18) {
			inventoryRows = 2;
		} else if (availableStructures.size() <= 27) {
			inventoryRows = 3;
		} else if (availableStructures.size() <= 36) {
			inventoryRows = 4;
		} else if (availableStructures.size() <= 45) {
			inventoryRows = 5;
		} else if (availableStructures.size() <= 54) {
			inventoryRows = 6;
		}

		nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (islandManager.hasIsland(player)) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

					return;
				}

				Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, new Runnable() {
					@Override
					public void run() {
						ItemStack is = event.getItem();

						for (Structure structureList : skyblock.getStructureManager().getStructures()) {
							if ((is.getType() == structureList.getMaterials().parseMaterial()) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad
													.getString("Menu.Creator.Selector.Item.Island.Displayname")
													.replace("%displayname", structureList.getDisplayname()))))) {
								if (structureList.isPermission() && structureList.getPermission() != null
										&& !structureList.getPermission().isEmpty()) {
									if (!player.hasPermission(structureList.getPermission())
											&& !player.hasPermission("skyblock.island.*")
											&& !player.hasPermission("skyblock.*")) {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Creator.Selector.Permission.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														open(player);
													}
												}, 1L);

										return;
									}
								}

								if (!fileManager.isFileExist(
										new File(new File(skyblock.getDataFolder().toString() + "/structures"),
												structureList.getOverworldFile()))) {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Creator.Selector.File.Overworld.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								} else if (!fileManager.isFileExist(
										new File(new File(skyblock.getDataFolder().toString() + "/structures"),
												structureList.getNetherFile()))) {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Creator.Selector.File.Nether.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								} else if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
										.getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
										&& cooldownManager.hasPlayer(CooldownType.Creation, player)) {
									CooldownPlayer cooldownPlayer = cooldownManager
											.getCooldownPlayer(CooldownType.Creation, player);
									Cooldown cooldown = cooldownPlayer.getCooldown();

									if (cooldown.getTime() < 60) {
										messageManager.sendMessage(player, config.getFileConfiguration()
												.getString("Island.Creator.Selector.Cooldown.Message")
												.replace("%time", cooldown.getTime() + " "
														+ config.getFileConfiguration().getString(
																"Island.Creator.Selector.Cooldown.Word.Second")));
									} else {
										long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
										messageManager.sendMessage(player, config.getFileConfiguration()
												.getString("Island.Creator.Selector.Cooldown.Message")
												.replace("%time", durationTime[2] + " "
														+ config.getFileConfiguration().getString(
																"Island.Creator.Selector.Cooldown.Word.Minute")
														+ " " + durationTime[3] + " "
														+ config.getFileConfiguration().getString(
																"Island.Creator.Selector.Cooldown.Word.Second")));
									}

									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}

								if (islandManager.createIsland(player, structureList)) {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Creator.Selector.Created.Message"));
									soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
								}

								return;
							}
						}
					}
				});
			}
		});

		for (int i = 0; i < availableStructures.size(); i++) {
			Structure structure = availableStructures.get(i);
			List<String> itemLore = new ArrayList<>();

			for (String itemLoreList : configLoad.getStringList("Menu.Creator.Selector.Item.Island.Lore")) {
				if (itemLoreList.contains("%description")) {
					if (structure.getDescription() == null || structure.getDescription().isEmpty()) {
						itemLore.add(configLoad.getString("Menu.Creator.Selector.Item.Island.Word.Empty"));
					} else {
						for (String descriptionList : structure.getDescription()) {
							itemLore.add(ChatColor.translateAlternateColorCodes('&', descriptionList));
						}
					}
				} else {
					itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
				}
			}

			nInv.addItem(nInv.createItem(structure.getMaterials().parseItem(),
					ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Creator.Selector.Item.Island.Displayname")
									.replace("%displayname", structure.getDisplayname())),
					itemLore, null, null, null), i);
		}

		nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Creator.Selector.Title")));
		nInv.setRows(inventoryRows);

		Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
			@Override
			public void run() {
				nInv.open();
			}
		});
	}
}
