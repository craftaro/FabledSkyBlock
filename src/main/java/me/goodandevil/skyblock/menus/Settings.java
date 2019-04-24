package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandMessage;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandSetting;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.AbstractAnvilGUI;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.Visit;

public class Settings {

	private static Settings instance;

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}

		return instance;
	}

	public void open(Player player, Settings.Type menuType, IslandRole role, Settings.Panel panel) {
		SkyBlock skyblock = SkyBlock.getInstance();

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		if (playerDataManager.hasPlayerData(player)) {
			Island island = islandManager.getIsland(player);

			Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
			FileConfiguration configLoad = skyblock.getFileManager()
					.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();

			if (menuType == Settings.Type.Categories) {
				nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (playerDataManager.hasPlayerData(player)) {
							Island island = islandManager.getIsland(player);

							if (island == null) {
								messageManager.sendMessage(player,
										configLoad.getString("Command.Island.Settings.Owner.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								return;
							} else if (!(island.hasRole(IslandRole.Operator, player.getUniqueId())
									|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Role.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								return;
							}

							ItemStack is = event.getItem();

							if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
											'&',
											configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))))) {
								soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
							} else if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
											'&',
											configLoad.getString("Menu.Settings.Categories.Item.Coop.Displayname"))))) {
								if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
										.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Coop.Disabled.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}

								if (island.hasRole(IslandRole.Operator, player.getUniqueId())
										&& !island.getSetting(IslandRole.Operator, "Coop").getStatus()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}

								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Coop, null), 1L);
							} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&', configLoad
											.getString("Menu.Settings.Categories.Item.Visitor.Displayname"))))) {
								if (island.hasRole(IslandRole.Operator, player.getUniqueId())
										&& !island.getSetting(IslandRole.Operator, "Visitor").getStatus()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}

								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Visitor, null), 1L);
							} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad
													.getString("Menu.Settings.Categories.Item.Member.Displayname"))))) {
								if (island.hasRole(IslandRole.Operator, player.getUniqueId())
										&& !island.getSetting(IslandRole.Operator, "Member").getStatus()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}

								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Member, null), 1L);
							} else if ((is.getType() == Material.ITEM_FRAME) && (is.hasItemMeta()) && (is.getItemMeta()
									.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad
											.getString("Menu.Settings.Categories.Item.Operator.Displayname"))))) {
								if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}

								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Operator, null), 1L);
							} else if ((is.getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad
													.getString("Menu.Settings.Categories.Item.Owner.Displayname"))))) {
								if (island.hasRole(IslandRole.Operator, player.getUniqueId())
										&& !island.getSetting(IslandRole.Operator, "Island").getStatus()) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);

									return;
								}

								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Owner, null), 1L);
							}
						}
					}
				});

				nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial()),
						configLoad.getString("Menu.Settings.Categories.Item.Visitor.Displayname"),
						configLoad.getStringList("Menu.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
				nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
						configLoad.getString("Menu.Settings.Categories.Item.Member.Displayname"),
						configLoad.getStringList("Menu.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
				nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME),
						configLoad.getString("Menu.Settings.Categories.Item.Operator.Displayname"),
						configLoad.getStringList("Menu.Settings.Categories.Item.Operator.Lore"), null, null, null), 4);

				if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
						.getBoolean("Island.Coop.Enable")) {
					nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
							configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"), null, null, null,
							null), 0);
					nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
							configLoad.getString("Menu.Settings.Categories.Item.Coop.Displayname"),
							configLoad.getStringList("Menu.Settings.Categories.Item.Coop.Lore"), null, null, null), 6);
					nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(),
							configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"),
							configLoad.getStringList("Menu.Settings.Categories.Item.Owner.Lore"), null, null, null), 7);
				} else {
					nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
							configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"), null, null, null,
							null), 0, 9);
					nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(),
							configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"),
							configLoad.getStringList("Menu.Settings.Categories.Item.Owner.Lore"), null, null, null), 6);
				}

				nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
						configLoad.getString("Menu.Settings.Categories.Title")));
				nInv.setRows(1);

				Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
					@Override
					public void run() {
						nInv.open();
					}
				});
			} else if (menuType == Settings.Type.Role && role != null) {
				nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (playerDataManager.hasPlayerData(player)) {
							Island island = islandManager.getIsland(player);

							if (island == null) {
								messageManager.sendMessage(player,
										configLoad.getString("Command.Island.Settings.Owner.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								return;
							} else if (!(island.hasRole(IslandRole.Operator, player.getUniqueId())
									|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
								messageManager.sendMessage(player,
										configLoad.getString("Command.Island.Settings.Role.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								return;
							} else if (island.hasRole(IslandRole.Operator, player.getUniqueId())
									&& !island.getSetting(IslandRole.Operator, role.name()).getStatus()) {
								messageManager.sendMessage(player,
										configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

								return;
							} else if (role == IslandRole.Coop) {
								if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
										.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Coop.Disabled.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}
							}

							ItemStack is = event.getItem();

							if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
													"Menu.Settings." + role.name() + ".Item.Return.Displayname"))))) {
								soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Categories, null, null), 1L);
							} else if ((is.getType() == Material.PAPER) && (is.hasItemMeta())
									&& (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&', configLoad
													.getString("Menu.Settings.Visitor.Item.Signature.Displayname"))))) {
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Panel, null, Panel.Signature), 1L);
							} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"))))) {
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Panel, null, Panel.Welcome), 1L);
							} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta()
									.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad
											.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
								if (island.isOpen()) {
									islandManager.closeIsland(island);
									soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
								} else {
									island.setOpen(true);
									soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
								}

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Visitor, null), 1L);
							} else if (is.hasItemMeta()) {
								String roleName = getRoleName(role);

								for (IslandSetting settingList : island.getSettings(role)) {
									if (is.getItemMeta().getDisplayName()
											.equals(ChatColor.translateAlternateColorCodes('&',
													configLoad.getString("Menu.Settings." + roleName + ".Item.Setting."
															+ settingList.getName() + ".Displayname")))) {
										if (!hasPermission(island, player, role)) {
											messageManager.sendMessage(player, configLoad
													.getString("Command.Island.Settings.Permission.Change.Message"));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

											return;
										}

										if (settingList != null) {
											if (settingList.getStatus()) {
												settingList.setStatus(false);
											} else {
												settingList.setStatus(true);
											}

											if (settingList.getName().equals("KeepItemsOnDeath")
													|| settingList.getName().equals("PvP")
													|| settingList.getName().equals("Damage")) {
												island.getVisit()
														.setSafeLevel(islandManager.getIslandSafeLevel(island));
											}
										}

										break;
									}
								}

								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

								Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, role, null), 1L);
							}
						}
					}
				});

				if (role == IslandRole.Visitor || role == IslandRole.Member || role == IslandRole.Coop) {
					if (role == IslandRole.Visitor) {
						Config config = skyblock.getFileManager()
								.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
						Visit visit = island.getVisit();

						if (config.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
							nInv.addItem(nInv.createItem(new ItemStack(Material.PAPER),
									configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"),
									configLoad.getStringList("Menu.Settings.Visitor.Item.Signature.Lore"), null, null,
									null), 3);
						}

						if (config.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
							if (visit.isOpen()) {
								nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
										configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
										configLoad.getStringList(
												"Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Open.Lore"),
										new Placeholder[] { new Placeholder("%visits", "" + visit.getVisitors().size()),
												new Placeholder("%votes", "" + visit.getVoters().size()),
												new Placeholder("%visitors",
														"" + islandManager.getVisitorsAtIsland(island).size()) },
										null, null), 4);
							} else {
								nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
										configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
										configLoad.getStringList(
												"Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Closed.Lore"),
										new Placeholder[] { new Placeholder("%visits", "" + visit.getVisitors().size()),
												new Placeholder("%votes", "" + visit.getVoters().size()),
												new Placeholder("%visitors",
														"" + islandManager.getVisitorsAtIsland(island).size()) },
										null, null), 4);
							}
						} else {
							if (visit.isOpen()) {
								nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
										configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
										configLoad.getStringList(
												"Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Open.Lore"),
										new Placeholder[] { new Placeholder("%visits", "" + visit.getVisitors().size()),
												new Placeholder("%visitors",
														"" + islandManager.getVisitorsAtIsland(island).size()) },
										null, null), 4);
							} else {
								nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
										configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"),
										configLoad.getStringList(
												"Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Closed.Lore"),
										new Placeholder[] { new Placeholder("%visits", "" + visit.getVisitors().size()),
												new Placeholder("%visitors",
														"" + islandManager.getVisitorsAtIsland(island).size()) },
										null, null), 4);
							}
						}

						if (config.getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
							nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(),
									configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"),
									configLoad.getStringList("Menu.Settings.Visitor.Item.Welcome.Lore"), null, null,
									null), 5);
						}
					}

					nInv.addItemStack(createItem(island, role, "Destroy", new ItemStack(Material.DIAMOND_PICKAXE)), 9);
					nInv.addItemStack(createItem(island, role, "Place", new ItemStack(Material.GRASS)), 10);
					nInv.addItemStack(createItem(island, role, "Anvil", new ItemStack(Material.ANVIL)), 11);
					nInv.addItemStack(createItem(island, role, "ArmorStandUse", new ItemStack(Material.ARMOR_STAND)),
							12);
					nInv.addItemStack(createItem(island, role, "Beacon", new ItemStack(Material.BEACON)), 13);
					nInv.addItemStack(createItem(island, role, "Bed", Materials.WHITE_BED.parseItem()), 14);
					nInv.addItemStack(createItem(island, role, "AnimalBreeding", new ItemStack(Material.WHEAT)), 15);
					nInv.addItemStack(createItem(island, role, "Brewing",
							new ItemStack(Materials.LEGACY_BREWING_STAND.getPostMaterial())), 16);
					nInv.addItemStack(createItem(island, role, "Bucket", new ItemStack(Material.BUCKET)), 17);
					nInv.addItemStack(createItem(island, role, "WaterCollection", new ItemStack(Material.POTION)), 18);
					nInv.addItemStack(createItem(island, role, "Storage", new ItemStack(Material.CHEST)), 19);
					nInv.addItemStack(createItem(island, role, "Workbench", Materials.CRAFTING_TABLE.parseItem()), 20);
					nInv.addItemStack(createItem(island, role, "Crop", Materials.WHEAT_SEEDS.parseItem()), 21);
					nInv.addItemStack(createItem(island, role, "Door", Materials.OAK_DOOR.parseItem()), 22);
					nInv.addItemStack(createItem(island, role, "Gate", Materials.OAK_FENCE_GATE.parseItem()), 23);
					nInv.addItemStack(createItem(island, role, "Projectile", new ItemStack(Material.ARROW)), 24);
					nInv.addItemStack(createItem(island, role, "Enchant", Materials.ENCHANTING_TABLE.parseItem()), 25);
					nInv.addItemStack(createItem(island, role, "Fire", new ItemStack(Material.FLINT_AND_STEEL)), 26);
					nInv.addItemStack(createItem(island, role, "Furnace", new ItemStack(Material.FURNACE)), 27);
					nInv.addItemStack(createItem(island, role, "HorseInventory", Materials.CHEST_MINECART.parseItem()),
							28);
					nInv.addItemStack(createItem(island, role, "MobRiding", new ItemStack(Material.SADDLE)), 29);
					nInv.addItemStack(createItem(island, role, "MobHurting", Materials.WOODEN_SWORD.parseItem()), 30);
					nInv.addItemStack(createItem(island, role, "MobTaming", Materials.POPPY.parseItem()), 31);
					nInv.addItemStack(createItem(island, role, "Leash", Materials.LEAD.parseItem()), 32);
					nInv.addItemStack(createItem(island, role, "LeverButton", new ItemStack(Material.LEVER)), 33);
					nInv.addItemStack(createItem(island, role, "Milking", new ItemStack(Material.MILK_BUCKET)), 34);
					nInv.addItemStack(createItem(island, role, "Jukebox", new ItemStack(Material.JUKEBOX)), 35);
					nInv.addItemStack(
							createItem(island, role, "PressurePlate", Materials.OAK_PRESSURE_PLATE.parseItem()), 36);
					nInv.addItemStack(createItem(island, role, "Redstone", new ItemStack(Material.REDSTONE)), 37);
					nInv.addItemStack(createItem(island, role, "Shearing", new ItemStack(Material.SHEARS)), 38);
					nInv.addItemStack(createItem(island, role, "Trading", new ItemStack(Material.EMERALD)), 39);
					nInv.addItemStack(createItem(island, role, "ItemDrop", new ItemStack(Material.PUMPKIN_SEEDS)), 40);
					nInv.addItemStack(createItem(island, role, "ItemPickup", new ItemStack(Material.MELON_SEEDS)), 41);
					nInv.addItemStack(createItem(island, role, "Fishing", new ItemStack(Material.FISHING_ROD)), 42);
					nInv.addItemStack(createItem(island, role, "DropperDispenser", new ItemStack(Material.DISPENSER)),
							43);
					nInv.addItemStack(createItem(island, role, "SpawnEgg", new ItemStack(Material.EGG)), 44);
					nInv.addItemStack(createItem(island, role, "HangingDestroy", new ItemStack(Material.ITEM_FRAME)),
							45);
					nInv.addItemStack(createItem(island, role, "Cake", new ItemStack(Material.CAKE)), 46);
					nInv.addItemStack(createItem(island, role, "DragonEggUse", new ItemStack(Material.DRAGON_EGG)), 47);
					nInv.addItemStack(createItem(island, role, "MinecartBoat", new ItemStack(Material.MINECART)), 48);
					nInv.addItemStack(createItem(island, role, "Portal", new ItemStack(Material.ENDER_PEARL)), 50);
					nInv.addItemStack(createItem(island, role, "Hopper", new ItemStack(Material.HOPPER)), 51);
					nInv.addItemStack(createItem(island, role, "EntityPlacement", new ItemStack(Material.ARMOR_STAND)),
							52);
					nInv.addItemStack(
							createItem(island, role, "ExperienceOrbPickup", Materials.EXPERIENCE_BOTTLE.parseItem()),
							53);

					nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Settings." + role.name() + ".Title")));
					nInv.setRows(6);
				} else if (role == IslandRole.Operator) {
					if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 9);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										10);
								nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
										11);
								nInv.addItemStack(createItem(island, role, "Unban", Materials.RED_DYE.parseItem()),
										12);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										13);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										14);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										15);
								nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
										16);
								nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
										17);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										21);
								nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
										22);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 24);
							} else {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 9);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										10);
								nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
										11);
								nInv.addItemStack(createItem(island, role, "Unban", Materials.RED_DYE.parseItem()),
										12);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										13);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										14);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										15);
								nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
										16);
								nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
										17);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										21);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 24);
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										11);
								nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
										12);
								nInv.addItemStack(createItem(island, role, "Unban", Materials.RED_DYE.parseItem()),
										13);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										14);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										15);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										16);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										21);
								nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
										22);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 24);
							} else {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										11);
								nInv.addItemStack(createItem(island, role, "Ban", new ItemStack(Material.IRON_AXE)),
										12);
								nInv.addItemStack(createItem(island, role, "Unban", Materials.RED_DYE.parseItem()),
										13);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										14);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										15);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										16);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										21);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 24);
							}
						}

						nInv.setRows(3);
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										11);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										12);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										13);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										14);
								nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
										15);
								nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
										16);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										21);
								nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
										22);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 24);
							} else {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										11);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										12);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										13);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										14);
								nInv.addItemStack(createItem(island, role, "Coop", new ItemStack(Material.NAME_TAG)),
										15);
								nInv.addItemStack(createItem(island, role, "CoopPlayers", new ItemStack(Material.BOOK)),
										16);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										21);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 23);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 24);
							}

							nInv.setRows(3);
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										11);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										12);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										13);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										14);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 15);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										16);
								nInv.addItemStack(createItem(island, role, "Border", new ItemStack(Material.BEACON)),
										21);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 22);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 23);

								nInv.setRows(3);
							} else {
								nInv.addItemStack(
										createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 9);
								nInv.addItemStack(createItem(island, role, "Kick", new ItemStack(Material.IRON_DOOR)),
										10);
								nInv.addItemStack(createItem(island, role, "Visitor", new ItemStack(Materials.OAK_SIGN.parseMaterial())),
										11);
								nInv.addItemStack(createItem(island, role, "Member", new ItemStack(Material.PAINTING)),
										12);
								nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseItem()),
										13);
								nInv.addItemStack(
										createItem(island, role, "MainSpawn", new ItemStack(Material.EMERALD)), 14);
								nInv.addItemStack(
										createItem(island, role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
										15);
								nInv.addItemStack(createItem(island, role, "Biome", new ItemStack(Material.MAP)), 16);
								nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseItem()), 17);

								nInv.setRows(2);
							}
						}
					}

					nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Settings." + role.name() + ".Title")));
				} else if (role == IslandRole.Owner) {
					if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.PvP.Enable")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 9);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											10);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 13);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											14);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 15);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 16);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											17);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 9);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											10);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 16);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 17);
								}
							} else {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 9);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											10);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 16);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											17);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											13);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 16);
								}
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 9);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											10);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 16);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											17);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											13);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 16);
								}
							} else {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											13);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											16);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											14);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 15);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											16);
								}
							}
						}
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 9);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											10);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											11);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 12);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											14);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 15);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 16);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											17);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 13);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											14);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 15);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 16);
								}
							} else {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 13);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											14);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 15);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											16);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath",
											new ItemStack(Material.ITEM_FRAME)), 16);
								}
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 13);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											14);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 15);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											16);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(
											createItem(island, role, "Damage", Materials.RED_DYE.parseItem()), 16);
								}
							} else {
								if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 10);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											11);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											12);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
									nInv.addItemStack(
											createItem(island, role, "Hunger", new ItemStack(Material.COOKED_BEEF)),
											16);
								} else {
									nInv.addItemStack(createItem(island, role, "NaturalMobSpawning",
											Materials.PIG_SPAWN_EGG.parseItem()), 11);
									nInv.addItemStack(
											createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
											12);
									nInv.addItemStack(
											createItem(island, role, "Explosions", Materials.GUNPOWDER.parseItem()),
											13);
									nInv.addItemStack(createItem(island, role, "FireSpread",
											new ItemStack(Material.FLINT_AND_STEEL)), 14);
									nInv.addItemStack(
											createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseItem()),
											15);
								}
							}
						}
					}

					nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Settings." + role.name() + ".Title")));
					nInv.setRows(2);
				}

				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
						configLoad.getString("Menu.Settings." + role.name() + ".Item.Return.Displayname"), null, null,
						null, null), 0, 8);

				Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
					@Override
					public void run() {
						nInv.open();
					}
				});
			} else if (menuType == Settings.Type.Panel) {
				if (panel == Settings.Panel.Welcome) {
					nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (playerDataManager.hasPlayerData(player)) {
								Island island = islandManager.getIsland(player);

								if (island == null) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Owner.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else if (!(island.hasRole(IslandRole.Operator, player.getUniqueId())
										|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Role.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}

								if (!skyblock.getFileManager()
										.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
										.getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}

								ItemStack is = event.getItem();

								if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
										&& (is.getItemMeta().getDisplayName().equals(
												ChatColor.translateAlternateColorCodes('&', configLoad.getString(
														"Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))))) {
									soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLater(skyblock,
											() -> open(player, Type.Role, IslandRole.Visitor, null), 1L);
								} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
										&& (is.getItemMeta().getDisplayName().equals(
												ChatColor.translateAlternateColorCodes('&', configLoad.getString(
														"Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
									if (island.isOpen()) {
										islandManager.closeIsland(island);
										soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
									} else {
										island.setOpen(true);
										soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
									}

									Bukkit.getServer().getScheduler().runTaskLater(skyblock,
											() -> open(player, Type.Role, IslandRole.Visitor, null), 1L);
								} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
												"Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"))))) {
									soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
										.getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
												"Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"))))) {
									if (island.getMessage(IslandMessage.Welcome).size() >= skyblock.getFileManager()
											.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
											.getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLater(skyblock,
												() -> {
													AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
														if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
															Island island1 = islandManager.getIsland(player);

															if (island1 == null) {
																messageManager.sendMessage(player,
																		configLoad.getString(
																				"Command.Island.Settings.Owner.Message"));
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);
																player.closeInventory();

																event1.setWillClose(true);
																event1.setWillDestroy(true);

																return;
															} else if (!(island1.hasRole(IslandRole.Operator,
																	player.getUniqueId())
																	|| island1.hasRole(IslandRole.Owner,
																			player.getUniqueId()))) {
																messageManager.sendMessage(player, configLoad
																		.getString("Command.Island.Role.Message"));
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);
																player.closeInventory();

																event1.setWillClose(true);
																event1.setWillDestroy(true);

																return;
															} else if (!skyblock.getFileManager()
																	.getConfig(new File(skyblock.getDataFolder(),
																			"config.yml"))
																	.getFileConfiguration()
																	.getBoolean("Island.Visitor.Welcome.Enable")) {
																messageManager.sendMessage(player,
																		configLoad.getString(
																				"Island.Settings.Visitor.Welcome.Disabled.Message"));
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);

																event1.setWillClose(true);
																event1.setWillDestroy(true);

																return;
															}

															Config config1 = skyblock.getFileManager()
																	.getConfig(new File(skyblock.getDataFolder(),
																			"config.yml"));
															FileConfiguration configLoad1 = config1
																	.getFileConfiguration();

															if (island1.getMessage(IslandMessage.Welcome)
																	.size() > configLoad1
																			.getInt("Island.Visitor.Welcome.Lines")
																	|| event1.getName().length() > configLoad1
																			.getInt("Island.Visitor.Welcome.Length")) {
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);
															} else {
																List<String> welcomeMessage = island1
																		.getMessage(IslandMessage.Welcome);
																welcomeMessage.add(event1.getName());
																island1.setMessage(IslandMessage.Welcome,
																		player.getName(), welcomeMessage);
																soundManager.playSound(player,
																		Sounds.NOTE_PLING.bukkitSound(), 1.0F,
																		1.0F);
															}

															Bukkit.getServer().getScheduler()
																	.runTaskLater(skyblock,
																			() -> open(player,
																					Type.Panel,
																					null,
																					Panel.Welcome), 1L);

															event1.setWillClose(true);
															event1.setWillDestroy(true);
														} else {
															event1.setWillClose(false);
															event1.setWillDestroy(false);
														}
													});

													ItemStack is1 = new ItemStack(Material.NAME_TAG);
													ItemMeta im = is1.getItemMeta();
													im.setDisplayName(configLoad.getString(
															"Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Word.Enter"));
													is1.setItemMeta(im);

													gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is1);
													gui.open();
												}, 1L);
									}
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
										.getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
												"Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"))))) {
									List<String> welcomeMessage = island.getMessage(IslandMessage.Welcome);

									if (welcomeMessage.size() == 0) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										welcomeMessage.remove(welcomeMessage.size() - 1);
										island.setMessage(IslandMessage.Welcome,
												island.getMessageAuthor(IslandMessage.Welcome), welcomeMessage);
										soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLater(skyblock,
												() -> open(player, Type.Panel, null, Panel.Welcome), 1L);
									}
								}
							}
						}
					});

					List<String> welcomeMessage = island.getMessage(IslandMessage.Welcome);

					if (welcomeMessage.size() == mainConfig.getFileConfiguration()
							.getInt("Island.Visitor.Welcome.Lines")) {
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"),
								configLoad
										.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Limit.Lore"),
								null, null, null), 1);
					} else {
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"),
								configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.More.Lore"),
								null, null, null), 1);
					}

					if (welcomeMessage.size() == 0) {
						List<String> itemLore = new ArrayList<>();
						itemLore.add(
								configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Word.Empty"));
						nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial()),
								configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"),
								itemLore, null, null, null), 2);
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad
										.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"),
								configLoad.getStringList(
										"Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.None.Lore"),
								null, null, null), 3);
					} else {
						nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial(), welcomeMessage.size()),
								configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"),
								welcomeMessage, null, null, null), 2);
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad
										.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"),
								configLoad.getStringList(
										"Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Lines.Lore"),
								null, null, null), 3);
					}

					nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
							configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"), null,
							null, null, null), 0, 4);

					nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title")));
					nInv.setType(InventoryType.HOPPER);

					Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
						@Override
						public void run() {
							nInv.open();
						}
					});
				} else if (panel == Settings.Panel.Signature) {
					nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (playerDataManager.hasPlayerData(player)) {
								Island island = islandManager.getIsland(player);

								if (island == null) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Settings.Owner.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else if (!(island.hasRole(IslandRole.Operator, player.getUniqueId())
										|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
									messageManager.sendMessage(player,
											configLoad.getString("Command.Island.Role.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}

								if (!skyblock.getFileManager()
										.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
										.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
									messageManager.sendMessage(player,
											configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}

								ItemStack is = event.getItem();

								if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
										&& (is.getItemMeta().getDisplayName().equals(
												ChatColor.translateAlternateColorCodes('&', configLoad.getString(
														"Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"))))) {
									soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

									Bukkit.getServer().getScheduler().runTaskLater(skyblock,
											() -> open(player, Type.Role, IslandRole.Visitor, null), 1L);
								} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
										&& (is.getItemMeta().getDisplayName().equals(
												ChatColor.translateAlternateColorCodes('&', configLoad.getString(
														"Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
									if (island.isOpen()) {
										islandManager.closeIsland(island);
										soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
									} else {
										island.setOpen(true);
										soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
									}

									Bukkit.getServer().getScheduler().runTaskLater(skyblock,
											() -> open(player, Type.Role, IslandRole.Visitor, null), 1L);
								} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
												"Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"))))) {
									soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

									event.setWillClose(false);
									event.setWillDestroy(false);
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
										.getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
												"Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"))))) {
									if (island.getMessage(IslandMessage.Signature).size() >= skyblock.getFileManager()
											.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
											.getFileConfiguration().getInt("Island.Visitor.Signature.Lines")) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLater(skyblock,
												() -> {
													AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
														if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
															Island island1 = islandManager.getIsland(player);

															if (island1 == null) {
																messageManager.sendMessage(player,
																		configLoad.getString(
																				"Command.Island.Settings.Owner.Message"));
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);
																player.closeInventory();

																event1.setWillClose(true);
																event1.setWillDestroy(true);

																return;
															} else if (!(island1.hasRole(IslandRole.Operator,
																	player.getUniqueId())
																	|| island1.hasRole(IslandRole.Owner,
																			player.getUniqueId()))) {
																messageManager.sendMessage(player, configLoad
																		.getString("Command.Island.Role.Message"));
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);
																player.closeInventory();

																event1.setWillClose(true);
																event1.setWillDestroy(true);

																return;
															} else if (!skyblock.getFileManager()
																	.getConfig(new File(skyblock.getDataFolder(),
																			"config.yml"))
																	.getFileConfiguration().getBoolean(
																			"Island.Visitor.Signature.Enable")) {
																messageManager.sendMessage(player,
																		configLoad.getString(
																				"Island.Settings.Visitor.Signature.Disabled.Message"));
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);

																event1.setWillClose(true);
																event1.setWillDestroy(true);

																return;
															}

															Config config1 = skyblock.getFileManager()
																	.getConfig(new File(skyblock.getDataFolder(),
																			"config.yml"));
															FileConfiguration configLoad1 = config1
																	.getFileConfiguration();

															if (island1.getMessage(IslandMessage.Signature)
																	.size() > configLoad1.getInt(
																			"Island.Visitor.Signature.Lines")
																	|| event1.getName().length() > configLoad1
																			.getInt("Island.Visitor.Signature.Length")) {
																soundManager.playSound(player,
																		Sounds.ANVIL_LAND.bukkitSound(), 1.0F,
																		1.0F);
															} else {
																List<String> signatureMessage = island1
																		.getMessage(IslandMessage.Signature);
																signatureMessage.add(event1.getName());
																island1.setMessage(IslandMessage.Signature,
																		player.getName(), signatureMessage);
																soundManager.playSound(player,
																		Sounds.NOTE_PLING.bukkitSound(), 1.0F,
																		1.0F);
															}

															Bukkit.getServer().getScheduler()
																	.runTaskLater(skyblock,
																			() -> open(player,
																					Type.Panel,
																					null,
																					Panel.Signature), 1L);

															event1.setWillClose(true);
															event1.setWillDestroy(true);
														} else {
															event1.setWillClose(false);
															event1.setWillDestroy(false);
														}
													});

													ItemStack is12 = new ItemStack(Material.NAME_TAG);
													ItemMeta im = is12.getItemMeta();
													im.setDisplayName(configLoad.getString(
															"Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Word.Enter"));
													is12.setItemMeta(im);

													gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is12);
													gui.open();
												}, 1L);
									}
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta()
										.getDisplayName()
										.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
												"Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"))))) {
									List<String> signatureMessage = island.getMessage(IslandMessage.Signature);

									if (signatureMessage.size() == 0) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										signatureMessage.remove(signatureMessage.size() - 1);
										island.setMessage(IslandMessage.Signature,
												island.getMessageAuthor(IslandMessage.Signature), signatureMessage);
										soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);

										Bukkit.getServer().getScheduler().runTaskLater(skyblock,
												() -> open(player, Type.Panel, null,
														Panel.Signature), 1L);
									}
								}
							}
						}
					});

					List<String> signatureMessage = island.getMessage(IslandMessage.Signature);

					if (signatureMessage.size() == mainConfig.getFileConfiguration()
							.getInt("Island.Visitor.Signature.Lines")) {
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"),
								configLoad.getStringList(
										"Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Limit.Lore"),
								null, null, null), 1);
					} else {
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"),
								configLoad
										.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.More.Lore"),
								null, null, null), 1);
					}

					if (signatureMessage.size() == 0) {
						List<String> itemLore = new ArrayList<>();
						itemLore.add(
								configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Word.Empty"));
						nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial()),
								configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"),
								itemLore, null, null, null), 2);
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad.getString(
										"Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"),
								configLoad.getStringList(
										"Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.None.Lore"),
								null, null, null), 3);
					} else {
						nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial(), signatureMessage.size()),
								configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"),
								signatureMessage, null, null, null), 2);
						nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW),
								configLoad.getString(
										"Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"),
								configLoad.getStringList(
										"Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Lines.Lore"),
								null, null, null), 3);
					}

					nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
							configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"), null,
							null, null, null), 0, 4);

					nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")));
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
	}

	private ItemStack createItem(Island island, IslandRole role, String setting, ItemStack is) {
		SkyBlock skyblock = SkyBlock.getInstance();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		List<String> itemLore = new ArrayList<>();

		ItemMeta im = is.getItemMeta();

		String roleName = role.name();

		if (role == IslandRole.Visitor || role == IslandRole.Member || role == IslandRole.Coop) {
			roleName = "Default";
		}

		im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));

		if (island.getSetting(role, setting).getStatus()) {
			for (String itemLoreList : configLoad
					.getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		} else {
			for (String itemLoreList : configLoad
					.getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		}

		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.setLore(itemLore);
		is.setItemMeta(im);

		return is;
	}

	private String getRoleName(IslandRole role) {
		if (role == IslandRole.Visitor || role == IslandRole.Member || role == IslandRole.Coop) {
			return "Default";
		}

		return role.name();
	}

	private boolean hasPermission(Island island, Player player, IslandRole role) {
		if (role == IslandRole.Visitor || role == IslandRole.Member || role == IslandRole.Coop
				|| role == IslandRole.Owner) {
			String roleName = role.name();

			if (role == IslandRole.Owner) {
				roleName = "Island";
			}

			if (island.hasRole(IslandRole.Operator, player.getUniqueId())
					&& !island.getSetting(IslandRole.Operator, roleName).getStatus()) {
				return false;
			}
		} else if (role == IslandRole.Operator) {
			if (!island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				return false;
			}
		}

		return true;
	}

	public enum Panel {

		Welcome, Signature;

	}

	public enum Type {

		Categories, Panel, Role;

	}
}
