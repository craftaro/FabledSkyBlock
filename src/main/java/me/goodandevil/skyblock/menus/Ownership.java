package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.AnvilGUI;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Ownership {

	private static Ownership instance;

	public static Ownership getInstance() {
		if (instance == null) {
			instance = new Ownership();
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
						PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(player);
						Island island = islandManager.getIsland(player);

						if (island == null) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Ownership.Owner.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							return;
						} else if (!island.hasRole(IslandRole.Owner, player.getUniqueId())) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Ownership.Role.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

							return;
						}

						if (playerData.getType() == null) {
							playerData.setType(Ownership.Visibility.Hidden);
						}

						ItemStack is = event.getItem();

						if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Ownership.Item.Exit.Displayname"))))) {
							soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
						} else if ((is.getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Ownership.Item.Original.Displayname"))))) {
							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);
						} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
								&& (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"))))) {
							soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);
						} else if ((is.getType() == Materials.WRITABLE_BOOK.parseMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Ownership.Item.Assign.Displayname"))))) {
							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
								@Override
								public void run() {
									AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {
										@Override
										public void onAnvilClick(final AnvilGUI.AnvilClickEvent event) {
											if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												if (playerDataManager.hasPlayerData(player)) {
													Island island = islandManager.getIsland(player);

													if (island == null) {
														messageManager.sendMessage(player, configLoad
																.getString("Command.Island.Ownership.Owner.Message"));
														soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(),
																1.0F, 1.0F);

														return;
													} else if (!island.hasRole(IslandRole.Owner,
															player.getUniqueId())) {
														messageManager.sendMessage(player, configLoad
																.getString("Command.Island.Ownership.Role.Message"));
														soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(),
																1.0F, 1.0F);

														return;
													}

													Bukkit.getScheduler().runTask(skyblock, new Runnable() {
														@Override
														public void run() {
															Bukkit.getServer().dispatchCommand(player,
																	"island ownership " + event.getName());
														}
													});
												}
											} else {
												event.setWillClose(false);
												event.setWillDestroy(false);
											}
										}
									});

									ItemStack is = new ItemStack(Material.NAME_TAG);
									ItemMeta im = is.getItemMeta();
									im.setDisplayName(configLoad.getString("Menu.Ownership.Item.Assign.Word.Enter"));
									is.setItemMeta(im);

									gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
									gui.open();
								}
							}, 1L);
						} else if ((is.getType() == Materials.LEGACY_EMPTY_MAP.getPostMaterial()) && (is.hasItemMeta())
								&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Ownership.Item.Password.Displayname"))))) {
							if (island.hasPassword()) {
								if (event.getClick() == ClickType.MIDDLE) {
									Ownership.Visibility visibility = (Ownership.Visibility) playerData.getType();

									if (visibility == Ownership.Visibility.Hidden) {
										playerData.setType(Ownership.Visibility.Visible);
									} else {
										playerData.setType(Ownership.Visibility.Hidden);
									}

									soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
											new Runnable() {
												@Override
												public void run() {
													open(player);
												}
											}, 1L);

									return;
								} else if (event.getClick() == ClickType.RIGHT) {
									island.setPassword(null);
									soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock,
											new Runnable() {
												@Override
												public void run() {
													open(player);
												}
											}, 1L);

									return;
								} else if (event.getClick() != ClickType.LEFT) {
									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}
							}

							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
								@Override
								public void run() {
									AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {
										@Override
										public void onAnvilClick(final AnvilGUI.AnvilClickEvent event) {
											if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												if (playerDataManager.hasPlayerData(player)) {
													Island island = islandManager.getIsland(player);

													if (island == null) {
														messageManager.sendMessage(player, configLoad
																.getString("Command.Island.Ownership.Owner.Message"));
														soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(),
																1.0F, 1.0F);

														return;
													} else if (!island.hasRole(IslandRole.Owner,
															player.getUniqueId())) {
														messageManager.sendMessage(player, configLoad
																.getString("Command.Island.Ownership.Role.Message"));
														soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(),
																1.0F, 1.0F);

														return;
													}

													island.setPassword(
															event.getName().replace("&", "").replace(" ", ""));
													soundManager.playSound(player, Sounds.ANVIL_USE.bukkitSound(), 1.0F,
															1.0F);

													Bukkit.getServer().getScheduler()
															.runTaskLaterAsynchronously(skyblock, new Runnable() {
																@Override
																public void run() {
																	open(player);
																}
															}, 1L);
												}
											} else {
												event.setWillClose(false);
												event.setWillDestroy(false);
											}
										}
									});

									ItemStack is = new ItemStack(Material.NAME_TAG);
									ItemMeta im = is.getItemMeta();
									im.setDisplayName(
											configLoad.getString("Menu.Ownership.Item.Password.Hidden.Word.Enter"));
									is.setItemMeta(im);

									gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
									gui.open();
								}
							}, 1L);
						}
					}
				}
			});

			Island island = skyblock.getIslandManager().getIsland(player);

			UUID originalOwnerUUID = island.getOriginalOwnerUUID();

			String originalOwnerName, ownershipPassword = island.getPassword();
			String[] playerTexture;

			Player targetPlayer = Bukkit.getServer().getPlayer(island.getOriginalOwnerUUID());

			if (targetPlayer == null) {
				OfflinePlayer offlinePlayer = new OfflinePlayer(originalOwnerUUID);
				originalOwnerName = offlinePlayer.getName();
				playerTexture = offlinePlayer.getTexture();
			} else {
				originalOwnerName = targetPlayer.getName();
				playerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
			}

			nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
					configLoad.getString("Menu.Ownership.Item.Exit.Displayname"), null, null, null, null), 0);
			nInv.addItem(nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]),
					configLoad.getString("Menu.Ownership.Item.Original.Displayname"),
					configLoad.getStringList("Menu.Ownership.Item.Original.Lore"),
					new Placeholder[] { new Placeholder("%player", originalOwnerName) }, null, null), 1);
			nInv.addItem(
					nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
							configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"), null, null, null, null),
					2);
			nInv.addItem(nInv.createItem(Materials.WRITABLE_BOOK.parseItem(),
					configLoad.getString("Menu.Ownership.Item.Assign.Displayname"),
					configLoad.getStringList("Menu.Ownership.Item.Assign.Lore"), null, null, null), 3);

			if (island.hasPassword()) {
				if (playerDataManager.getPlayerData(player).getType() == Ownership.Visibility.Hidden) {
					nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(),
							configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
							configLoad.getStringList("Menu.Ownership.Item.Password.Hidden.Lore"), null, null, null), 4);
				} else {
					nInv.addItem(
							nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(),
									configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
									configLoad.getStringList("Menu.Ownership.Item.Password.Visible.Lore"),
									new Placeholder[] { new Placeholder("%password", ownershipPassword) }, null, null),
							4);
				}
			} else {
				nInv.addItem(
						nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(),
								configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
								configLoad.getStringList("Menu.Ownership.Item.Password.Unset.Lore"), null, null, null),
						4);
			}

			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Title")));
			nInv.setType(InventoryType.HOPPER);

			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					nInv.open();
				}
			});
		}
	}

	public enum Visibility {

		Visible, Hidden;

	}
}
