package me.goodandevil.skyblock.menus;

import java.io.File;

import me.goodandevil.skyblock.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class ControlPanel {

	private static ControlPanel instance;

	public static ControlPanel getInstance() {
		if (instance == null) {
			instance = new ControlPanel();
		}

		return instance;
	}

	public void open(Player player) {
		SkyBlock skyblock = SkyBlock.getInstance();
		Island island = skyblock.getIslandManager().getIsland(player);

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		nInventoryUtil nInv = new nInventoryUtil(player, event -> {
			ItemStack is = event.getItem();

			if ((is.getType() == Materials.OAK_DOOR.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Teleport.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island teleport"), 1L);
			} else if ((is.getType() == Materials.IRON_DOOR.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
					configLoad.getString("Menu.ControlPanel.Item.Lock.Displayname"))))) {
					if (island.isOpen()) {
						Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island close"), 1L);
					} else {
						Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island open"), 1L);
					}
			} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"))))) {
				skyblock.getSoundManager().playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

				event.setWillClose(false);
				event.setWillDestroy(false);
			} else if ((is.getType() == Materials.EXPERIENCE_BOTTLE.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Level.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island level"), 1L);
			} else if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Settings.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island settings"), 1L);
			} else if ((is.getType() == Material.ITEM_FRAME) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Members.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island members"), 1L);
			} else if ((is.getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Biome.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island biome"), 1L);
			} else if ((is.getType() == Materials.CLOCK.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Weather.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island weather"), 1L);
			} else if ((is.getType() == Material.IRON_AXE) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
							configLoad.getString("Menu.ControlPanel.Item.Bans.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island bans"), 1L);
			} else if ((is.getType() == Materials.OAK_SIGN.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
					configLoad.getString("Menu.ControlPanel.Item.Visitors.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island visitors"), 1L);
			} else if ((is.getType() == Materials.ANVIL.parseMaterial()) && (is.hasItemMeta())
					&& (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
					configLoad.getString("Menu.ControlPanel.Item.Upgrades.Displayname"))))) {
				Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island upgrades"), 1L);
			}
		});

		// Teleport to island and open/close island
		nInv.addItem(nInv.createItem(Materials.OAK_DOOR.parseItem(),
				configLoad.getString("Menu.ControlPanel.Item.Teleport.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Teleport.Lore"), null, null, null), 1);
		nInv.addItem(nInv.createItem(Materials.IRON_DOOR.parseItem(),
				configLoad.getString("Menu.ControlPanel.Item.Lock.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Lock.Lore"), null, null, null), 10);

		// Glass panes barriers
		nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
				configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 0, 2, 5, 8, 9, 11, 14, 17);

		// 4 Items at the left
		nInv.addItem(nInv.createItem(new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial()),
				configLoad.getString("Menu.ControlPanel.Item.Level.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Level.Lore"), null, null, null), 3);
		nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
				configLoad.getString("Menu.ControlPanel.Item.Settings.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Settings.Lore"), null, null, null), 4);
		nInv.addItem(nInv.createItem(Materials.CLOCK.parseItem(),
				configLoad.getString("Menu.ControlPanel.Item.Weather.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Weather.Lore"), null, null, null), 12);
		nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(),
				configLoad.getString("Menu.ControlPanel.Item.Biome.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Biome.Lore"), null, null, null), 13);

		// 4 Items at the right
		nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME),
				configLoad.getString("Menu.ControlPanel.Item.Members.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Members.Lore"), null, null, null), 16);
		nInv.addItem(nInv.createItem(new ItemStack(Material.IRON_AXE),
				configLoad.getString("Menu.ControlPanel.Item.Bans.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Bans.Lore"), null, null,
				new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES }), 6);
		nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial()),
				configLoad.getString("Menu.ControlPanel.Item.Visitors.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Visitors.Lore"), null, null, null), 7);
		nInv.addItem(nInv.createItem(new ItemStack(Materials.ANVIL.parseMaterial()),
				configLoad.getString("Menu.ControlPanel.Item.Upgrades.Displayname"),
				configLoad.getStringList("Menu.ControlPanel.Item.Upgrades.Lore"), null, null, null), 15);

		nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Title")));
		nInv.setRows(2);

		Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
	}
}
