package me.goodandevil.skyblock.menus.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.upgrade.UpgradeManager;
import me.goodandevil.skyblock.utils.AnvilGUI;
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
		MessageManager messageManager = skyblock.getMessageManager();
		UpgradeManager upgradeManager = skyblock.getUpgradeManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		if (playerDataManager.hasPlayerData(player) && playerDataManager.getPlayerData(player).getViewer() != null) {
			FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
					.getFileConfiguration();
			Viewer viewer = (Upgrade.Viewer) playerDataManager.getPlayerData(player).getViewer();

			if (viewer == null || viewer.getType() == Upgrade.Viewer.Type.Upgrades) {
				nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (!(player.hasPermission("skyblock.admin.upgrade") || player.hasPermission("skyblock.admin.*")
								|| player.hasPermission("skyblock.*"))) {
							messageManager.sendMessage(player,
									configLoad.getString("Island.Admin.Upgrade.Permission.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						ItemStack is = event.getItem();
						me.goodandevil.skyblock.upgrade.Upgrade upgrade = null;

						if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Exit.Displayname"))))) {
							soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);

							return;
						} else if ((is.getType() == Material.POTION) && (is.hasItemMeta())) {
							if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Speed.Displayname")))) {
								upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed)
										.get(0);
								viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed);
							} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
									'&', configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Jump.Displayname")))) {
								upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump)
										.get(0);
								viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump);
							}
						} else if ((is.getType() == Materials.WHEAT_SEEDS.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Crop.Displayname"))))) {
							upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop)
									.get(0);
							viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop);
						} else if ((is.getType() == Material.FEATHER) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Fly.Displayname"))))) {
							upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly)
									.get(0);
							viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly);
						} else if ((is.getType() == Material.SPIDER_EYE) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Drops.Displayname"))))) {
							upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops)
									.get(0);
							viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops);
						} else if ((is.getType() == Material.BEACON) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Size.Displayname"))))) {
							viewer.setType(Upgrade.Viewer.Type.Size);
							viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);

							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
								@Override
								public void run() {
									open(player);
								}
							}, 1L);
						} else if ((is.getType() == Materials.SPAWNER.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad
												.getString("Menu.Admin.Upgrade.Upgrades.Item.Spawner.Displayname"))))) {
							upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner)
									.get(0);
							viewer.setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner);
						}

						if (upgrade != null) {
							if (event.getClick() == ClickType.LEFT) {
								if (upgrade.isEnabled()) {
									upgrade.setEnabled(false);
								} else {
									upgrade.setEnabled(true);
								}

								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 1L);
							} else if (event.getClick() == ClickType.RIGHT) {
								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										AnvilGUI gui = new AnvilGUI(player, event1 -> {
											if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												if (!(player.hasPermission("skyblock.admin.upgrade")
														|| player.hasPermission("skyblock.admin.*")
														|| player.hasPermission("skyblock.*"))) {
													messageManager.sendMessage(player, configLoad
															.getString("Island.Admin.Upgrade.Permission.Message"));
													soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(),
															1.0F, 1.0F);

													return;
												}

												if (playerDataManager.hasPlayerData(player)
														&& playerDataManager.getPlayerData(player) != null) {
													if (!(event1.getName().matches("[0-9]+")
															|| event1.getName().matches("([0-9]*)\\.([0-9]{2}$)"))) {
														messageManager.sendMessage(player, configLoad
																.getString("Island.Admin.Upgrade.Numerical.Message"));
														soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(),
																1.0F, 1.0F);

														event1.setWillClose(false);
														event1.setWillDestroy(false);

														return;
													}

													double upgradeCost = Double.valueOf(event1.getName());
													me.goodandevil.skyblock.upgrade.Upgrade.Type upgradeType = ((Viewer) playerDataManager
															.getPlayerData(player).getViewer()).getUpgrade();

													me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgradeManager
															.getUpgrades(upgradeType).get(0);
													upgrade.setCost(upgradeCost);
													soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(),
															1.0F, 1.0F);

													Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock,
															new Runnable() {
																@Override
																public void run() {
																	Config config = fileManager.getConfig(new File(
																			skyblock.getDataFolder(), "upgrades.yml"));
																	FileConfiguration configLoad = config
																			.getFileConfiguration();

																	configLoad.set(
																			"Upgrades." + upgradeType.name() + ".Cost",
																			upgradeCost);

																	try {
																		configLoad.save(config.getFile());
																	} catch (IOException e) {
																		e.printStackTrace();
																	}
																}
															});

													Bukkit.getServer().getScheduler()
															.runTaskLaterAsynchronously(skyblock, new Runnable() {
																@Override
																public void run() {
																	open(player);
																}
															}, 1L);
												}

												event1.setWillClose(true);
												event1.setWillDestroy(true);
											} else {
												event1.setWillClose(false);
												event1.setWillDestroy(false);
											}
										});

										ItemStack is = new ItemStack(Material.NAME_TAG);
										ItemMeta im = is.getItemMeta();
										im.setDisplayName(
												configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Word.Enter"));
										is.setItemMeta(im);

										gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
										gui.open();
									}
								}, 1L);
							} else {
								event.setWillClose(false);
								event.setWillDestroy(false);
							}
						}
					}
				});

				ItemStack potion = new ItemStack(Material.POTION);
				me.goodandevil.skyblock.upgrade.Upgrade upgrade;

				int NMSVersion = NMSUtil.getVersionNumber();

				if (NMSVersion > 12) {
					PotionMeta pm = (PotionMeta) potion.getItemMeta();
					pm.setBasePotionData(new PotionData(PotionType.SPEED));
					potion.setItemMeta(pm);
				} else {
					potion = new ItemStack(Material.POTION, 1, (short) 8194);
				}

				upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Speed).get(0);
				nInv.addItem(nInv.createItem(potion,
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Speed.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Speed.Lore"),
						new Placeholder[] {
								new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
								new Placeholder("%status", getStatus(upgrade)) },
						null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 1);

				if (NMSVersion > 12) {
					PotionMeta pm = (PotionMeta) potion.getItemMeta();
					pm.setBasePotionData(new PotionData(PotionType.JUMP));
					potion.setItemMeta(pm);
				} else {
					potion = new ItemStack(Material.POTION, 1, (short) 8203);
				}

				upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Jump).get(0);
				nInv.addItem(nInv.createItem(potion,
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Jump.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Jump.Lore"),
						new Placeholder[] {
								new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
								new Placeholder("%status", getStatus(upgrade)) },
						null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 2);

				upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Crop).get(0);
				nInv.addItem(nInv.createItem(Materials.WHEAT_SEEDS.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Crop.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Crop.Lore"),
						new Placeholder[] {
								new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
								new Placeholder("%status", getStatus(upgrade)) },
						null, null), 3);

				upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Fly).get(0);
				nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Fly.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Fly.Lore"),
						new Placeholder[] {
								new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
								new Placeholder("%status", getStatus(upgrade)) },
						null, null), 4);

				upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Drops).get(0);
				nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Drops.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Drops.Lore"),
						new Placeholder[] {
								new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
								new Placeholder("%status", getStatus(upgrade)) },
						null, null), 5);

				List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
						.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);
				int upgradeTiers = 0;

				if (upgrades != null) {
					upgradeTiers = upgrades.size();
				}

				nInv.addItem(nInv.createItem(new ItemStack(Material.BEACON),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Size.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Size.Lore"),
						new Placeholder[] { new Placeholder("%tiers", "" + upgradeTiers) }, null, null), 6);

				upgrade = upgradeManager.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Spawner).get(0);
				nInv.addItem(nInv.createItem(Materials.SPAWNER.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Spawner.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Spawner.Lore"),
						new Placeholder[] {
								new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
								new Placeholder("%status", getStatus(upgrade)) },
						null, null), 7);

				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Exit.Displayname")),
						null, null, null, null), 0, 8);

				nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
						configLoad.getString("Menu.Admin.Upgrade.Upgrades.Title")));
				nInv.setRows(1);

				Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
					@Override
					public void run() {
						nInv.open();
					}
				});
			} else if (viewer.getType() == Upgrade.Viewer.Type.Size) {
				nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (!(player.hasPermission("skyblock.admin.upgrade") || player.hasPermission("skyblock.admin.*")
								|| player.hasPermission("skyblock.*"))) {
							messageManager.sendMessage(player,
									configLoad.getString("Island.Admin.Upgrade.Permission.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						if (playerDataManager.hasPlayerData(player)) {
							PlayerData playerData = playerDataManager.getPlayerData(player);
							ItemStack is = event.getItem();

							if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad
													.getString("Menu.Admin.Upgrade.Size.Item.Return.Displayname"))))) {
								playerData.setViewer(new Upgrade.Viewer(Upgrade.Viewer.Type.Upgrades, null));
								soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 1L);
							} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta()
									.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad
											.getString("Menu.Admin.Upgrade.Size.Item.Information.Displayname"))))) {
								List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
										.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);

								if (upgrades != null && upgrades.size() >= 5) {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Admin.Upgrade.Tier.Limit.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								} else {
									soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
											new Runnable() {
												@Override
												public void run() {
													AnvilGUI gui = new AnvilGUI(player, event1 -> {
														if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
															if (playerDataManager.hasPlayerData(player)
																	&& playerDataManager
																			.getPlayerData(player) != null) {
																if (!event1.getName().matches("[0-9]+")) {
																	messageManager.sendMessage(player,
																			configLoad.getString(
																					"Island.Admin.Upgrade.Numerical.Message"));
																	soundManager.playSound(player,
																			Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																			1.0F);

																	event1.setWillClose(false);
																	event1.setWillDestroy(false);

																	return;
																} else {
																	List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
																			.getUpgrades(
																					me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);

																	if (upgrades != null && upgrades.size() >= 5) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Tier.Limit.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		Bukkit.getServer().getScheduler()
																				.runTaskLaterAsynchronously(skyblock,
																						new Runnable() {
																							@Override
																							public void run() {
																								open(player);
																							}
																						}, 1L);

																		return;
																	}
																}

																int size = Integer.valueOf(event1.getName());

																if (size > 1000) {
																	messageManager.sendMessage(player,
																			configLoad.getString(
																					"Island.Admin.Upgrade.Tier.Size.Message"));
																	soundManager.playSound(player,
																			Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																			1.0F);

																	event.setWillClose(false);
																	event.setWillDestroy(false);

																	return;
																} else if (upgradeManager.hasUpgrade(
																		me.goodandevil.skyblock.upgrade.Upgrade.Type.Size,
																		size)) {
																	messageManager.sendMessage(player,
																			configLoad.getString(
																					"Island.Admin.Upgrade.Tier.Exist.Message"));
																	soundManager.playSound(player,
																			Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																			1.0F);

																	event1.setWillClose(false);
																	event1.setWillDestroy(false);

																	return;
																}

																soundManager.playSound(player,
																		Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
																upgradeManager.addUpgrade(
																		me.goodandevil.skyblock.upgrade.Upgrade.Type.Size,
																		size);

																Bukkit.getServer().getScheduler()
																		.runTaskLaterAsynchronously(skyblock,
																				new Runnable() {
																					@Override
																					public void run() {
																						open(player);
																					}
																				}, 1L);
															}

															event1.setWillClose(true);
															event1.setWillDestroy(true);
														} else {
															event1.setWillClose(false);
															event1.setWillDestroy(false);
														}
													});

													ItemStack is = new ItemStack(Material.NAME_TAG);
													ItemMeta im = is.getItemMeta();
													im.setDisplayName(configLoad
															.getString("Menu.Admin.Upgrade.Size.Item.Word.Size.Enter"));
													is.setItemMeta(im);

													gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
													gui.open();
												}
											}, 1L);
								}
							} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
									&& (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad
													.getString("Menu.Admin.Upgrade.Size.Item.Barrier.Displayname"))))) {
								soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

								event.setWillClose(false);
								event.setWillDestroy(false);
							} else if ((is.getType() == Material.PAPER) && (is.hasItemMeta())) {
								int slot = event.getSlot();
								int tier = slot - 3;

								me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgradeManager
										.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size).get(tier);

								if (upgrade != null) {
									if (event.getClick() == ClickType.LEFT) {
										soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														AnvilGUI gui = new AnvilGUI(player, event1 -> {
															if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
																if (!(player.hasPermission("skyblock.admin.upgrade")
																		|| player.hasPermission("skyblock.admin.*")
																		|| player.hasPermission("skyblock.*"))) {
																	messageManager.sendMessage(player,
																			configLoad.getString(
																					"Island.Admin.Upgrade.Permission.Message"));
																	soundManager.playSound(player,
																			Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																			1.0F);

																	return;
																}

																if (playerDataManager.hasPlayerData(player)
																		&& playerDataManager
																				.getPlayerData(player) != null) {
																	if (!event1.getName().matches("[0-9]+")) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Numerical.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		event1.setWillClose(false);
																		event1.setWillDestroy(false);

																		return;
																	} else if (upgradeManager.getUpgrades(
																			me.goodandevil.skyblock.upgrade.Upgrade.Type.Size)
																			.get(tier) == null) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Tier.Selected.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		Bukkit.getServer().getScheduler()
																				.runTaskLaterAsynchronously(skyblock,
																						new Runnable() {
																							@Override
																							public void run() {
																								open(player);
																							}
																						}, 1L);

																		return;
																	}

																	int size = Integer.valueOf(event1.getName());

																	if (size > 1000) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Tier.Size.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		event.setWillClose(false);
																		event.setWillDestroy(false);

																		return;
																	} else if (upgradeManager.hasUpgrade(
																			me.goodandevil.skyblock.upgrade.Upgrade.Type.Size,
																			size)) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Tier.Exist.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		event.setWillClose(false);
																		event.setWillDestroy(false);

																		return;
																	}

																	soundManager.playSound(player,
																			Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
																	upgradeManager.getUpgrades(
																			me.goodandevil.skyblock.upgrade.Upgrade.Type.Size)
																			.get(tier).setValue(size);
																	fileManager
																			.getConfig(
																					new File(skyblock.getDataFolder(),
																							"upgrades.yml"))
																			.getFileConfiguration()
																			.set("Upgrades.Size." + tier + ".Value",
																					size);

																	Bukkit.getServer().getScheduler()
																			.runTaskLaterAsynchronously(skyblock,
																					new Runnable() {
																						@Override
																						public void run() {
																							open(player);
																						}
																					}, 1L);
																}

																event1.setWillClose(true);
																event1.setWillDestroy(true);
															} else {
																event1.setWillClose(false);
																event1.setWillDestroy(false);
															}
														});

														ItemStack is = new ItemStack(Material.NAME_TAG);
														ItemMeta im = is.getItemMeta();
														im.setDisplayName(configLoad.getString(
																"Menu.Admin.Upgrade.Size.Item.Word.Size.Enter"));
														is.setItemMeta(im);

														gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
														gui.open();
													}
												}, 1L);

										return;
									} else if (event.getClick() == ClickType.MIDDLE) {
										soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
										upgradeManager.removeUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size,
												upgrade.getCost(), upgrade.getValue());
									} else if (event.getClick() == ClickType.RIGHT) {
										soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
												new Runnable() {
													@Override
													public void run() {
														AnvilGUI gui = new AnvilGUI(player, event1 -> {
															if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
																if (!(player.hasPermission("skyblock.admin.upgrade")
																		|| player.hasPermission("skyblock.admin.*")
																		|| player.hasPermission("skyblock.*"))) {
																	messageManager.sendMessage(player,
																			configLoad.getString(
																					"Island.Admin.Upgrade.Permission.Message"));
																	soundManager.playSound(player,
																			Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																			1.0F);

																	return;
																}

																if (playerDataManager.hasPlayerData(player)
																		&& playerDataManager
																				.getPlayerData(player) != null) {
																	if (!(event1.getName().matches("[0-9]+")
																			|| event1.getName().matches(
																					"([0-9]*)\\.([0-9]{2}$)"))) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Numerical.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		event1.setWillClose(false);
																		event1.setWillDestroy(false);

																		return;
																	} else if (upgradeManager.getUpgrades(
																			me.goodandevil.skyblock.upgrade.Upgrade.Type.Size)
																			.get(tier) == null) {
																		messageManager.sendMessage(player,
																				configLoad.getString(
																						"Island.Admin.Upgrade.Tier.Selected.Message"));
																		soundManager.playSound(player,
																				Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																				1.0F);

																		Bukkit.getServer().getScheduler()
																				.runTaskLaterAsynchronously(skyblock,
																						new Runnable() {
																							@Override
																							public void run() {
																								open(player);
																							}
																						}, 1L);

																		return;
																	}

																	double cost = Double.valueOf(event1.getName());

																	soundManager.playSound(player,
																			Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
																	upgradeManager.getUpgrades(
																			me.goodandevil.skyblock.upgrade.Upgrade.Type.Size)
																			.get(tier).setCost(cost);
																	fileManager
																			.getConfig(
																					new File(skyblock.getDataFolder(),
																							"upgrades.yml"))
																			.getFileConfiguration()
																			.set("Upgrades.Size." + tier + ".Cost",
																					cost);

																	Bukkit.getServer().getScheduler()
																			.runTaskLaterAsynchronously(skyblock,
																					new Runnable() {
																						@Override
																						public void run() {
																							open(player);
																						}
																					}, 1L);
																}

																event1.setWillClose(true);
																event1.setWillDestroy(true);
															} else {
																event1.setWillClose(false);
																event1.setWillDestroy(false);
															}
														});

														ItemStack is = new ItemStack(Material.NAME_TAG);
														ItemMeta im = is.getItemMeta();
														im.setDisplayName(configLoad.getString(
																"Menu.Admin.Upgrade.Size.Item.Word.Cost.Enter"));
														is.setItemMeta(im);

														gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
														gui.open();
													}
												}, 1L);

										return;
									} else {
										event.setWillClose(false);
										event.setWillDestroy(false);

										return;
									}
								}

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

				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Size.Item.Return.Displayname")),
						null, null, null, null), 0);
				nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Size.Item.Information.Displayname")),
						configLoad.getStringList("Menu.Admin.Upgrade.Size.Item.Information.Lore"), null, null, null),
						1);
				nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Admin.Upgrade.Size.Item.Barrier.Displayname")),
						null, null, null, null), 2);

				List<me.goodandevil.skyblock.upgrade.Upgrade> upgrades = upgradeManager
						.getUpgrades(me.goodandevil.skyblock.upgrade.Upgrade.Type.Size);

				if (upgrades != null) {
					for (int i = 0; i < 5; i++) {
						if (upgrades.size() >= i + 1) {
							me.goodandevil.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
							int tier = i + 1;

							if (upgrade != null) {
								nInv.addItem(nInv.createItem(new ItemStack(Material.PAPER, tier),
										ChatColor.translateAlternateColorCodes('&',
												configLoad.getString("Menu.Admin.Upgrade.Size.Item.Tier.Displayname")
														.replace("%tier", "" + tier)),
										configLoad.getStringList("Menu.Admin.Upgrade.Size.Item.Tier.Lore"),
										new Placeholder[] { new Placeholder("%size", "" + upgrade.getValue()),
												new Placeholder("%cost",
														NumberUtil.formatNumberByDecimal(upgrade.getCost())) },
										null, null), i + 3);
							}
						}
					}
				}

				nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
						configLoad.getString("Menu.Admin.Upgrade.Size.Title")));
				nInv.setRows(1);

				Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
					@Override
					public void run() {
						nInv.open();
					}
				});
			}
		}
	}

	private String getStatus(me.goodandevil.skyblock.upgrade.Upgrade upgrade) {
		SkyBlock skyblock = SkyBlock.getInstance();
		FileConfiguration configLoad = skyblock.getFileManager()
				.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();

		if (upgrade.isEnabled()) {
			return configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Word.Disable");
		} else {
			return configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Word.Enable");
		}
	}

	public static class Viewer {

		private Type type;
		private me.goodandevil.skyblock.upgrade.Upgrade.Type upgrade;

		public Viewer(Type type, me.goodandevil.skyblock.upgrade.Upgrade.Type upgrade) {
			this.type = type;
			this.upgrade = upgrade;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public me.goodandevil.skyblock.upgrade.Upgrade.Type getUpgrade() {
			return upgrade;
		}

		public void setUpgrade(me.goodandevil.skyblock.upgrade.Upgrade.Type upgrade) {
			this.upgrade = upgrade;
		}

		public enum Type {

			Upgrades, Size;

		}
	}
}
