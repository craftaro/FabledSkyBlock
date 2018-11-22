package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Visitors implements Listener {
	
    private static Visitors instance;

    public static Visitors getInstance() {
        if(instance == null) {
            instance = new Visitors();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
    	PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
    	FileManager fileManager = plugin.getFileManager();
    	
    	PlayerData playerData = playerDataManager.getPlayerData(player);
    	
    	Island island = plugin.getIslandManager().getIsland(playerData.getOwner());
    	
    	Config languageConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
    	
		Map<Integer, UUID> sortedIslandVisitors = new TreeMap<>();
		List<UUID> islandVisitors = island.getVisitors();
		
    	InventoryUtil inv = new InventoryUtil(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Title")), null, 6);
		inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Visitors.Item.Exit.Displayname"), null, null, null, null), 0, 8);
		inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Visitors.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Visitors.Item.Statistics.Lore"), inv.createItemLoreVariable(new String[] { "%visitors#" + islandVisitors.size() }), null, null), 4);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Visitors.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);
		
		for (UUID islandVisitorList : islandVisitors) {
			sortedIslandVisitors.put(playerDataManager.getPlayerData(Bukkit.getServer().getPlayer(islandVisitorList)).getVisitTime(), islandVisitorList);
		}
		
		islandVisitors.clear();
		
		for (int sortedIslandVisitorList : sortedIslandVisitors.keySet()) {
			islandVisitors.add(sortedIslandVisitors.get(sortedIslandVisitorList));
		}
		
		int playerMenuPage = playerData.getPage(), nextEndIndex = sortedIslandVisitors.size() - playerMenuPage * 36;
		
		if (playerMenuPage != 1) {
			inv.addItem(inv.createItem(SkullUtil.create("ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=", "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="), configLoad.getString("Menu.Visitors.Item.Previous.Displayname"), null, null, null, null), 1);
		}
		
		if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
			inv.addItem(inv.createItem(SkullUtil.create("wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=", "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), configLoad.getString("Menu.Visitors.Item.Next.Displayname"), null, null, null, null), 7);
		}
		
		if (islandVisitors.size() == 0) {
			inv.addItem(inv.createItem(new ItemStack(Material.BARRIER), configLoad.getString("Menu.Visitors.Item.Nothing.Displayname"), null, null, null, null), 31);
		} else {
			boolean isOperator = island.isRole(Role.Operator, player.getUniqueId()), isOwner = island.isRole(Role.Owner, player.getUniqueId()), canKick = island.getSetting(Settings.Role.Operator, "Kick").getStatus(), canBan = island.getSetting(Settings.Role.Operator, "Ban").getStatus(), banningEnabled = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Banning");
			int index = playerMenuPage * 36 - 36, endIndex = index >= islandVisitors.size() ? islandVisitors.size() - 1 : index + 36, inventorySlot = 17;
			
			for (; index < endIndex; index++) {
				if (islandVisitors.size() > index) {
					inventorySlot++;
					
					Player targetPlayer = Bukkit.getServer().getPlayer(islandVisitors.get(index));
					PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
					
					String[] targetPlayerTexture = targetPlayerData.getTexture();
					String islandVisitTimeFormatted;
					
					long[] islandVisitTime = NumberUtil.getDuration(targetPlayerData.getVisitTime());
					
					if (islandVisitTime[0] != 0) {
						islandVisitTimeFormatted = islandVisitTime[0] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Days") + ", " + islandVisitTime[1]  + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Hours") + ", " + islandVisitTime[2] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Minutes") + ", " + islandVisitTime[3] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
					} else if (islandVisitTime[1] != 0) {
						islandVisitTimeFormatted = islandVisitTime[1]  + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Hours") + ", " + islandVisitTime[2] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Minutes") + ", " + islandVisitTime[3] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
					} else if (islandVisitTime[2] != 0) {
						islandVisitTimeFormatted = islandVisitTime[2] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Minutes") + ", " + islandVisitTime[3] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
					} else {
						islandVisitTimeFormatted = islandVisitTime[3] + " " + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
					}
					
					List<String> itemLore = new ArrayList<>();
					
					if ((isOperator && canKick) || isOwner) {
						if (banningEnabled && ((isOperator && canBan) || isOwner)) {
							itemLore.addAll(configLoad.getStringList("Menu.Visitors.Item.Visitor.Kick.Permission.Ban.Permission.Lore"));
						} else {
							itemLore.addAll(configLoad.getStringList("Menu.Visitors.Item.Visitor.Kick.Permission.Ban.NoPermission.Lore"));
						}
					} else {
						if (banningEnabled && ((isOperator && canBan) || isOwner)) {
							itemLore.addAll(configLoad.getStringList("Menu.Visitors.Item.Visitor.Kick.NoPermission.Ban.Permission.Lore"));
						} else {
							itemLore.addAll(configLoad.getStringList("Menu.Visitors.Item.Visitor.Kick.NoPermission.Ban.NoPermission.Lore"));
						}
					}
					
					inv.addItem(inv.createItem(SkullUtil.create(targetPlayerTexture[0], targetPlayerTexture[1]), ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Visitor.Displayname").replace("%player", targetPlayer.getName())), itemLore, inv.createItemLoreVariable(new String[] { "%time#" + islandVisitTimeFormatted }), null, null), inventorySlot);
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
			
			FileManager fileManager = plugin.getFileManager();
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Title")))) {
				event.setCancelled(true);
				
				PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
				
				IslandManager islandManager = plugin.getIslandManager();
				SoundManager soundManager = plugin.getSoundManager();
				
				Island island = null;
				
				if (islandManager.hasIsland(player)) {
					island = islandManager.getIsland(playerData.getOwner());
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Visitors.Owner.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();
					
					return;
				}
				
				if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Barrier.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Exit.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
		    		player.closeInventory();
		    	} else if ((event.getCurrentItem().getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Statistics.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Nothing.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
		    		if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Previous.Displayname")))) {
		    			playerData.setPage(playerData.getPage() - 1);
		    			open(player);
		    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
		    		} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Item.Next.Displayname")))) {
		    			playerData.setPage(playerData.getPage() + 1);
		    			open(player);
		    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
		    		} else {
		    			String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
		    			
		    			boolean isOperator = island.isRole(Role.Operator, player.getUniqueId()), isOwner = island.isRole(Role.Owner, player.getUniqueId()), canKick = island.getSetting(Settings.Role.Operator, "Kick").getStatus(), canBan = island.getSetting(Settings.Role.Operator, "Ban").getStatus(), banningEnabled = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Banning");
		    			
						if ((isOperator && canKick) || isOwner) {
							if (banningEnabled && ((isOperator && canBan) || isOwner)) {
								if (event.getClick() == ClickType.LEFT) {
									Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);
								} else if (event.getClick() == ClickType.RIGHT) {
									Bukkit.getServer().dispatchCommand(player, "island ban " + playerName);
								} else {
									soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
							} else {
								Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);
							}
						} else {
							if (banningEnabled && ((isOperator && canBan) || isOwner)) {
								Bukkit.getServer().dispatchCommand(player, "island ban " + playerName);
							} else {
								soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
							
								return;
							}
						}
						
						new BukkitRunnable() {
							@Override
							public void run() {
								open(player);
							}
						}.runTaskLater(plugin, 3L);
		    		}
		    	}
			}
		}
	}
}
