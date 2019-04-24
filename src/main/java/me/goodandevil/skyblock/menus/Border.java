package me.goodandevil.skyblock.menus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.Placeholder;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.WorldBorder;

public class Border {

	private static Border instance;

	public static Border getInstance() {
		if (instance == null) {
			instance = new Border();
		}

		return instance;
	}

	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();

		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
				.getFileConfiguration();

		nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Island island = islandManager.getIsland(player);

				if (island == null) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Owner.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
						&& island.getSetting(IslandRole.Operator, "Border").getStatus())
						|| island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Border.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Disabled.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				}

				ItemStack is = event.getItem();

				if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Border.Item.Exit.Displayname"))))) {
					soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
				} else if ((is.getType() == Material.TRIPWIRE_HOOK) && (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
								configLoad.getString("Menu.Border.Item.Toggle.Displayname"))))) {
					if (island.isBorder()) {
						island.setBorder(false);
					} else {
						island.setBorder(true);
					}

					islandManager.updateBorder(island);
					soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

					Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
				} else if ((is.getType() == Materials.LIGHT_BLUE_DYE.parseMaterial()) && (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName()
								.equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
												configLoad.getString("Menu.Border.Item.Word.Blue")))))) {
					if (island.getBorderColor() == WorldBorder.Color.Blue) {
						soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

						event.setWillClose(false);
						event.setWillDestroy(false);
					} else {
						island.setBorderColor(WorldBorder.Color.Blue);
						islandManager.updateBorder(island);

						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
					}
				} else if ((is.getType() == Materials.LIME_DYE.parseMaterial()) && (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName()
								.equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
												configLoad.getString("Menu.Border.Item.Word.Green")))))) {
					if (island.getBorderColor() == WorldBorder.Color.Green) {
						soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

						event.setWillClose(false);
						event.setWillDestroy(false);
					} else {
						island.setBorderColor(WorldBorder.Color.Green);
						islandManager.updateBorder(island);

						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
					}
				} else if ((is.getType() == Materials.RED_DYE.parseMaterial()) && (is.hasItemMeta())
						&& (is.getItemMeta().getDisplayName()
								.equals(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
												configLoad.getString("Menu.Border.Item.Word.Red")))))) {
					if (island.getBorderColor() == WorldBorder.Color.Red) {
						soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

						event.setWillClose(false);
						event.setWillDestroy(false);
					} else {
						island.setBorderColor(WorldBorder.Color.Red);
						islandManager.updateBorder(island);

						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

						Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
					}
				}
			}
		});

		Island island = islandManager.getIsland(player);

		nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
				configLoad.getString("Menu.Border.Item.Exit.Displayname"), null, null, null, null), 0);

		WorldBorder.Color borderColor = island.getBorderColor();
		String borderToggle;

		if (island.isBorder()) {
			borderToggle = configLoad.getString("Menu.Border.Item.Word.Disable");
		} else {
			borderToggle = configLoad.getString("Menu.Border.Item.Word.Enable");
		}

		nInv.addItem(nInv.createItem(new ItemStack(Material.TRIPWIRE_HOOK),
				configLoad.getString("Menu.Border.Item.Toggle.Displayname"),
				configLoad.getStringList("Menu.Border.Item.Toggle.Lore"),
				new Placeholder[] { new Placeholder("%toggle", borderToggle) }, null, null), 1);

		if (borderColor == WorldBorder.Color.Blue) {
			nInv.addItem(nInv.createItem(Materials.LIGHT_BLUE_DYE.parseItem(),
					configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
							configLoad.getString("Menu.Border.Item.Word.Blue")),
					configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
					new Placeholder[] { new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Blue")) },
					null, null), 2);
		} else {
			nInv.addItem(nInv.createItem(Materials.LIGHT_BLUE_DYE.parseItem(),
					configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
							configLoad.getString("Menu.Border.Item.Word.Blue")),
					configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
					new Placeholder[] { new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Blue")) },
					null, null), 2);
		}

		if (borderColor == WorldBorder.Color.Green) {
			nInv.addItem(nInv.createItem(Materials.LIME_DYE.parseItem(),
					configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
							configLoad.getString("Menu.Border.Item.Word.Green")),
					configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
					new Placeholder[] {
							new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Green")) },
					null, null), 3);
		} else {
			nInv.addItem(nInv.createItem(Materials.LIME_DYE.parseItem(),
					configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
							configLoad.getString("Menu.Border.Item.Word.Green")),
					configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
					new Placeholder[] {
							new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Green")) },
					null, null), 3);
		}

		if (borderColor == WorldBorder.Color.Red) {
			nInv.addItem(nInv.createItem(Materials.RED_DYE.parseItem(),
					configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
							configLoad.getString("Menu.Border.Item.Word.Red")),
					configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
					new Placeholder[] { new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Red")) },
					null, null), 4);
		} else {
			nInv.addItem(nInv.createItem(Materials.RED_DYE.parseItem(),
					configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
							configLoad.getString("Menu.Border.Item.Word.Red")),
					configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
					new Placeholder[] { new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Red")) },
					null, null), 4);
		}

		nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Border.Title")));
		nInv.setType(InventoryType.HOPPER);

		Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
			@Override
			public void run() {
				nInv.open();
			}
		});
	}
}
