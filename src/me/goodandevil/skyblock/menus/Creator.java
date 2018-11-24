package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.creation.Creation;
import me.goodandevil.skyblock.creation.CreationManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Creator implements Listener {

    private static Creator instance;

    public static Creator getInstance(){
        if(instance == null) {
            instance = new Creator();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
    	
		List<Structure> availableStructures = new ArrayList<>();
		
		for (Structure structureList : plugin.getStructureManager().getStructures()) {
			if (structureList.getDisplayname() == null || structureList.getDisplayname().isEmpty() || structureList.getFile() == null || structureList.getFile().isEmpty()) {
				continue;
			}
			
			if (structureList.isPermission()) {
				if (!player.hasPermission(structureList.getPermission()) && !player.hasPermission("skyblock.island.*") && !player.hasPermission("skyblock.*")) {
					continue;
				}
			}
			
			availableStructures.add(structureList);
		}
		
		int inventoryRows = 0;
		
		if (availableStructures.size() == 0) {
			plugin.getMessageManager().sendMessage(player, configLoad.getString("Island.Creator.Selector.None.Message"));
			plugin.getSoundManager().playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			
			return;
		} else if (availableStructures.size() <= 9) {
			inventoryRows = 1;
		} else if (availableStructures.size() <= 18) {
			inventoryRows = 2;
		} else if (availableStructures.size() <= 27) {
			inventoryRows = 3;
		} else if (availableStructures.size() <= 36) {
			inventoryRows = 4;
		} else if (availableStructures.size() <= 45) {
			inventoryRows = 5;
		} else if (availableStructures.size() <= 54) {
			inventoryRows = 6;
		}
		
    	InventoryUtil inv = new InventoryUtil(configLoad.getString("Menu.Creator.Selector.Title"), null, inventoryRows);
    	
    	for (int i = 0; i < availableStructures.size(); i++) {
    		Structure structure = availableStructures.get(i);
    		List<String> itemLore = new ArrayList<>();
    		
    		for (String itemLoreList : configLoad.getStringList("Menu.Creator.Selector.Item.Island.Lore")) {
    			if (itemLoreList.contains("%description")) {
    				if (structure.getDescription() == null || structure.getDescription().isEmpty()) {
    					itemLore.add(configLoad.getString("Menu.Creator.Selector.Item.Island.Word.Empty"));
    				} else {
        				for (String descriptionList : structure.getDescription()) {
        					itemLore.add(ChatColor.translateAlternateColorCodes('&', descriptionList));
        				}
    				}
    			} else {
    				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
    			}
    		}
    		
    		inv.addItem(inv.createItem(structure.getMaterials().parseItem(), ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Creator.Selector.Item.Island.Displayname").replace("%displayname", structure.getDisplayname())), itemLore, null, null, null), i);
    	}
    	
    	player.openInventory(inv.getInventory());
    }
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			
			MessageManager messageManager = plugin.getMessageManager();
			IslandManager islandManager = plugin.getIslandManager();
			SoundManager soundManager = plugin.getSoundManager();
			FileManager fileManager = plugin.getFileManager();
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Creator.Selector.Title")))) {
				event.setCancelled(true);
				
				CreationManager creationManager = plugin.getCreationManager();
				
				if (islandManager.hasIsland(player)) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
				
				for (Structure structureList : plugin.getStructureManager().getStructures()) {
					if ((event.getCurrentItem().getType() == structureList.getMaterials().parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Creator.Selector.Item.Island.Displayname").replace("%displayname", structureList.getDisplayname()))))) {
						if (structureList.isPermission() && structureList.getPermission() != null && !structureList.getPermission().isEmpty()) {
							if (!player.hasPermission(structureList.getPermission()) && !player.hasPermission("skyblock.island.*") && !player.hasPermission("skyblock.*")) {
								messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Permission.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								open(player);
								
								return;
							}
						}
						
						if (!fileManager.isFileExist(new File(new File(plugin.getDataFolder().toString() + "/structures"), structureList.getFile()))) {
							messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.File.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							
							return;
						} else if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable") && creationManager.hasPlayer(player)) {
							Creation creation = creationManager.getPlayer(player);
							
							if (creation.getTime() < 60) {
								messageManager.sendMessage(player, config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message").replace("%time", creation.getTime() + " " + config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Word.Second")));
							} else {
								long[] durationTime = NumberUtil.getDuration(creation.getTime());
								messageManager.sendMessage(player, config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message").replace("%time", durationTime[2] + " " + config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Word.Minute") + " " + durationTime[3] + " " + config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Word.Second")));
							}
							
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
						
						islandManager.createIsland(player, structureList);
						
						messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
						
						player.closeInventory();
						
						return;
					}
				}
			}
		}
	}
}
