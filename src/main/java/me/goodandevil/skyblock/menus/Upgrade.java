package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.island.IslandUpgradeEvent;
import me.goodandevil.skyblock.api.utils.APIUtil;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.upgrade.UpgradeManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Upgrade {

	private static Upgrade instance;

	public static Upgrade getInstance() {
		if (instance == null) {
			instance = new Upgrade();
		}

		return instance;
	}

	@SuppressWarnings("deprecation")
	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		EconomyManager economyManager = skyblock.getEconomyManager();
		MessageManager messageManager = skyblock.getMessageManager();
		UpgradeManager upgradeManager = skyblock.getUpgradeManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
				.getFileConfiguration();

		if (!economyManager.isEconomy()) {
			messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Disabled.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

			return;
		}

		if (playerDataManager.hasPlayerData(player) && playerDataManager.getPlayerData(player).getOwner() != null) {
			Island island = islandManager.getIsland(player);

			nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!economyManager.isEconomy()) {
						messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Disabled.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

						return;
					}

					if (playerDataManager.hasPlayerData(player)) {
						PlayerData playerData = playerDataManager.getPlayerData(player);

						if (playerData.getOwner() == null) {
							messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Owner.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						ItemStack is = event.getItem();

						if ((is.getType() == Material.POTION) && (is.hasItemMeta())) {
							if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")))) {
								if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed)) {
									if (island.isUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed)) {
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed,
												false);

										for (Player all : islandManager.getPlayersAtIsland(island)) {
											all.removePotionEffect(PotionEffectType.SPEED);
										}
									} else {
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed,
												true);
									}

									soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
											new Runnable() {
												@Override
												public void run() {
													open(player);
												}
											}, 1L);
								} else {
									List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
											.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed);

									if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
										me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

										if (economyManager.hasBalance(player, upgrade.getCost())) {
											messageManager.sendMessage(player,
													configLoad.getString("Island.Upgrade.Bought.Message")
															.replace("%upgrade", is.getItemMeta().getDisplayName()));
											soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

											economyManager.withdraw(player, upgrade.getCost());
											island.setUpgrade(player,
													me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed, true);

											Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
													new Runnable() {
														@Override
														public void run() {
															open(player);
														}
													}, 1L);
										} else {
											messageManager.sendMessage(player,
													configLoad.getString("Island.Upgrade.Money.Message"));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

											event.setWillClose(false);
											event.setWillDestroy(false);
										}
									} else {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Exist.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								}
							} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
									'&', configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")))) {
								if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump)) {
									if (island.isUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump)) {
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump,
												false);

										for (Player all : islandManager.getPlayersAtIsland(island)) {
											all.removePotionEffect(PotionEffectType.JUMP);
										}
									} else {
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump,
												true);
									}

									soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
											new Runnable() {
												@Override
												public void run() {
													open(player);
												}
											}, 1L);
								} else {
									List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
											.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump);

									if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
										me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

										if (economyManager.hasBalance(player, upgrade.getCost())) {
											messageManager.sendMessage(player,
													configLoad.getString("Island.Upgrade.Bought.Message")
															.replace("%upgrade", is.getItemMeta().getDisplayName()));
											soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

											economyManager.withdraw(player, upgrade.getCost());
											island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump,
													true);

											Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
													new Runnable() {
														@Override
														public void run() {
															open(player);
														}
													}, 1L);
										} else {
											messageManager.sendMessage(player,
													configLoad.getString("Island.Upgrade.Money.Message"));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

											event.setWillClose(false);
											event.setWillDestroy(false);
										}
									} else {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Exist.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								}
							}
						} else if ((is.getType() == Materials.WHEAT_SEEDS.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Crop.Displayname"))))) {
							if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop)) {
								if (island.isUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop)) {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop, false);
								} else {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop, true);
								}

								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 1L);
							} else {
								List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
										.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop);

								if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
									me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

									if (economyManager.hasBalance(player, upgrade.getCost())) {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Bought.Message")
														.replace("%upgrade", is.getItemMeta().getDisplayName()));
										soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

										economyManager.withdraw(player, upgrade.getCost());
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop,
												true);

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														open(player);
													}
												}, 1L);
									} else {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Money.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								} else {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Upgrade.Exist.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								}
							}
						} else if ((is.getType() == Material.FEATHER) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Fly.Displayname"))))) {
							if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly)) {
								if (island.isUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly)) {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly, false);

									for (Player all : islandManager.getPlayersAtIsland(island)) {
										if (all.getGameMode() != GameMode.CREATIVE) {
											all.setFlying(false);
											all.setAllowFlight(false);
										}
									}
								} else {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly, true);

									for (Player all : islandManager.getPlayersAtIsland(island)) {
										all.setAllowFlight(true);
										all.setFlying(true);
									}
								}

								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 1L);
							} else {
								List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
										.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly);

								if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
									me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

									if (economyManager.hasBalance(player, upgrade.getCost())) {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Bought.Message")
														.replace("%upgrade", is.getItemMeta().getDisplayName()));
										soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

										economyManager.withdraw(player, upgrade.getCost());
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly,
												true);

										for (Player all : islandManager.getPlayersAtIsland(island)) {
											all.setAllowFlight(true);
											all.setFlying(true);
										}

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														open(player);
													}
												}, 1L);
									} else {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Money.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								} else {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Upgrade.Exist.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								}
							}
						} else if ((is.getType() == Material.SPIDER_EYE) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Drops.Displayname"))))) {
							if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops)) {
								if (island.isUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops)) {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops,
											false);
								} else {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops, true);
								}

								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 1L);
							} else {
								List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
										.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops);

								if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
									me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

									if (economyManager.hasBalance(player, upgrade.getCost())) {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Bought.Message")
														.replace("%upgrade", is.getItemMeta().getDisplayName()));
										soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

										economyManager.withdraw(player, upgrade.getCost());
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops,
												true);

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														open(player);
													}
												}, 1L);
									} else {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Money.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								} else {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Upgrade.Exist.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								}
							}
						} else if ((is.getType() == Material.BEACON) && (is.hasItemMeta())) {
							List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
									.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);

							if (upgrades != null && upgrades.size() > 0) {
								for (int i = 0; i < upgrades.size(); i++) {
									me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
									int tier = i + 1;

									if (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&',
													configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
															.replace("%tier", "" + tier)))) {
										if (upgrade.getValue() > island.getSize()
												&& upgrade.getValue() != island.getSize()) {
											if (economyManager.hasBalance(player, upgrade.getCost())) {
												messageManager.sendMessage(player,
														configLoad.getString("Island.Upgrade.Bought.Message").replace(
																"%upgrade", is.getItemMeta().getDisplayName()));
												soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F,
														1.0F);

												economyManager.withdraw(player, upgrade.getCost());
												island.setSize(upgrade.getValue());

												Bukkit.getServer().getPluginManager().callEvent(new IslandUpgradeEvent(
														island.getAPIWrapper(), player, APIUtil.fromImplementation(
																me.goodandevil.skyblock.upgrade.Upgrade.Type.Size)));

												Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
														new Runnable() {
															@Override
															public void run() {
																open(player);
															}
														}, 1L);
											} else {
												messageManager.sendMessage(player,
														configLoad.getString("Island.Upgrade.Money.Message"));
												soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
														1.0F);

												event.setWillClose(false);
												event.setWillDestroy(false);
											}

											return;
										}
									}
								}

								messageManager.sendMessage(player,
										configLoad.getString("Island.Upgrade.Claimed.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								event.setWillClose(false);
								event.setWillDestroy(false);
							}
						} else if ((is.getType() == Materials.SPAWNER.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname"))))) {
							if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner)) {
								if (island.isUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner)) {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner,
											false);
								} else {
									island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner,
											true);
								}

								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 1L);
							} else {
								List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
										.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner);

								if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
									me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

									if (economyManager.hasBalance(player, upgrade.getCost())) {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Bought.Message")
														.replace("%upgrade", is.getItemMeta().getDisplayName()));
										soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);

										economyManager.withdraw(player, upgrade.getCost());
										island.setUpgrade(player, me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner,
												true);

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														open(player);
													}
												}, 1L);
									} else {
										messageManager.sendMessage(player,
												configLoad.getString("Island.Upgrade.Money.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									}
								} else {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Upgrade.Exist.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								}
							}
						}
					}
				}
			});

			List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades;

			ItemStack potion = new ItemStack(Material.POTION);
			int NMSVersion = NMSUtil.getVersionNumber();

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed);

			if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
				me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

				if (NMSVersion > 8) {
					PotionMeta pm = (PotionMeta) potion.getItemMeta();

					if (NMSVersion > 9) {
						pm.setBasePotionData(new PotionData(PotionType.SPEED));
					} else {
						pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0), true);
					}

					potion.setItemMeta(pm);
				} else {
					potion = new ItemStack(Material.POTION, 1, (short) 8194);
				}

				if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed)) {
					nInv.addItem(nInv.createItem(potion,
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
							configLoad.getStringList("Menu.Upgrade.Item.Speed.Claimed.Lore"),
							new Placeholder[] {
									new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
									new Placeholder("%status",
											getStatus(island, me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed)) },
							null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 0);
				} else {
					if (economyManager.hasBalance(player, upgrade.getCost())) {
						nInv.addItem(nInv.createItem(potion,
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Speed.Claimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 0);
					} else {
						nInv.addItem(nInv.createItem(potion,
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Speed.Unclaimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 0);
					}
				}
			}

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump);

			if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
				me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

				if (NMSVersion > 8) {
					potion = new ItemStack(Material.POTION);
					PotionMeta pm = (PotionMeta) potion.getItemMeta();

					if (NMSVersion > 9) {
						pm.setBasePotionData(new PotionData(PotionType.JUMP));
					} else {
						pm.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 1, 0), true);
					}

					potion.setItemMeta(pm);
				} else {
					potion = new ItemStack(Material.POTION, 1, (short) 8203);
				}

				if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump)) {
					nInv.addItem(nInv.createItem(potion,
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
							configLoad.getStringList("Menu.Upgrade.Item.Jump.Claimed.Lore"),
							new Placeholder[] {
									new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
									new Placeholder("%status",
											getStatus(island, me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump)) },
							null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 1);
				} else {
					if (economyManager.hasBalance(player, upgrade.getCost())) {
						nInv.addItem(nInv.createItem(potion,
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Jump.Claimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 1);
					} else {
						nInv.addItem(nInv.createItem(potion,
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Jump.Unclaimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 1);
					}
				}
			}

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop);

			if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
				me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

				if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop)) {
					nInv.addItem(nInv.createItem(Materials.WHEAT_SEEDS.parseItem(),
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
							configLoad.getStringList("Menu.Upgrade.Item.Crop.Claimed.Lore"),
							new Placeholder[] {
									new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
									new Placeholder("%status",
											getStatus(island, me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop)) },
							null, null), 3);
				} else {
					if (economyManager.hasBalance(player, upgrade.getCost())) {
						nInv.addItem(nInv.createItem(Materials.WHEAT_SEEDS.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Crop.Claimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 3);
					} else {
						nInv.addItem(nInv.createItem(Materials.WHEAT_SEEDS.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Crop.Unclaimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 3);
					}
				}
			}

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly);

			if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
				me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

				if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly)) {
					nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
							configLoad.getStringList("Menu.Upgrade.Item.Fly.Claimed.Lore"),
							new Placeholder[] {
									new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
									new Placeholder("%status",
											getStatus(island, me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly)) },
							null, null), 4);
				} else {
					if (economyManager.hasBalance(player, upgrade.getCost())) {
						nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Fly.Claimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 4);
					} else {
						nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Fly.Unclaimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 4);
					}
				}
			}

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops);

			if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
				me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

				if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops)) {
					nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
							configLoad.getStringList("Menu.Upgrade.Item.Drops.Claimed.Lore"),
							new Placeholder[] {
									new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
									new Placeholder("%status",
											getStatus(island, me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops)) },
							null, null), 5);
				} else {
					if (economyManager.hasBalance(player, upgrade.getCost())) {
						nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Drops.Claimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 5);
					} else {
						nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Drops.Unclaimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 5);
					}
				}
			}

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);

			if (upgrades != null && upgrades.size() > 0) {
				for (int i = 0; i < upgrades.size(); i++) {
					me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
					int tier = i + 1;

					if (tier != upgrades.size()) {
						if (upgrade.getValue() <= island.getSize()) {
							continue;
						}
					}

					if (island.getSize() >= upgrade.getValue()) {
						nInv.addItem(nInv.createItem(new ItemStack(Material.BEACON),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Size.Displayname").replace("%tier",
												"" + tier)),
								configLoad.getStringList("Menu.Upgrade.Item.Size.Claimed.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
										new Placeholder("%tier", "" + tier),
										new Placeholder("%size", "" + upgrade.getValue()) },
								null, null), 7);
					} else {
						if (economyManager.hasBalance(player, upgrade.getCost())) {
							nInv.addItem(
									nInv.createItem(new ItemStack(Material.BEACON),
											ChatColor.translateAlternateColorCodes('&',
													configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
															.replace("%tier", "" + tier)),
											configLoad.getStringList("Menu.Upgrade.Item.Size.Claimable.Lore"),
											new Placeholder[] {
													new Placeholder("%cost",
															NumberUtil.formatNumberByDecimal(upgrade.getCost())),
													new Placeholder("%tier", "" + tier),
													new Placeholder("%size", "" + upgrade.getValue()) },
											null, null),
									7);
						} else {
							nInv.addItem(
									nInv.createItem(new ItemStack(Material.BEACON),
											ChatColor.translateAlternateColorCodes('&',
													configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
															.replace("%tier", "" + tier)),
											configLoad.getStringList("Menu.Upgrade.Item.Size.Unclaimable.Lore"),
											new Placeholder[] {
													new Placeholder("%cost",
															NumberUtil.formatNumberByDecimal(upgrade.getCost())),
													new Placeholder("%tier", "" + tier),
													new Placeholder("%size", "" + upgrade.getValue()) },
											null, null),
									7);
						}
					}

					break;
				}
			}

			upgrades = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner);

			if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
				me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

				if (island.hasUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner)) {
					nInv.addItem(nInv.createItem(Materials.SPAWNER.parseItem(),
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
							configLoad.getStringList("Menu.Upgrade.Item.Spawner.Claimed.Lore"),
							new Placeholder[] {
									new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
									new Placeholder("%status",
											getStatus(island, me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner)) },
							null, null), 8);
				} else {
					if (economyManager.hasBalance(player, upgrade.getCost())) {
						nInv.addItem(nInv.createItem(Materials.SPAWNER.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Spawner.Claimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 8);
					} else {
						nInv.addItem(nInv.createItem(Materials.SPAWNER.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
								configLoad.getStringList("Menu.Upgrade.Item.Spawner.Unclaimable.Lore"),
								new Placeholder[] {
										new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
								null, null), 8);
					}
				}
			}

			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Upgrade.Title")));
			nInv.setRows(1);
			nInv.open();
		}
	}

	private String getStatus(Island island, me.goodandevil.skyblock.upgrade.Upgrade.Type type) {
		SkyBlock skyblock = SkyBlock.getInstance();
		FileConfiguration configLoad = skyblock.getFileManager()
				.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();
		String upgradeStatus;

		if (island.isUpgrade(type)) {
			upgradeStatus = configLoad.getString("Menu.Upgrade.Item.Word.Disable");
		} else {
			upgradeStatus = configLoad.getString("Menu.Upgrade.Item.Word.Enable");
		}

		return upgradeStatus;
	}
}
