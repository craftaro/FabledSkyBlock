package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Information {

    private static Information instance;

    public static Information getInstance(){
        if(instance == null) {
            instance = new Information();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	SkyBlock skyblock = SkyBlock.getInstance();
    	
    	PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
    	MessageManager messageManager = skyblock.getMessageManager();
    	IslandManager islandManager = skyblock.getIslandManager();
    	SoundManager soundManager = skyblock.getSoundManager();
    	FileManager fileManager = skyblock.getFileManager();
    	
    	if (playerDataManager.hasPlayerData(player)) {
    		PlayerData playerData = playerDataManager.getPlayerData(player);
    		
    		if (playerData.getViewer() != null) {
    			Information.Viewer viewer = (Information.Viewer) playerData.getViewer();
    			
    			if (!islandManager.hasIsland(viewer.getOwner())) {
    				islandManager.loadIsland(viewer.getOwner());
    			}

    			FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();
    			Island island = islandManager.getIsland(viewer.getOwner());
    			
    			if (island == null) {
					messageManager.sendMessage(player, configLoad.getString("Island.Information.Island.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
    				return;
    			}
    			
    			if (viewer.getType() == Information.Viewer.Type.Visitors) {
    				if (island.isOpen()) {
    	    			if (island.getVisitors().size() == 0) {
    						messageManager.sendMessage(player, configLoad.getString("Island.Information.Visitors.Message"));
    						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
    	    				
    						playerData.setViewer(new Information.Viewer(viewer.getOwner(), Information.Viewer.Type.Categories));
    						open(player);
    						
    	    				return;	
    	    			}
    				} else {
						messageManager.sendMessage(player, configLoad.getString("Island.Information.Closed.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
	    				
						playerData.setViewer(new Information.Viewer(viewer.getOwner(), Information.Viewer.Type.Categories));
						open(player);
						
	    				return;	
    				}
    			}
    			
    			me.goodandevil.skyblock.visit.Visit visit = island.getVisit();
    			
    			String islandOwnerName = "";
				Player targetPlayer = Bukkit.getServer().getPlayer(viewer.getOwner());
				
				if (targetPlayer == null) {
					islandOwnerName = new OfflinePlayer(viewer.getOwner()).getName();
				} else {
					islandOwnerName = targetPlayer.getName();
				}
    			    			
    			if (viewer.getType() == Information.Viewer.Type.Categories) {
    		    	nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
    					@Override
    					public void onClick(ClickEvent event) {
    						if (playerDataManager.hasPlayerData(player)) {
    							PlayerData playerData = playerDataManager.getPlayerData(player);
    							ItemStack is = event.getItem();
    							
        						if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Exit.Displayname"))))) {
        				    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
        						} else if ((is.getType() == Materials.ITEM_FRAME.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Members.Displayname"))))) {
        							playerData.setViewer(new Information.Viewer(((Information.Viewer) playerData.getViewer()).getOwner(), Information.Viewer.Type.Members));
        							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
        							
        							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player);
										}
        							}, 1L);
        						} else if ((is.getType() == Materials.LEGACY_EMPTY_MAP.getPostMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"))))) {
        							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
        						
        							event.setWillClose(false);
        							event.setWillDestroy(false);
        						} else if ((is.getType() == Materials.PAINTING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Visitors.Displayname"))))) {
        							playerData.setViewer(new Information.Viewer(((Information.Viewer) playerData.getViewer()).getOwner(), Information.Viewer.Type.Visitors));
        							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
        							
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
    		    	
    				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Information.Categories.Item.Exit.Displayname"), null, null, null, null), 0, 4);
    				nInv.addItem(nInv.createItem(Materials.ITEM_FRAME.parseItem(), configLoad.getString("Menu.Information.Categories.Item.Members.Displayname"), configLoad.getStringList("Menu.Information.Categories.Item.Members.Lore"), null, null, null), 1);
    				nInv.addItem(nInv.createItem(Materials.PAINTING.parseItem(), configLoad.getString("Menu.Information.Categories.Item.Visitors.Displayname"), configLoad.getStringList("Menu.Information.Categories.Item.Visitors.Lore"), null, null, null), 3);
    				
    				Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
    				List<String> itemLore = new ArrayList<>();
    				
					if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
							for (String itemLoreList : configLoad.getStringList("Menu.Information.Categories.Item.Information.Vote.Enabled.Signature.Enabled.Lore")) {
								if (itemLoreList.contains("%signature")) {
									List<String> islandSignature = visit.getSiganture();
									
									if (islandSignature.size() == 0) {
										itemLore.add(configLoad.getString("Menu.Information.Categories.Item.Information.Vote.Word.Empty"));
									} else {
										for (String signatureList : islandSignature) {
											itemLore.add(signatureList);
										}
									}
								} else {
									itemLore.add(itemLoreList);
								}
							}
						} else {
							itemLore.addAll(configLoad.getStringList("Menu.Information.Categories.Item.Information.Vote.Enabled.Signature.Disabled.Lore"));
						}
						
						nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"), itemLore, nInv.createItemLoreVariable(new String[] { "%level#" + visit.getLevel().getLevel(), "%members#" + visit.getMembers(), "%votes#" + visit.getVoters().size(), "%visits#" + visit.getVisitors().size(), "%players#" + islandManager.getPlayersAtIsland(island).size(), "%player_capacity#" + mainConfig.getFileConfiguration().getInt("Island.Visitor.Capacity"), "%owner#" + islandOwnerName }), null, null), 2);
					} else {
						if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
							for (String itemLoreList : configLoad.getStringList("Menu.Information.Categories.Item.Information.Vote.Disabled.Signature.Enabled.Lore")) {
								if (itemLoreList.contains("%signature")) {
									List<String> islandSignature = visit.getSiganture();
									
									if (islandSignature.size() == 0) {
										itemLore.add(configLoad.getString("Menu.Information.Categories.Item.Information.Vote.Word.Empty"));
									} else {
										for (String signatureList : islandSignature) {
											itemLore.add(signatureList);
										}
									}
								} else {
									itemLore.add(itemLoreList);
								}
							}
						} else {
							itemLore.addAll(configLoad.getStringList("Menu.Information.Categories.Item.Information.Vote.Disabled.Signature.Disabled.Lore"));
						}
						
						nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"), itemLore, nInv.createItemLoreVariable(new String[] { "%level#" + visit.getLevel().getLevel(), "%members#" + visit.getMembers(), "%visits#" + visit.getVisitors().size(), "%players#" + islandManager.getPlayersAtIsland(island).size(), "%player_capacity#" + mainConfig.getFileConfiguration().getInt("Island.Visitor.Capacity"), "%owner#" + islandOwnerName }), null, null), 2);
					}
    		    	
    		    	nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Title")));
    		    	nInv.setType(InventoryType.HOPPER);
    		    	nInv.open();
    			} else if (viewer.getType() == Information.Viewer.Type.Members) {
    		    	nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
    					@Override
    					public void onClick(ClickEvent event) {
    						if (playerDataManager.hasPlayerData(player)) {
    							PlayerData playerData = playerDataManager.getPlayerData(player);
    							ItemStack is = event.getItem();
    							
        						if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Item.Return.Displayname"))))) {
        							playerData.setViewer(new Information.Viewer(((Information.Viewer) playerData.getViewer()).getOwner(), Information.Viewer.Type.Categories));
        							soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        				    		
        							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player);
										}
        							}, 1L);
        						} else if ((is.getType() == Materials.PAINTING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Item.Statistics.Displayname"))))) {
        							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
        							
        							event.setWillClose(false);
        							event.setWillDestroy(false);
        						} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Item.Barrier.Displayname"))))) {
        							soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
        							
        							event.setWillClose(false);
        							event.setWillDestroy(false);
        						} else if ((is.getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
        							if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Item.Previous.Displayname")))) {
        				    			playerData.setPage(playerData.getPage() - 1);
        				    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        				    		
        				    			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
        									@Override
        									public void run() {
        										open(player);
        									}
        				    			}, 1L);
        				    		} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Item.Next.Displayname")))) {
        				    			playerData.setPage(playerData.getPage() + 1);
        				    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        				    			
        				    			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
        									@Override
        									public void run() {
        										open(player);
        									}
        				    			}, 1L);
        				    		} else {
            							soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
            							
            							event.setWillClose(false);
            							event.setWillDestroy(false);
        				    		}
        						}
    						}
    					}
    		    	});
    		    	
    				List<UUID> displayedMembers = new ArrayList<>();
    				List<UUID> islandMembers = island.getRole(Role.Member);
    				List<UUID> islandOperators = island.getRole(Role.Operator);
    				
    				displayedMembers.add(island.getOwnerUUID());
    				displayedMembers.addAll(islandOperators);
    				displayedMembers.addAll(islandMembers);
    		    	
    		    	nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Information.Members.Item.Return.Displayname"), null, null, null, null), 0, 8);
    		    	nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Information.Members.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Information.Members.Item.Statistics.Lore"), nInv.createItemLoreVariable(new String[] { "%island_members#" + (islandMembers.size() + islandOperators.size() + 1), "%island_capacity#" + skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Member.Capacity"), "%members#" + islandMembers.size(), "%operators#" + islandOperators.size()}), null, null), 4);
    		    	nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Information.Members.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);
    		    	
    	    		int playerMenuPage = playerData.getPage(), nextEndIndex = displayedMembers.size() - playerMenuPage * 36;
    		    	
    	    		if (playerMenuPage != 1) {
    	    			nInv.addItem(nInv.createItem(SkullUtil.create("ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=", "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="), configLoad.getString("Menu.Information.Members.Item.Previous.Displayname"), null, null, null, null), 1);
    	    		}
    	    		
    	    		if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
    	    			nInv.addItem(nInv.createItem(SkullUtil.create("wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=", "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), configLoad.getString("Menu.Information.Members.Item.Next.Displayname"), null, null, null, null), 7);
    	    		}
    	    		
        			int index = playerMenuPage * 36 - 36, endIndex = index >= displayedMembers.size() ? displayedMembers.size() - 1 : index + 36, inventorySlot = 17;
    		    	
    	    		for (; index < endIndex; index++) {
        				if (displayedMembers.size() > index) {
        					inventorySlot++;
        					
        					UUID playerUUID = displayedMembers.get(index);
        					
        					String[] playerTexture;
        					String playerName, islandRole;
        					
        					targetPlayer = Bukkit.getServer().getPlayer(playerUUID);
        					
        					if (targetPlayer == null) {
        						OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
        						playerName = offlinePlayer.getName();
        						playerTexture = offlinePlayer.getTexture();
        					} else {
        						playerName = targetPlayer.getName();
        						playerData = skyblock.getPlayerDataManager().getPlayerData(targetPlayer);
        						playerTexture = playerData.getTexture();
        					}
        					
        					if (islandMembers.contains(playerUUID)) {
        						islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Member");
        					} else if (islandOperators.contains(playerUUID)) {
        						islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Operator");
        					} else {
        						islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Owner");
        					}
        					
        					nInv.addItem(nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]), configLoad.getString("Menu.Information.Members.Item.Member.Displayname").replace("%player", playerName), configLoad.getStringList("Menu.Information.Members.Item.Member.Lore"), nInv.createItemLoreVariable(new String[] { "%role#" + islandRole }), null, null), inventorySlot);
        				}
        			}
    	    		
    		    	nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Title")));
    		    	nInv.setRows(6);
    		    	nInv.open();
    			} else if (viewer.getType() == Information.Viewer.Type.Visitors) {
    		    	nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
    					@Override
    					public void onClick(ClickEvent event) {
    						if (playerDataManager.hasPlayerData(player)) {
    							PlayerData playerData = playerDataManager.getPlayerData(player);
    							ItemStack is = event.getItem();
    							
        						if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Visitors.Item.Return.Displayname"))))) {
        							playerData.setViewer(new Information.Viewer(((Information.Viewer) playerData.getViewer()).getOwner(), Information.Viewer.Type.Categories));
        							soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        				    		
        							Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player);
										}
        							}, 1L);
        						} else if ((is.getType() == Materials.PAINTING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Visitors.Item.Statistics.Displayname"))))) {
        							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
        							
        							event.setWillClose(false);
        							event.setWillDestroy(false);
        						} else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Visitors.Item.Barrier.Displayname"))))) {
        							soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
        							
        							event.setWillClose(false);
        							event.setWillDestroy(false);
        						} else if ((is.getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
        							if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Visitors.Item.Previous.Displayname")))) {
        				    			playerData.setPage(playerData.getPage() - 1);
        				    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        				    		
        				    			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
        									@Override
        									public void run() {
        										open(player);
        									}
        				    			}, 1L);
        				    		} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Visitors.Item.Next.Displayname")))) {
        				    			playerData.setPage(playerData.getPage() + 1);
        				    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        				    			
        				    			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
        									@Override
        									public void run() {
        										open(player);
        									}
        				    			}, 1L);
        				    		} else {
            							soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
            							
            							event.setWillClose(false);
            							event.setWillDestroy(false);
        				    		}
        						}
    						}
    					}
    		    	});
    		    	
    				List<UUID> displayedVisitors = new ArrayList<>();
    				displayedVisitors.addAll(island.getVisitors());
    		    	
    		    	nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Information.Visitors.Item.Return.Displayname"), null, null, null, null), 0, 8);
    		    	nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Information.Visitors.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Information.Visitors.Item.Statistics.Lore"), nInv.createItemLoreVariable(new String[] { "%island_visitors#" + displayedVisitors.size() }), null, null), 4);
    		    	nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Information.Visitors.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);
    		    	
    	    		int playerMenuPage = playerData.getPage(), nextEndIndex = displayedVisitors.size() - playerMenuPage * 36;
    		    	
    	    		if (playerMenuPage != 1) {
    	    			nInv.addItem(nInv.createItem(SkullUtil.create("ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=", "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="), configLoad.getString("Menu.Information.Visitors.Item.Previous.Displayname"), null, null, null, null), 1);
    	    		}
    	    		
    	    		if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
    	    			nInv.addItem(nInv.createItem(SkullUtil.create("wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=", "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), configLoad.getString("Menu.Information.Visitors.Item.Next.Displayname"), null, null, null, null), 7);
    	    		}
    	    		
        			int index = playerMenuPage * 36 - 36, endIndex = index >= displayedVisitors.size() ? displayedVisitors.size() - 1 : index + 36, inventorySlot = 17;
    		    	
    	    		for (; index < endIndex; index++) {
        				if (displayedVisitors.size() > index) {
        					inventorySlot++;
        					
        					UUID playerUUID = displayedVisitors.get(index);
        					
        					String[] playerTexture;
        					String playerName;
        					
        					targetPlayer = Bukkit.getServer().getPlayer(playerUUID);
        					
        					if (targetPlayer == null) {
        						OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
        						playerName = offlinePlayer.getName();
        						playerTexture = offlinePlayer.getTexture();
        					} else {
        						playerName = targetPlayer.getName();
        						playerData = skyblock.getPlayerDataManager().getPlayerData(targetPlayer);
        						playerTexture = playerData.getTexture();
        					}
        					
        					nInv.addItem(nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]), configLoad.getString("Menu.Information.Visitors.Item.Visitor.Displayname").replace("%player", playerName), null, null, null, null), inventorySlot);
        				}
        			}
    	    		
    		    	nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Visitors.Title")));
    		    	nInv.setRows(6);
    		    	nInv.open();
    			}
    			
    			islandManager.unloadIsland(viewer.getOwner());
    		}
    	}
    }
    
    @EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			SkyBlock skyblock = SkyBlock.getInstance();
			
			PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
			SoundManager soundManager = skyblock.getSoundManager();
			
			Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Title")))) {
				event.setCancelled(true);
				
				PlayerData playerData;
				
				if (playerDataManager.hasPlayerData(player)) {
					playerData = playerDataManager.getPlayerData(player);
					
					if (playerData.getViewer() == null) {
						return;
					}
				} else {
					return;
				}
				
				if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Title")))) {
					if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Exit.Displayname"))))) {
			    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
			    		player.closeInventory();
					} else if ((event.getCurrentItem().getType() == Materials.ITEM_FRAME.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Members.Displayname"))))) {
						playerData.setViewer(new Information.Viewer(((Information.Viewer) playerData.getViewer()).getOwner(), Information.Viewer.Type.Members));
						open(player);
						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
					} else if ((event.getCurrentItem().getType() == Materials.LEGACY_EMPTY_MAP.getPostMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"))))) {
						soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
					} else if ((event.getCurrentItem().getType() == Materials.ITEM_FRAME.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Categories.Item.Visitors.Displayname"))))) {
						playerData.setViewer(new Information.Viewer(((Information.Viewer) playerData.getViewer()).getOwner(), Information.Viewer.Type.Visitors));
						open(player);
						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
					}
				} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Information.Members.Title")))) {
					
				}
			}
		}
    }
	
    public static class Viewer {
    	
    	private UUID islandOwnerUUID;
    	private Type type;
    	
    	public Viewer(UUID islandOwnerUUID, Type type) {
    		this.islandOwnerUUID = islandOwnerUUID;
    		this.type = type;
    	}
    	
    	public UUID getOwner() {
    		return islandOwnerUUID;
    	}
    	
    	public Type getType() {
    		return type;
    	}
    	
    	public enum Type {
    		
    		Categories,
    		Members,
    		Visitors;
    		
    	}
    }
}
