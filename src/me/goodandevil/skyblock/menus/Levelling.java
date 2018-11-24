package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Level;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.item.MaterialUtil;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Levelling implements Listener {
	
    private static Levelling instance;

    public static Levelling getInstance(){
        if(instance == null) {
            instance = new Levelling();
        }
        
        return instance;
    }
	
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
    	FileManager fileManager = plugin.getFileManager();
    	PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
    	
    	Island island = plugin.getIslandManager().getIsland(playerData.getOwner());
    	Level level = island.getLevel();
    	
		Map<String, Integer> islandMaterials = level.getMaterials();
		
		int playerMenuPage = playerData.getPage(), nextEndIndex = islandMaterials.size() - playerMenuPage * 36;
    	
		Config languageConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
		
		InventoryUtil inv = new InventoryUtil(configLoad.getString("Menu.Levelling.Title"), null, 6);
		inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Levelling.Item.Exit.Displayname"), null, null, null, null), 0, 8);
		inv.addItem(inv.createItem(Materials.FIREWORK_STAR.parseItem(), configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Rescan.Lore"), null, null, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS }), 3, 5);
		inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Statistics.Lore"), inv.createItemLoreVariable(new String[] { "%level_points#" + NumberUtil.formatNumber(level.getPoints()), "%level#" + NumberUtil.formatNumber(level.getLevel()) }), null, null), 4);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);
		
		if (playerMenuPage != 1) {
			inv.addItem(inv.createItem(SkullUtil.create("ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=", "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="), configLoad.getString("Menu.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
		}
		
		if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
			inv.addItem(inv.createItem(SkullUtil.create("wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=", "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), configLoad.getString("Menu.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
		}
		
		if (islandMaterials.size() == 0) {
			inv.addItem(inv.createItem(new ItemStack(Material.BARRIER), configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
		} else {
			int index = playerMenuPage * 36 - 36, endIndex = index >= islandMaterials.size() ? islandMaterials.size() - 1 : index + 36, inventorySlot = 17;
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
			
			for (; index < endIndex; index++) {
				if (islandMaterials.size() > index) {
					String material = (String) islandMaterials.keySet().toArray()[index];
					Materials materials = Materials.fromString(material);
					
					if (materials != null) {
						int materialAmount = islandMaterials.get(material);
						
						if (config.getFileConfiguration().getString("Materials." + material + ".Points") != null) {
							int pointsRequired = config.getFileConfiguration().getInt("Materials." + material + ".Points");
							
							if (pointsRequired != 0) {
								inventorySlot++;
								
								int pointsEarned = materialAmount*pointsRequired;
								
								ItemStack is = materials.parseItem();
								is.setAmount(materialAmount);
								is.setType(MaterialUtil.correctMaterial(is.getType()));
								
								inv.addItem(inv.createItem(is, configLoad.getString("Menu.Levelling.Item.Material.Displayname").replace("%points", NumberUtil.formatNumber(pointsEarned)).replace("%material", WordUtils.capitalize(material.toLowerCase().replace("_", " ").replace("item", "").replace("block", ""))), null, null, null, null), inventorySlot);
							}
						}
					}
				}
			}
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
			
			Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Title")))) {
				event.setCancelled(true);
				
				if (!islandManager.hasIsland(player)) {
					messageManager.sendMessage(player, config.getFileConfiguration().getString("Command.Island.Level.Owner.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();
					
					return;
				}
				
				if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Exit.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
		    		player.closeInventory();
		    	} else if ((event.getCurrentItem().getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.FIREWORK_STAR.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"))))) {
		    		LevellingManager levellingManager = plugin.getLevellingManager();
		    		Island island = islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
		    		
		    		if (levellingManager.hasLevelling(island.getOwnerUUID())) {
						me.goodandevil.skyblock.levelling.Levelling levelling = levellingManager.getLevelling(island.getOwnerUUID());
						long[] durationTime = NumberUtil.getDuration(levelling.getTime());
						
						if (levelling.getTime() >= 3600) {
							messageManager.sendMessage(player, config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Message").replace("%time", durationTime[1] + " " + config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[2] + " " + config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[3] + " " + config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Word.Second")));
						} else if (levelling.getTime() >= 60) {
							messageManager.sendMessage(player, config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Message").replace("%time", durationTime[2] + " " + config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[3] + " " + config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Word.Second")));							
						} else {
							messageManager.sendMessage(player, config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Message").replace("%time", levelling.getTime() + " " + config.getFileConfiguration().getString("Command.Island.Level.Cooldown.Word.Second")));
						}
						
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
		    		
		    		player.closeInventory();
		    		
		    		new BukkitRunnable() {
						public void run() {
							messageManager.sendMessage(player, config.getFileConfiguration().getString("Command.Island.Level.Processing.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
							
							levellingManager.createLevelling(island.getOwnerUUID());
							levellingManager.loadLevelling(island.getOwnerUUID());
							levellingManager.calculatePoints(player, island);
						}
					}.runTaskAsynchronously(plugin);
		    	} else if ((event.getCurrentItem().getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
		    		PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
		    		
		    		if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Previous.Displayname")))) {
		    			playerData.setPage(playerData.getPage() - 1);
		    			open(player);
		    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
		    		} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Next.Displayname")))) {
		    			playerData.setPage(playerData.getPage() + 1);
		    			open(player);
		    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
		    		} else {
		    			soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
		    		}
		    	} else {
		    		soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
		    	}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		
		Main plugin = Main.getInstance();
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Title")))) {
			plugin.getSoundManager().playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
		}
	}
}
