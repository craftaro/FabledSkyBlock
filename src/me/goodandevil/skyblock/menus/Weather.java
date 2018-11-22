package me.goodandevil.skyblock.menus;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Weather implements Listener {

    private static Weather instance;

    public static Weather getInstance(){
        if(instance == null) {
            instance = new Weather();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
    	Island island = plugin.getIslandManager().getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
    	
		Config languageConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
		
		int islandTime = island.getTime();
		String timeName = "", timeChoice = "", weatherSynchronised, weatherChoice, synchronisedChoice;
		
		if (island.isWeatherSynchronised()) {
			weatherSynchronised = configLoad.getString("Menu.Weather.Item.Info.Synchronised.Enabled");
		} else {
			weatherSynchronised = configLoad.getString("Menu.Weather.Item.Info.Synchronised.Disabled");
		}
		
		if (islandTime == 0) {
			timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Dawn");
			timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Day");
		} else if (islandTime == 1000) {
			timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Day");
			timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Noon");
		} else if (islandTime == 6000) {
			timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Noon");
			timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Dusk");
		} else if (islandTime == 12000) {
			timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Dusk");
			timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Night");
		} else if (islandTime == 13000) {
			timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Night");
			timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Midnight");
		} else if (islandTime == 18000) {
			timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Midnight");
			timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Dawn");
		}
		
		if (island.getWeather() == WeatherType.CLEAR) {
			weatherChoice = configLoad.getString("Menu.Weather.Item.Weather.Choice.Downfall");
		} else {
			weatherChoice = configLoad.getString("Menu.Weather.Item.Weather.Choice.Clear");
		}
		
		if (island.isWeatherSynchronised()) {
			synchronisedChoice = configLoad.getString("Menu.Weather.Item.Synchronised.Choice.Disable");
		} else {
			synchronisedChoice = configLoad.getString("Menu.Weather.Item.Synchronised.Choice.Enable");
		}
		
		InventoryUtil inv = new InventoryUtil(configLoad.getString("Menu.Weather.Title"), InventoryType.HOPPER, 0);
		inv.addItem(inv.createItem(new ItemStack(Material.NAME_TAG), configLoad.getString("Menu.Weather.Item.Info.Displayname"), configLoad.getStringList("Menu.Weather.Item.Info.Lore"), inv.createItemLoreVariable(new String[] { "%synchronised#" + weatherSynchronised, "%time_name#" + timeName, "%time#" + island.getTime(), "%weather#" + island.getWeatherName() }), null, null), 0);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Weather.Item.Barrier.Displayname"), null, null, null, null), 1);
		inv.addItem(inv.createItem(Materials.SUNFLOWER.parseItem(), configLoad.getString("Menu.Weather.Item.Time.Displayname"), configLoad.getStringList("Menu.Weather.Item.Time.Lore"), inv.createItemLoreVariable(new String[] { "%choice#" + timeChoice }), null, null), 2);
		inv.addItem(inv.createItem(new ItemStack(Material.GHAST_TEAR), configLoad.getString("Menu.Weather.Item.Weather.Displayname"), configLoad.getStringList("Menu.Weather.Item.Weather.Lore"), inv.createItemLoreVariable(new String[] { "%choice#" + weatherChoice }), null, null), 3);
		inv.addItem(inv.createItem(new ItemStack(Material.TRIPWIRE_HOOK), configLoad.getString("Menu.Weather.Item.Synchronised.Displayname"), configLoad.getStringList("Menu.Weather.Item.Synchronised.Lore"), inv.createItemLoreVariable(new String[] { "%choice#" + synchronisedChoice }), null, null), 4);
		
		player.openInventory(inv.getInventory());
    }
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			FileManager fileManager = plugin.getFileManager();
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Title")))) {
				event.setCancelled(true);
				
				IslandManager islandManager = plugin.getIslandManager();
				SoundManager soundManager = plugin.getSoundManager();
				
				Island island = null;
				
				if (islandManager.hasIsland(player)) {
					island = islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
					
					if (!((island.isRole(Role.Operator, player.getUniqueId()) && island.getSetting(Settings.Role.Operator, "Biome").getStatus()) || island.isRole(Role.Owner, player.getUniqueId()))) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Weather.Permission.Message")));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						player.closeInventory();
						
						return;
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Weather.Owner.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();
					
					return;
				}
				
		    	if ((event.getCurrentItem().getType() == Material.NAME_TAG) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Item.Info.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Item.Barrier.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.SUNFLOWER.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Item.Time.Displayname"))))) {
		    		int islandTime = island.getTime();
		    		
		    		if (islandTime == 0) {
		    			island.setTime(1000);
		    		} else if (islandTime == 1000) {
		    			island.setTime(6000);
		    		} else if (islandTime == 6000) {
		    			island.setTime(12000);
		    		} else if (islandTime == 12000) {
		    			island.setTime(13000);
		    		} else if (islandTime == 13000) {
		    			island.setTime(18000);
		    		} else if (islandTime == 18000) {
		    			island.setTime(0);
		    		}
		    		
		    		if (!island.isWeatherSynchronised()) {
		    			for (Player all : islandManager.getPlayersAtIsland(island, Location.World.Normal)) {
		    				all.setPlayerTime(island.getTime(), fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
		    			}
		    		}
		    		
		    		open(player);
		    		soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.GHAST_TEAR) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Item.Weather.Displayname"))))) {
		    		if (island.getWeather() == WeatherType.DOWNFALL) {
		    			island.setWeather(WeatherType.CLEAR);
		    		} else {
		    			island.setWeather(WeatherType.DOWNFALL);
		    		}
		    		
		    		if (!island.isWeatherSynchronised()) {
		    			for (Player all : islandManager.getPlayersAtIsland(island, Location.World.Normal)) {
		    				all.setPlayerWeather(island.getWeather());
		    			}
		    		}
		    		
		    		open(player);
		    		soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.TRIPWIRE_HOOK) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Item.Synchronised.Displayname"))))) {
		    		if (island.isWeatherSynchronised()) {
		    			island.setWeatherSynchronised(false);
		    			
		    			int islandTime = island.getTime();
		    			WeatherType islandWeather = island.getWeather();
		    			
		    			for (Player all : islandManager.getPlayersAtIsland(island, Location.World.Normal)) {
		    				all.setPlayerTime(islandTime, fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
		    				all.setPlayerWeather(islandWeather);
		    			}
		    		} else {
		    			island.setWeatherSynchronised(true);
		    			
		    			for (Player all : islandManager.getPlayersAtIsland(island, Location.World.Normal)) {
		    				all.resetPlayerTime();
		    				all.resetPlayerWeather();
		    			}
		    		}
		    		
		    		open(player);
		    		soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
		    	}
			}
		}
	}
}
