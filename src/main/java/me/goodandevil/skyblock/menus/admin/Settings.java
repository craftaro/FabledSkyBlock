package me.goodandevil.skyblock.menus.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Settings {

	private static Settings instance;

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}

		return instance;
	}

	public void open(Player player, Settings.Type menuType, me.goodandevil.skyblock.island.IslandRole role) {
		SkyBlock skyblock = SkyBlock.getInstance();

		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();

		if (menuType == Settings.Type.Categories) {
			nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!(player.hasPermission("skyblock.admin.settings") || player.hasPermission("skyblock.admin.*")
							|| player.hasPermission("skyblock.*"))) {
						messageManager.sendMessage(player,
								configLoad.getString("Island.Admin.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

						return;
					}

					ItemStack is = event.getItem();

					if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"))))) {
						soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
					} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
							.equals(ChatColor.translateAlternateColorCodes('&', configLoad
									.getString("Menu.Admin.Settings.Categories.Item.Visitor.Displayname"))))) {
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Role, me.goodandevil.skyblock.island.IslandRole.Visitor);
							}
						}, 1L);
					} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Settings.Categories.Item.Member.Displayname"))))) {
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Role, me.goodandevil.skyblock.island.IslandRole.Member);
							}
						}, 1L);
					} else if ((is.getType() == Material.ITEM_FRAME) && (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&', configLoad
											.getString("Menu.Admin.Settings.Categories.Item.Operator.Displayname"))))) {
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Role, me.goodandevil.skyblock.island.IslandRole.Operator);
							}
						}, 1L);
					} else if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Settings.Categories.Item.Coop.Displayname"))))) {
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Role, me.goodandevil.skyblock.island.IslandRole.Coop);
							}
						}, 1L);
					} else if ((is.getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"))))) {
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Role, me.goodandevil.skyblock.island.IslandRole.Owner);
							}
						}, 1L);
					}
				}
			});

			nInv.addItem(nInv.createItem(new ItemStack(Material.SIGN),
					configLoad.getString("Menu.Admin.Settings.Categories.Item.Visitor.Displayname"),
					configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
			nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
					configLoad.getString("Menu.Admin.Settings.Categories.Item.Member.Displayname"),
					configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
			nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME),
					configLoad.getString("Menu.Admin.Settings.Categories.Item.Operator.Displayname"),
					configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Operator.Lore"), null, null, null),
					4);

			if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
					.getBoolean("Island.Coop.Enable")) {
				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
						configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"), null, null, null,
						null), 0);
				nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
						configLoad.getString("Menu.Admin.Settings.Categories.Item.Coop.Displayname"),
						configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Coop.Lore"), null, null, null),
						6);
				nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(),
						configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"),
						configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Owner.Lore"), null, null, null),
						7);
			} else {
				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
						configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"), null, null, null,
						null), 0, 8);
				nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(),
						configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"),
						configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Owner.Lore"), null, null, null),
						6);
			}

			nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
					configLoad.getString("Menu.Admin.Settings.Categories.Title")));
			nInv.setRows(1);

			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					nInv.open();
				}
			});
		} else if (menuType == Settings.Type.Role) {
			nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!(player.hasPermission("skyblock.admin.settings") || player.hasPermission("skyblock.admin.*")
							|| player.hasPermission("skyblock.*"))) {
						messageManager.sendMessage(player,
								configLoad.getString("Island.Admin.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

						return;
					}

					ItemStack is = event.getItem();

					if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is
							.getItemMeta().getDisplayName()
							.equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Settings.Visitor.Item.Return.Displayname")))
							|| is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Admin.Settings.Member.Item.Return.Displayname")))
							|| is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&',
											configLoad
													.getString("Menu.Admin.Settings.Operator.Item.Return.Displayname")))
							|| is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Admin.Settings.Owner.Item.Return.Displayname"))))) {
						soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Categories, null);
							}
						}, 1L);
					} else if (is.hasItemMeta()) {
						String roleName = getRoleName(role);

						FileConfiguration settingsConfigLoad = skyblock.getFileManager()
								.getConfig(new File(skyblock.getDataFolder(), "settings.yml")).getFileConfiguration();

						for (String settingList : settingsConfigLoad.getConfigurationSection("Settings." + role.name())
								.getKeys(false)) {
							if (is.getItemMeta().getDisplayName()
									.equals(ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Admin.Settings." + roleName + ".Item.Setting."
													+ settingList + ".Displayname")))) {
								if (settingsConfigLoad.getBoolean("Settings." + role.name() + "." + settingList)) {
									settingsConfigLoad.set("Settings." + role.name() + "." + settingList, false);
								} else {
									settingsConfigLoad.set("Settings." + role.name() + "." + settingList, true);
								}

								Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										try {
											Config config = skyblock.getFileManager()
													.getConfig(new File(skyblock.getDataFolder(), "settings.yml"));
											config.getFileConfiguration().save(config.getFile());
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});

								break;
							}
						}

						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
							@Override
							public void run() {
								open(player, Settings.Type.Role, role);
							}
						}, 1L);
					}
				}
			});

			if (role == me.goodandevil.skyblock.island.IslandRole.Visitor
					|| role == me.goodandevil.skyblock.island.IslandRole.Member
					|| role == me.goodandevil.skyblock.island.IslandRole.Coop) {
				nInv.addItemStack(createItem(role, "Destroy", new ItemStack(Material.DIAMOND_PICKAXE)), 9);
				nInv.addItemStack(createItem(role, "Place", new ItemStack(Material.GRASS)), 10);
				nInv.addItemStack(createItem(role, "Anvil", new ItemStack(Material.ANVIL)), 11);
				nInv.addItemStack(createItem(role, "ArmorStandUse", new ItemStack(Material.ARMOR_STAND)), 12);
				nInv.addItemStack(createItem(role, "Beacon", new ItemStack(Material.BEACON)), 13);
				nInv.addItemStack(createItem(role, "Bed", Materials.WHITE_BED.parseItem()), 14);
				nInv.addItemStack(createItem(role, "AnimalBreeding", new ItemStack(Material.WHEAT)), 15);
				nInv.addItemStack(
						createItem(role, "Brewing", new ItemStack(Materials.LEGACY_BREWING_STAND.getPostMaterial())),
						16);
				nInv.addItemStack(createItem(role, "Bucket", new ItemStack(Material.BUCKET)), 17);
				nInv.addItemStack(createItem(role, "WaterCollection", new ItemStack(Material.POTION)), 18);
				nInv.addItemStack(createItem(role, "Storage", new ItemStack(Material.CHEST)), 19);
				nInv.addItemStack(createItem(role, "Workbench", Materials.CRAFTING_TABLE.parseItem()), 20);
				nInv.addItemStack(createItem(role, "Crop", Materials.WHEAT_SEEDS.parseItem()), 21);
				nInv.addItemStack(createItem(role, "Door", Materials.OAK_DOOR.parseItem()), 22);
				nInv.addItemStack(createItem(role, "Gate", Materials.OAK_FENCE_GATE.parseItem()), 23);
				nInv.addItemStack(createItem(role, "Projectile", new ItemStack(Material.ARROW)), 24);
				nInv.addItemStack(createItem(role, "Enchant", Materials.ENCHANTING_TABLE.parseItem()), 25);
				nInv.addItemStack(createItem(role, "Fire", new ItemStack(Material.FLINT_AND_STEEL)), 26);
				nInv.addItemStack(createItem(role, "Furnace", new ItemStack(Material.FURNACE)), 27);
				nInv.addItemStack(createItem(role, "HorseInventory", Materials.CHEST_MINECART.parseItem()), 28);
				nInv.addItemStack(createItem(role, "MobRiding", new ItemStack(Material.SADDLE)), 29);
				nInv.addItemStack(createItem(role, "MobHurting", Materials.WOODEN_SWORD.parseItem()), 30);
				nInv.addItemStack(createItem(role, "MobTaming", Materials.POPPY.parseItem()), 31);
				nInv.addItemStack(createItem(role, "Leash", Materials.LEAD.parseItem()), 32);
				nInv.addItemStack(createItem(role, "LeverButton", new ItemStack(Material.LEVER)), 33);
				nInv.addItemStack(createItem(role, "Milking", new ItemStack(Material.MILK_BUCKET)), 34);
				nInv.addItemStack(createItem(role, "Jukebox", new ItemStack(Material.JUKEBOX)), 35);
				nInv.addItemStack(createItem(role, "PressurePlate", Materials.OAK_PRESSURE_PLATE.parseItem()), 36);
				nInv.addItemStack(createItem(role, "Redstone", new ItemStack(Material.REDSTONE)), 37);
				nInv.addItemStack(createItem(role, "Shearing", new ItemStack(Material.SHEARS)), 38);
				nInv.addItemStack(createItem(role, "Trading", new ItemStack(Material.EMERALD)), 39);
				nInv.addItemStack(createItem(role, "ItemDrop", new ItemStack(Material.PUMPKIN_SEEDS)), 40);
				nInv.addItemStack(createItem(role, "ItemPickup", new ItemStack(Material.MELON_SEEDS)), 41);
				nInv.addItemStack(createItem(role, "Fishing", new ItemStack(Material.FISHING_ROD)), 42);
				nInv.addItemStack(createItem(role, "DropperDispenser", new ItemStack(Material.DISPENSER)), 43);
				nInv.addItemStack(createItem(role, "SpawnEgg", new ItemStack(Material.EGG)), 44);
				nInv.addItemStack(createItem(role, "Cake", new ItemStack(Material.CAKE)), 46);
				nInv.addItemStack(createItem(role, "DragonEggUse", new ItemStack(Material.DRAGON_EGG)), 47);
				nInv.addItemStack(createItem(role, "MinecartBoat", new ItemStack(Material.MINECART)), 48);
				nInv.addItemStack(createItem(role, "Portal", new ItemStack(Material.ENDER_PEARL)), 49);
				nInv.addItemStack(createItem(role, "Hopper", new ItemStack(Material.HOPPER)), 50);
				nInv.addItemStack(createItem(role, "ArmorStandPlacement", new ItemStack(Material.ARMOR_STAND)), 51);
				nInv.addItemStack(createItem(role, "ExperienceOrbPickup", Materials.EXPERIENCE_BOTTLE.parseItem()), 52);

				nInv.setRows(6);
			} else if (role == me.goodandevil.skyblock.island.IslandRole.Operator) {
				if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
					if (mainConfig.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 9);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 10);
							nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 11);
							nInv.addItemStack(createItem(role, "Unban", Materials.ROSE_RED.parseItem()), 12);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 13);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 14);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 15);
							nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 16);
							nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 17);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									21);
							nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 22);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 24);
						} else {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 9);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 10);
							nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 11);
							nInv.addItemStack(createItem(role, "Unban", Materials.ROSE_RED.parseItem()), 12);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 13);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 14);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 15);
							nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 16);
							nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 17);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									21);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 24);
						}
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
							nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 12);
							nInv.addItemStack(createItem(role, "Unban", Materials.ROSE_RED.parseItem()), 13);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 14);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 15);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 16);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									21);
							nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 22);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 24);
						} else {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
							nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 12);
							nInv.addItemStack(createItem(role, "Unban", Materials.ROSE_RED.parseItem()), 13);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 14);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 15);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 16);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									21);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 24);
						}
					}

					nInv.setRows(3);
				} else {
					if (mainConfig.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 12);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 13);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 14);
							nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 15);
							nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 16);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									21);
							nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 22);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 24);
						} else {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 12);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 13);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 14);
							nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 15);
							nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 16);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									21);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 24);
						}

						nInv.setRows(3);
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 10);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 12);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 13);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 14);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 15);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									16);
							nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 21);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 22);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 23);

							nInv.setRows(3);
						} else {
							nInv.addItemStack(createItem(role, "Invite", Materials.WRITABLE_BOOK.parseItem()), 9);
							nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 10);
							nInv.addItemStack(createItem(role, "Visitor", new ItemStack(Material.SIGN)), 11);
							nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 12);
							nInv.addItemStack(createItem(role, "Island", Materials.OAK_SAPLING.parseItem()), 13);
							nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 14);
							nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
									15);
							nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 16);
							nInv.addItemStack(createItem(role, "Weather", Materials.CLOCK.parseItem()), 17);

							nInv.setRows(2);
						}
					}
				}
			} else if (role == me.goodandevil.skyblock.island.IslandRole.Owner) {
				if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.PvP.Enable")) {
					if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()), 9);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 14);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 16);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()), 9);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 17);
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()), 9);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 13);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
							}
						}
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()), 9);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 16);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 13);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 16);
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 13);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 14);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 15);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 16);
							}
						}
					}
				} else {
					if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()), 9);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 11);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 12);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 14);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 16);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 14);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 16);
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 14);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(
										createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
							}
						}
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 14);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 15);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(createItem(role, "Damage", Materials.ROSE_RED.parseItem()), 16);
							}
						} else {
							if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										10);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 12);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
								nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
							} else {
								nInv.addItemStack(
										createItem(role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseItem()),
										11);
								nInv.addItemStack(createItem(role, "MobGriefing", Materials.IRON_SHOVEL.parseItem()),
										12);
								nInv.addItemStack(createItem(role, "Explosions", Materials.GUNPOWDER.parseItem()), 13);
								nInv.addItemStack(
										createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
								nInv.addItemStack(createItem(role, "LeafDecay", Materials.OAK_LEAVES.parseItem()), 15);
							}
						}
					}
				}

				nInv.setRows(2);
			}

			nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
					configLoad.getString("Menu.Admin.Settings." + role.name() + ".Item.Return.Displayname"), null, null,
					null, null), 0, 8);
			nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
					configLoad.getString("Menu.Admin.Settings." + role.name() + ".Title")));

			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					nInv.open();
				}
			});
		}
	}

	private ItemStack createItem(me.goodandevil.skyblock.island.IslandRole role, String setting, ItemStack is) {
		SkyBlock skyblock = SkyBlock.getInstance();

		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		List<String> itemLore = new ArrayList<>();

		ItemMeta im = is.getItemMeta();

		String roleName = role.name();

		if (role == me.goodandevil.skyblock.island.IslandRole.Visitor
				|| role == me.goodandevil.skyblock.island.IslandRole.Member
				|| role == me.goodandevil.skyblock.island.IslandRole.Coop) {
			roleName = "Default";
		}

		im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				configLoad.getString("Menu.Admin.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));

		if (fileManager.getConfig(new File(skyblock.getDataFolder(), "settings.yml")).getFileConfiguration()
				.getBoolean("Settings." + role.name() + "." + setting)) {
			for (String itemLoreList : configLoad
					.getStringList("Menu.Admin.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		} else {
			for (String itemLoreList : configLoad
					.getStringList("Menu.Admin.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		}

		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.setLore(itemLore);
		is.setItemMeta(im);

		return is;
	}

	private String getRoleName(me.goodandevil.skyblock.island.IslandRole role) {
		if (role == me.goodandevil.skyblock.island.IslandRole.Visitor
				|| role == me.goodandevil.skyblock.island.IslandRole.Member
				|| role == me.goodandevil.skyblock.island.IslandRole.Coop) {
			return "Default";
		}

		return role.name();
	}

	public enum Type {

		Categories, Panel, Role;

	}
}
