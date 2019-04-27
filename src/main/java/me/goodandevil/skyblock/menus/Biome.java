package me.goodandevil.skyblock.menus;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.biome.BiomeManager;
import me.goodandevil.skyblock.cooldown.Cooldown;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownPlayer;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.island.*;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.SBiome;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;

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
			FileConfiguration langConfig = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();

			nInventoryUtil nInv = new nInventoryUtil(player, event -> {
				Island island = islandManager.getIsland(player);

				if (island == null) {
					messageManager.sendMessage(player,
							langConfig.getString("Command.Island.Biome.Owner.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();

					return;
				} else if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
						&& island.getSetting(IslandRole.Operator, "Biome").getStatus())
						|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
					messageManager.sendMessage(player,
							langConfig.getString("Command.Island.Biome.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();

					return;
				}

				ItemStack is = event.getItem();

				if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
								langConfig.getString("Menu.Biome.Item.Info.Displayname"))))) {
					soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

					event.setWillClose(false);
					event.setWillDestroy(false);
				} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
						&& (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
								langConfig.getString("Menu.Biome.Item.Barrier.Displayname"))))) {
					soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

					event.setWillClose(false);
					event.setWillDestroy(false);
				} else if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
langConfig.getString("Menu.Biome.Item.Exit.Displayname"))))) {
soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
				} else {
					if (is.getItemMeta().hasEnchant(Enchantment.THORNS)) {
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

						event.setWillClose(false);
						event.setWillDestroy(false);
					} else {
						if (cooldownManager.hasPlayer(CooldownType.Biome, player) && !player.hasPermission("fabledskyblock.bypass.cooldown")) {
							CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Biome, player);
							Cooldown cooldown = cooldownPlayer.getCooldown();

							if (cooldown.getTime() < 60) {
								messageManager.sendMessage(player,
										langConfig.getString("Island.Biome.Cooldown.Message")
												.replace("%time",
														cooldown.getTime() + " " + langConfig
																.getString("Island.Biome.Cooldown.Word.Second")));
							} else {
								long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
								messageManager.sendMessage(player,
										langConfig.getString("Island.Biome.Cooldown.Message")
												.replace("%time", durationTime[2] + " "
														+ langConfig.getString("Island.Biome.Cooldown.Word.Minute")
														+ " " + durationTime[3] + " "
														+ langConfig.getString("Island.Biome.Cooldown.Word.Second")));
							}

							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

							event.setWillClose(false);
							event.setWillDestroy(false);

							return;
						}

						@SuppressWarnings("deprecation")
						SBiome selectedBiomeType = SBiome.getFromGuiIcon(is.getType(), is.getData().getData());

						cooldownManager.createPlayer(CooldownType.Biome, player);
						biomeManager.setBiome(island, selectedBiomeType.getBiome());
						island.setBiome(selectedBiomeType.getBiome());
						island.save();

						soundManager.playSound(island.getLocation(IslandWorld.Normal, IslandEnvironment.Island),
								Sounds.SPLASH.bukkitSound(), 1.0F, 1.0F);

						if (!islandManager.isPlayerAtIsland(island, player, IslandWorld.Normal)) {
							soundManager.playSound(player, Sounds.SPLASH.bukkitSound(), 1.0F, 1.0F);
						}

						Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
					}
				}
			});

			Island island = islandManager.getIsland(player);
			org.bukkit.block.Biome islandBiome = island.getBiome();
			String islandBiomeName = island.getBiomeName();

			nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
					ChatColor.translateAlternateColorCodes('&',
							langConfig.getString("Menu.Biome.Item.Info.Displayname")),
					langConfig.getStringList("Menu.Biome.Item.Info.Lore"),
					new Placeholder[] { new Placeholder("%biome_type", islandBiomeName) }, null, null), 4);
			
			nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
                            langConfig.getString("Menu.Biome.Item.Exit.Displayname"), null, null, null, null),
                    0, 8);
			
			nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
							ChatColor.translateAlternateColorCodes('&',
									langConfig.getString("Menu.Biome.Item.Barrier.Displayname")),
							null, null, null, null),
					9, 10, 11, 12, 13, 14, 15, 16, 17);
			
			FileConfiguration settings = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration();
			
			boolean allowNetherBiome = settings.getBoolean("Island.Biome.AllowOtherWorldlyBiomes.Nether");
			boolean allowEndBiome = settings.getBoolean("Island.Biome.AllowOtherWorldlyBiomes.End");

			int slotIndex = 18;
			for (SBiome biome : SBiome.values()) {
			    if (!biome.isAvailable()) 
			        continue;
			    
			    if (!allowNetherBiome && biome.equals(SBiome.NETHER))
			        continue;
			    
			    if (!allowEndBiome && (biome.equals(SBiome.THE_END) || biome.equals(SBiome.THE_VOID)))
			        continue;

			    if (!player.hasPermission("fabledskyblock.biome.*") && !player.hasPermission("fabledskyblock.biome." + biome.name().toLowerCase()))
			    	continue;
			    
			    if (islandBiome.equals(biome.getBiome())) {
			        nInv.addItem(nInv.createItem(biome.getGuiIcon(),
                            ChatColor.translateAlternateColorCodes('&',
                                    langConfig.getString("Menu.Biome.Item.Biome.Current.Displayname")
                                        .replace("%biome_type", biome.getFormattedBiomeName())),
                            langConfig.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
                            new Enchantment[] { Enchantment.THORNS }, new ItemFlag[] { ItemFlag.HIDE_ENCHANTS }), 
			            slotIndex);
			    } else {
			        nInv.addItem(nInv.createItem(biome.getGuiIcon(),
	                        ChatColor.translateAlternateColorCodes('&',
	                                langConfig.getString("Menu.Biome.Item.Biome.Select.Displayname")
	                                    .replace("%biome_type", biome.getFormattedBiomeName())),
	                        langConfig.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null), 
			            slotIndex);
			    }
			    
			    slotIndex++;
			}

			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', langConfig.getString("Menu.Biome.Title")));
			nInv.setRows(4);

			Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
		}
	}
}
