package me.goodandevil.skyblock.menus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Weather {

	private static Weather instance;

	public static Weather getInstance() {
		if (instance == null) {
			instance = new Weather();
		}

		return instance;
	}

	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		if (playerDataManager.hasPlayerData(player)) {
			FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
					.getFileConfiguration();

			nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (playerDataManager.hasPlayerData(player)) {
						Island island = islandManager.getIsland(player);

						if (island == null) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Weather.Owner.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							player.closeInventory();

							return;
						} else if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
								&& island.getSetting(IslandRole.Operator, "Biome").getStatus())
								|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Weather.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							player.closeInventory();

							return;
						}

						ItemStack is = event.getItem();

						if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Weather.Item.Info.Displayname"))))) {
							soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);
						} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
								&& (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Weather.Item.Barrier.Displayname"))))) {
							soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);
						} else if ((is.getType() == Materials.SUNFLOWER.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Weather.Item.Time.Displayname"))))) {
							int islandTime = island.getTime();

							if (islandTime == 0) {
								island.setTime(1000);
							} else if (islandTime == 1000) {
								island.setTime(6000);
							} else if (islandTime == 6000) {
								island.setTime(12000);
							} else if (islandTime == 12000) {
								island.setTime(13000);
							} else if (islandTime == 13000) {
								island.setTime(18000);
							} else if (islandTime == 18000) {
								island.setTime(0);
							}

							if (!island.isWeatherSynchronized()) {
								for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
									all.setPlayerTime(island.getTime(),
											fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
													.getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
								}
							}

							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
								@Override
								public void run() {
									open(player);
								}
							}, 1L);
						} else if ((is.getType() == Material.GHAST_TEAR) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Weather.Item.Weather.Displayname"))))) {
							if (island.getWeather() == WeatherType.DOWNFALL) {
								island.setWeather(WeatherType.CLEAR);
							} else {
								island.setWeather(WeatherType.DOWNFALL);
							}

							if (!island.isWeatherSynchronized()) {
								for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
									all.setPlayerWeather(island.getWeather());
								}
							}

							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
								@Override
								public void run() {
									open(player);
								}
							}, 1L);
						} else if ((is.getType() == Material.TRIPWIRE_HOOK) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Weather.Item.Synchronised.Displayname"))))) {
							if (island.isWeatherSynchronized()) {
								island.setWeatherSynchronized(false);

								int islandTime = island.getTime();
								WeatherType islandWeather = island.getWeather();

								for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
									all.setPlayerTime(islandTime,
											fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
													.getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
									all.setPlayerWeather(islandWeather);
								}
							} else {
								island.setWeatherSynchronized(true);

								for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
									all.resetPlayerTime();
									all.resetPlayerWeather();
								}
							}

							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
								@Override
								public void run() {
									open(player);
								}
							}, 1L);
						}
					}
				}
			});

			Island island = islandManager.getIsland(player);

			String timeName = "", timeChoice = "", weatherSynchronised, weatherChoice, synchronisedChoice;
			int islandTime = island.getTime();

			if (island.isWeatherSynchronized()) {
				weatherSynchronised = configLoad.getString("Menu.Weather.Item.Info.Synchronised.Enabled");
			} else {
				weatherSynchronised = configLoad.getString("Menu.Weather.Item.Info.Synchronised.Disabled");
			}

			if (islandTime == 0) {
				timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Dawn");
				timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Day");
			} else if (islandTime == 1000) {
				timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Day");
				timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Noon");
			} else if (islandTime == 6000) {
				timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Noon");
				timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Dusk");
			} else if (islandTime == 12000) {
				timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Dusk");
				timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Night");
			} else if (islandTime == 13000) {
				timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Night");
				timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Midnight");
			} else if (islandTime == 18000) {
				timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Midnight");
				timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Dawn");
			}

			if (island.getWeather() == WeatherType.CLEAR) {
				weatherChoice = configLoad.getString("Menu.Weather.Item.Weather.Choice.Downfall");
			} else {
				weatherChoice = configLoad.getString("Menu.Weather.Item.Weather.Choice.Clear");
			}

			if (island.isWeatherSynchronized()) {
				synchronisedChoice = configLoad.getString("Menu.Weather.Item.Synchronised.Choice.Disable");
			} else {
				synchronisedChoice = configLoad.getString("Menu.Weather.Item.Synchronised.Choice.Enable");
			}

			nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
					configLoad.getString("Menu.Weather.Item.Info.Displayname"),
					configLoad.getStringList("Menu.Weather.Item.Info.Lore"),
					new Placeholder[] { new Placeholder("%synchronised", weatherSynchronised),
							new Placeholder("%time_name", timeName), new Placeholder("%time", "" + island.getTime()),
							new Placeholder("%weather", island.getWeatherName()) },
					null, null), 0);
			nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
					configLoad.getString("Menu.Weather.Item.Barrier.Displayname"), null, null, null, null), 1);
			nInv.addItem(nInv.createItem(Materials.SUNFLOWER.parseItem(),
					configLoad.getString("Menu.Weather.Item.Time.Displayname"),
					configLoad.getStringList("Menu.Weather.Item.Time.Lore"),
					new Placeholder[] { new Placeholder("%choice", timeChoice) }, null, null), 2);
			nInv.addItem(nInv.createItem(new ItemStack(Material.GHAST_TEAR),
					configLoad.getString("Menu.Weather.Item.Weather.Displayname"),
					configLoad.getStringList("Menu.Weather.Item.Weather.Lore"),
					new Placeholder[] { new Placeholder("%choice", weatherChoice) }, null, null), 3);
			nInv.addItem(nInv.createItem(new ItemStack(Material.TRIPWIRE_HOOK),
					configLoad.getString("Menu.Weather.Item.Synchronised.Displayname"),
					configLoad.getStringList("Menu.Weather.Item.Synchronised.Lore"),
					new Placeholder[] { new Placeholder("%choice", synchronisedChoice) }, null, null), 4);

			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Title")));
			nInv.setType(InventoryType.HOPPER);

			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					nInv.open();
				}
			});
		}
	}
}
