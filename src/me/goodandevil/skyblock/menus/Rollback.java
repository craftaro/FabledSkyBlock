package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Rollback implements Listener {

    private static Rollback instance;

    public static Rollback getInstance() {
        if(instance == null) {
            instance = new Rollback();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
		Config languageConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
		
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Title")));
		
		ItemStack is;
		ItemMeta im;
		
		List<String> itemLore = new ArrayList<>();
		
		is = Materials.BLACK_STAINED_GLASS_PANE.parseItem();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Barrier.Displayname")));
		is.setItemMeta(im);
		inv.setItem(1, is);
		
		is = new ItemStack(Materials.WRITABLE_BOOK.parseMaterial());
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Save.Displayname")));
		
		for (String itemLoreList : configLoad.getStringList("Menu.Rollback.Item.Save.Lore")) {
			itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
		}
		
		im.setLore(itemLore);
		is.setItemMeta(im);
		inv.setItem(2, is);
		itemLore.clear();
		
		is = new ItemStack(Material.ENCHANTED_BOOK);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Load.Displayname")));
		
		for (String itemLoreList : configLoad.getStringList("Menu.Rollback.Item.Load.Lore")) {
			itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
		}
		
		im.setLore(itemLore);
		is.setItemMeta(im);
		inv.setItem(3, is);
		itemLore.clear();
		
		is = new ItemStack(Material.HOPPER);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Reset.Displayname")));
		
		for (String itemLoreList : configLoad.getStringList("Menu.Rollback.Item.Reset.Lore")) {
			itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
		}
		
		im.setLore(itemLore);
		is.setItemMeta(im);
		inv.setItem(4, is);
		itemLore.clear();
		
		player.openInventory(inv);
    }
    
    private Island island = null;
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			
			Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Title")))) {
				event.setCancelled(true);
				
				IslandManager islandManager = plugin.getIslandManager();
				SoundManager soundManager = plugin.getSoundManager();
				
				if (islandManager.hasIsland(player)) {
					island = islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
					
					if (!island.isRole(Role.Owner, player.getUniqueId())) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Rollback.Role.Message")));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						player.closeInventory();
						
						return;
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Rollback.Owner.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();
					
					return;
				}
				
		    	if ((event.getCurrentItem().getType() == Material.NAME_TAG) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Info.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Barrier.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.WRITABLE_BOOK.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Save.Displayname"))))) {
		    		/*new BukkitRunnable() {
		    			public void run() {
				    		for (Location.World worldList : Location.World.values()) {
				    			Location islandLocation = island.getLocation(worldList, Location.Environment.Island);
				    			
				    			try {
									Schematic.getInstance().save(new File(new File(plugin.getDataFolder().toString() + "/rollback-data/" + island.getOwnerUUID().toString()), worldList.name() + ".schematic"), new Location(islandLocation.getWorld(), islandLocation.getBlockX() + 85, islandLocation.getBlockY(), islandLocation.getBlockZ() + 85), new Location(islandLocation.getWorld(), islandLocation.getBlockX() - 85, islandLocation.getBlockY(), islandLocation.getBlockZ() - 85));
								} catch (Exception e) {
									e.printStackTrace();
								}
				    		}
		    			}
		    		}.runTaskAsynchronously(plugin);*/
		    		
		    		soundManager.playSound(player, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Item.Load.Displayname"))))) {
		    		/*new BukkitRunnable() {
		    			public void run() {
				    		for (Location.World worldList : Location.World.values()) {
				    			Location islandLocation = island.getLocation(worldList, Location.Environment.Island);
				    			
					    		try {
									Schematic.getInstance().paste(new File(new File(plugin.getDataFolder().toString() + "/rollback-data/" + island.getOwnerUUID().toString()), "Normal.schematic"), new Location(islandLocation.getWorld(), islandLocation.getBlockX() - 85, 0, islandLocation.getBlockZ() - 85), true);
								} catch (Exception e) {
									e.printStackTrace();
								}
				    		}
		    			}
		    		}.runTaskAsynchronously(plugin);*/
		    		
		    		soundManager.playSound(player, Sounds.PISTON_EXTEND.bukkitSound(), 1.0F, 1.0F);
		    	}
			}
		}
	}
}
