package me.goodandevil.skyblock.menus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.biome.BiomeManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.cooldown.Cooldown;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownPlayer;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Biomes;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Biome {

	private static Biome instance;

	public static Biome getInstance() {
		if (instance == null) {
			instance = new Biome();
		}

		return instance;
	}

	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		CooldownManager cooldownManager = skyblock.getCooldownManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		BiomeManager biomeManager = skyblock.getBiomeManager();
		SoundManager soundManager = skyblock.getSoundManager();

		if (playerDataManager.hasPlayerData(player)) {
			Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Island island = null;

					if (playerDataManager.hasPlayerData(player) && islandManager.hasIsland(player)) {
						island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());

						if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
								&& island.getSetting(IslandRole.Operator, "Biome").getStatus())
								|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
							messageManager.sendMessage(player,
									config.getFileConfiguration().getString("Command.Island.Biome.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							player.closeInventory();

							return;
						}
					} else {
						messageManager.sendMessage(player,
								config.getFileConfiguration().getString("Command.Island.Biome.Owner.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						player.closeInventory();

						return;
					}

					ItemStack is = event.getItem();

					if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Biome.Item.Info.Displayname"))))) {
						soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

						event.setWillClose(false);
						event.setWillDestroy(false);
					} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
							&& (is.hasItemMeta())
							&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Biome.Item.Barrier.Displayname"))))) {
						soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

						event.setWillClose(false);
						event.setWillDestroy(false);
					} else {
						if (is.getItemMeta().hasEnchant(Enchantment.THORNS)) {
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);
						} else {
							if (cooldownManager.hasPlayer(CooldownType.Biome, player)) {
								CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Biome,
										player);
								Cooldown cooldown = cooldownPlayer.getCooldown();

								if (cooldown.getTime() < 60) {
									messageManager.sendMessage(player,
											config.getFileConfiguration().getString("Island.Biome.Cooldown.Message")
													.replace("%time",
															cooldown.getTime() + " " + config.getFileConfiguration()
																	.getString("Island.Biome.Cooldown.Word.Second")));
								} else {
									long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
									messageManager.sendMessage(player,
											config.getFileConfiguration().getString("Island.Biome.Cooldown.Message")
													.replace("%time", durationTime[2] + " "
															+ config.getFileConfiguration().getString(
																	"Island.Biome.Cooldown.Word.Minute")
															+ " " + durationTime[3] + " "
															+ config.getFileConfiguration()
																	.getString("Island.Biome.Cooldown.Word.Second")));
								}

								soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

								event.setWillClose(false);
								event.setWillDestroy(false);

								return;
							}

							org.bukkit.block.Biome selectedBiomeType = null;

							if (is.getType() == Materials.SUNFLOWER.parseMaterial()) {
								selectedBiomeType = org.bukkit.block.Biome.PLAINS;
							} else if (is.getType() == Materials.FERN.parseMaterial()) {
								selectedBiomeType = org.bukkit.block.Biome.FOREST;
							} else if (is.getType() == Materials.LILY_PAD.parseMaterial()) {
								selectedBiomeType = Biomes.SWAMPLAMD.bukkitBiome();
							} else if (is.getType() == Material.DEAD_BUSH) {
								selectedBiomeType = org.bukkit.block.Biome.DESERT;
							} else if (is.getType() == Materials.SNOWBALL.parseMaterial()) {
								selectedBiomeType = Biomes.COLD_BEACH.bukkitBiome();
							} else if (is.getType() == Material.VINE) {
								selectedBiomeType = org.bukkit.block.Biome.JUNGLE;
							} else if (is.getType() == Materials.DARK_OAK_SAPLING.parseMaterial()) {
								selectedBiomeType = Biomes.ROOFED_FOREST.bukkitBiome();
							}

							if (!player.hasPermission("skyblock.bypass.cooldown")
									&& !player.hasPermission("skyblock.bypass.*")
									&& !player.hasPermission("skyblock.*")) {
								cooldownManager.createPlayer(CooldownType.Biome, player);
								biomeManager.setBiome(island, selectedBiomeType);
							}

							island.setBiome(selectedBiomeType);
							island.save();

							soundManager.playSound(island.getLocation(IslandWorld.Normal, IslandEnvironment.Island),
									Sounds.SPLASH.bukkitSound(), 1.0F, 1.0F);

							if (!islandManager.isPlayerAtIsland(island, player, IslandWorld.Normal)) {
								soundManager.playSound(player, Sounds.SPLASH.bukkitSound(), 1.0F, 1.0F);
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

			Island island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
			String islandBiomeName = island.getBiomeName();

			int NMSVersion = NMSUtil.getVersionNumber();

			nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG, 1),
					ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.Biome.Item.Info.Displayname")),
					configLoad.getStringList("Menu.Biome.Item.Info.Lore"),
					new Placeholder[] { new Placeholder("%biome_type", islandBiomeName) }, null, null), 0);
			nInv.addItem(
					nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
							ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Menu.Biome.Item.Barrier.Displayname")),
							null, null, null, null),
					1);

			if (islandBiomeName.equals("Plains")) {
				nInv.addItem(
						nInv.createItem(Materials.SUNFLOWER.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						2);
			} else {
				nInv.addItem(nInv.createItem(Materials.SUNFLOWER.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname").replace("%biome_type",
										"Plains")),
						configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null), 2);
			}

			if (islandBiomeName.equals("Forest")) {
				nInv.addItem(
						nInv.createItem(Materials.FERN.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						3);
			} else {
				nInv.addItem(nInv.createItem(Materials.FERN.parseItem(),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname").replace("%biome_type",
										"Forest")),
						configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null), 3);
			}

			if (islandBiomeName.equals("Swampland") || islandBiomeName.equals("Swamp")) {
				nInv.addItem(
						nInv.createItem(Materials.LILY_PAD.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						4);
			} else {
				if (NMSVersion < 13) {
					nInv.addItem(
							nInv.createItem(Materials.LILY_PAD.parseItem(),
									ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname")
													.replace("%biome_type", "Swampland")),
									configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
							4);
				} else {
					nInv.addItem(
							nInv.createItem(Materials.LILY_PAD.parseItem(),
									ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname")
													.replace("%biome_type", "Swamp")),
									configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
							4);
				}
			}

			if (islandBiomeName.equals("Desert")) {
				nInv.addItem(
						nInv.createItem(new ItemStack(Material.DEAD_BUSH, 1),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						5);
			} else {
				nInv.addItem(nInv.createItem(new ItemStack(Material.DEAD_BUSH, 1),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname").replace("%biome_type",
										"Desert")),
						configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null), 5);
			}

			if (islandBiomeName.equals("Cold Beach") || islandBiomeName.equals("Snowy Beach")) {
				nInv.addItem(
						nInv.createItem(Materials.SNOWBALL.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						6);
			} else {
				if (NMSVersion < 13) {
					nInv.addItem(
							nInv.createItem(Materials.SNOWBALL.parseItem(),
									ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname")
													.replace("%biome_type", "Cold Beach")),
									configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
							6);
				} else {
					nInv.addItem(
							nInv.createItem(Materials.SNOWBALL.parseItem(),
									ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname")
													.replace("%biome_type", "Snowy Beach")),
									configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
							6);
				}
			}

			if (islandBiomeName.equals("Jungle")) {
				nInv.addItem(
						nInv.createItem(new ItemStack(Material.VINE, 1),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						7);
			} else {
				nInv.addItem(nInv.createItem(new ItemStack(Material.VINE, 1),
						ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname").replace("%biome_type",
										"Jungle")),
						configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null), 7);
			}

			if (islandBiomeName.equals("Roofed Forest") || islandBiomeName.equals("Dark Forest")) {
				nInv.addItem(
						nInv.createItem(Materials.DARK_OAK_SAPLING.parseItem(),
								ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Biome.Item.Biome.Current.Displayname")
												.replace("%biome_type", islandBiomeName)),
								configLoad.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
								new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }),
						8);
			} else {
				if (NMSVersion < 13) {
					nInv.addItem(
							nInv.createItem(Materials.DARK_OAK_SAPLING.parseItem(),
									ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname")
													.replace("%biome_type", "Roofed Forest")),
									configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
							8);
				} else {
					nInv.addItem(
							nInv.createItem(Materials.DARK_OAK_SAPLING.parseItem(),
									ChatColor.translateAlternateColorCodes('&',
											configLoad.getString("Menu.Biome.Item.Biome.Select.Displayname")
													.replace("%biome_type", "Dark Forest")),
									configLoad.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
							8);
				}
			}

			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Biome.Title")));
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
