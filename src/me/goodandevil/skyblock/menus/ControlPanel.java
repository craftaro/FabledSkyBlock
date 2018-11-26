package me.goodandevil.skyblock.menus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class ControlPanel implements Listener {

    private static ControlPanel instance;

    public static ControlPanel getInstance(){
        if(instance == null) {
            instance = new ControlPanel();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	SkyBlock skyblock = SkyBlock.getInstance();
    	
    	Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		InventoryUtil inv = new InventoryUtil(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Title")), null, 1);
		inv.addItem(inv.createItem(Materials.OAK_DOOR.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Teleport.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Teleport.Lore"), null, null, null), 0);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 1);
		inv.addItem(inv.createItem(new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial()), configLoad.getString("Menu.ControlPanel.Item.Level.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Level.Lore"), null, null, null), 2);
		inv.addItem(inv.createItem(new ItemStack(Material.NAME_TAG), configLoad.getString("Menu.ControlPanel.Item.Settings.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Settings.Lore"), null, null, null), 3);
		inv.addItem(inv.createItem(new ItemStack(Material.ITEM_FRAME), configLoad.getString("Menu.ControlPanel.Item.Members.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Members.Lore"), null, null, null), 4);
		inv.addItem(inv.createItem(Materials.OAK_SAPLING.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Biome.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Biome.Lore"), null, null, null), 5);
		inv.addItem(inv.createItem(Materials.CLOCK.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Weather.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Weather.Lore"), null, null, null), 6);
		inv.addItem(inv.createItem(new ItemStack(Material.IRON_AXE), configLoad.getString("Menu.ControlPanel.Item.Bans.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Bans.Lore"), null, null, new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES }), 7);
		inv.addItem(inv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.ControlPanel.Item.Visitors.Displayname"), configLoad.getStringList("Menu.ControlPanel.Item.Visitors.Lore"), null, null, null), 8);
		
		player.openInventory(inv.getInventory());
    }
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			SkyBlock skyblock = SkyBlock.getInstance();
			
			Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Title")))) {
				event.setCancelled(true);
				
		    	if ((event.getCurrentItem().getType() == Materials.OAK_DOOR.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Teleport.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island teleport");
		    	} else if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"))))) {
		    		skyblock.getSoundManager().playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.EXPERIENCE_BOTTLE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Level.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island level");
		    	} else if ((event.getCurrentItem().getType() == Material.NAME_TAG) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Settings.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island settings");
		    	} else if ((event.getCurrentItem().getType() == Material.ITEM_FRAME) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Members.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island members");
		    	} else if ((event.getCurrentItem().getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Biome.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island biome");
		    	} else if ((event.getCurrentItem().getType() == Materials.CLOCK.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Weather.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island weather");
		    	} else if ((event.getCurrentItem().getType() == Material.IRON_AXE) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Bans.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island bans");
		    	} else if ((event.getCurrentItem().getType() == Material.SIGN) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Item.Visitors.Displayname"))))) {
		    		player.closeInventory();
		    		Bukkit.getServer().dispatchCommand(player, "island visitors");
		    	}
			}
		}
	}
}
