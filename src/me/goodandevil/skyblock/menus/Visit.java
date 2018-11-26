package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.StringUtil;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.visit.VisitManager;

public class Visit implements Listener {

    private static Visit instance;

    public static Visit getInstance() {
        if(instance == null) {
            instance = new Visit();
        }
        
        return instance;
    }
	
	public void open(Player player, Visit.Type type, Visit.Sort sort) {
		SkyBlock skyblock = SkyBlock.getInstance();
		
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		IslandManager islandManager = skyblock.getIslandManager();
		VisitManager visitManager = skyblock.getVisitManager();
		FileManager fileManager = skyblock.getFileManager();
		
		Map<UUID, me.goodandevil.skyblock.visit.Visit> displayedIslands = new HashMap<>();
		Map<UUID, me.goodandevil.skyblock.visit.Visit> visitIslands = visitManager.getOpenIslands();
		
		if (type == Visit.Type.Solo) {
			for (UUID visitIslandList : visitIslands.keySet()) {
				if (visitIslands.get(visitIslandList).getMembers() != 1) {
					visitIslands.remove(visitIslandList);
				}
			}
		} else if (type == Visit.Type.Team) {
			for (UUID visitIslandList : visitIslands.keySet()) {
				if (visitIslands.get(visitIslandList).getMembers() == 1) {
					visitIslands.remove(visitIslandList);
				}
			}
		}
		
		if (sort == Visit.Sort.Default) {
			displayedIslands.putAll(visitIslands);
		} else if (sort == Visit.Sort.Players || sort == Visit.Sort.Level || sort == Visit.Sort.Members || sort == Visit.Sort.Visits || sort == Visit.Sort.Votes) {
			Map<Integer, List<UUID>> sortedIslands = new TreeMap<>(Collections.reverseOrder());
			
			for (UUID visitIslandList : visitIslands.keySet()) {
				int islandInteger = 0;
				
				if (sort == Visit.Sort.Players) {
					if (islandManager.containsIsland(visitIslandList)) {
						islandInteger = islandManager.getPlayersAtIsland(islandManager.getIsland(visitIslandList)).size();
					}
				} else if (sort == Visit.Sort.Level) {
					islandInteger = visitIslands.get(visitIslandList).getLevel().getLevel();
				} else if (sort == Visit.Sort.Members) {
					islandInteger = visitIslands.get(visitIslandList).getMembers();
				} else if (sort == Visit.Sort.Visits) {
					islandInteger = visitIslands.get(visitIslandList).getVisitors().size();
				} else if (sort == Visit.Sort.Votes) {
					islandInteger = visitIslands.get(visitIslandList).getVoters().size();
				}
				
				List<UUID> identicalIslands = new ArrayList<>();
				
				if (sortedIslands.containsKey(islandInteger)) {
					identicalIslands.addAll(sortedIslands.get(islandInteger));
				}
				
				identicalIslands.add(visitIslandList);
				sortedIslands.put(islandInteger, identicalIslands);
			}
			
			for (int sortedIslandList : sortedIslands.keySet()) {
				List<UUID> identicalIslands = sortedIslands.get(sortedIslandList);
				
				for (UUID identicalIslandList : identicalIslands) {
					displayedIslands.put(identicalIslandList, visitIslands.get(identicalIslandList));
				}
			}
		}
		
		int playerMenuPage = playerDataManager.getPlayerData(player).getPage(), nextEndIndex = displayedIslands.size() - playerMenuPage * 36, totalIslands = visitManager.getIslands().size();
		
		Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
		
		InventoryUtil inv = new InventoryUtil(configLoad.getString("Menu.Visit.Title"), null, 6);
		inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Visit.Item.Exit.Displayname"), null, null, null, null), 0, 8);
		inv.addItem(inv.createItem(new ItemStack(Material.HOPPER), configLoad.getString("Menu.Visit.Item.Type.Displayname"), configLoad.getStringList("Menu.Visit.Item.Type.Lore"), inv.createItemLoreVariable(new String[] { "%type#" + StringUtil.capatilizeUppercaseLetters(type.name()) }), null, null), 3);
		inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Visit.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Visit.Item.Statistics.Lore"), inv.createItemLoreVariable(new String[] { "%islands_open#" + NumberUtil.formatNumber(visitIslands.size()), "%islands_closed#" + NumberUtil.formatNumber(totalIslands - visitIslands.size()), "%islands#" + NumberUtil.formatNumber(totalIslands) }), null, null), 4);
		inv.addItem(inv.createItem(new ItemStack(Material.HOPPER), configLoad.getString("Menu.Visit.Item.Sort.Displayname"), configLoad.getStringList("Menu.Visit.Item.Sort.Lore"), inv.createItemLoreVariable(new String[] { "%sort#" + StringUtil.capatilizeUppercaseLetters(sort.name()) }), null, null), 5);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Visit.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);
		
		if (playerMenuPage != 1) {
			inv.addItem(inv.createItem(SkullUtil.create("ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=", "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="), configLoad.getString("Menu.Visit.Item.Previous.Displayname"), null, null, null, null), 1);
		}
		
		if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
			inv.addItem(inv.createItem(SkullUtil.create("wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=", "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), configLoad.getString("Menu.Visit.Item.Next.Displayname"), null, null, null, null), 7);
		}
		
		if (displayedIslands.size() == 0) {
			inv.addItem(inv.createItem(new ItemStack(Material.BARRIER), configLoad.getString("Menu.Visit.Item.Nothing.Displayname"), null, null, null, null), 31);
		} else {
			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
			
			int index = playerMenuPage * 36 - 36, endIndex = index >= displayedIslands.size() ? displayedIslands.size() - 1 : index + 36, inventorySlot = 17, playerCapacity = config.getFileConfiguration().getInt("Island.Visitor.Capacity");
			
			boolean voteEnabled = config.getFileConfiguration().getBoolean("Island.Visitor.Vote");
			boolean signatureEnabled = config.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable");
			
			for (; index < endIndex; index++) {
				if (displayedIslands.size() > index) {
					inventorySlot++;
					
					UUID targetPlayerUUID = (UUID) displayedIslands.keySet().toArray()[index];
					me.goodandevil.skyblock.visit.Visit visit = displayedIslands.get(targetPlayerUUID);
					
					Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerUUID);
					
					String targetPlayerName;
					String[] targetPlayerTexture;
					
					if (targetPlayer == null) {
						OfflinePlayer offlinePlayer = new OfflinePlayer(targetPlayerUUID);
						targetPlayerName = offlinePlayer.getName();
						targetPlayerTexture = offlinePlayer.getTexture();
					} else {
						targetPlayerName = targetPlayer.getName();
						targetPlayerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
					}
					
					int playersAtIsland = 0;
					Island island = null;
					
					if (islandManager.containsIsland(targetPlayerUUID)) {
						island = islandManager.getIsland(targetPlayerUUID);
						playersAtIsland = islandManager.getPlayersAtIsland(island).size();
					}
					
					List<String> itemLore = new ArrayList<>();
					
					if (voteEnabled) {
						String voteAction = "";
						
						if (visit.getVoters().contains(player.getUniqueId())) {
							voteAction = configLoad.getString("Menu.Visit.Item.Island.Vote.Enabled.Signature.Word.Remove");
						} else {
							voteAction = configLoad.getString("Menu.Visit.Item.Island.Vote.Enabled.Signature.Word.Add");
						}
						
						if (signatureEnabled) {
							List<String> correctItemLore;
							
							if (island != null && (island.isRole(Role.Member, player.getUniqueId()) || island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
								correctItemLore = configLoad.getStringList("Menu.Visit.Item.Island.Vote.Enabled.Signature.Enabled.Member.Lore");
							} else {
								correctItemLore = configLoad.getStringList("Menu.Visit.Item.Island.Vote.Enabled.Signature.Enabled.Visitor.Lore");
							}
							
							for (String itemLoreList : correctItemLore) {
								if (itemLoreList.contains("%signature")) {
									List<String> islandSignature = visit.getSiganture();
									
									if (islandSignature.size() == 0) {
										itemLore.add(configLoad.getString("Menu.Visit.Item.Island.Vote.Word.Empty"));
									} else {
										for (String signatureList : visit.getSiganture()) {
											itemLore.add(signatureList);
										}
									}
								} else {
									itemLore.add(itemLoreList);
								}
							}
						} else {
							if (island != null && (island.isRole(Role.Member, player.getUniqueId()) || island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
								itemLore.addAll(configLoad.getStringList("Menu.Visit.Item.Island.Vote.Enabled.Signature.Disabled.Member.Lore"));
							} else {
								itemLore.addAll(configLoad.getStringList("Menu.Visit.Item.Island.Vote.Enabled.Signature.Disabled.Visitor.Lore"));
							}
						}
						
						inv.addItem(inv.createItem(SkullUtil.create(targetPlayerTexture[0], targetPlayerTexture[1]), configLoad.getString("Menu.Visit.Item.Island.Displayname").replace("%player", targetPlayerName), itemLore, inv.createItemLoreVariable(new String[] { "%level#" + visit.getLevel().getLevel(), "%members#" + visit.getMembers(), "%votes#" + visit.getVoters().size(), "%visits#" + visit.getVisitors().size(), "%players#" + playersAtIsland, "%player_capacity#" + playerCapacity, "%action#" + voteAction }), null, null), inventorySlot);
					} else {
						if (signatureEnabled) {
							for (String itemLoreList : configLoad.getStringList("Menu.Visit.Item.Island.Vote.Disabled.Signature.Enabled.Lore")) {
								if (itemLoreList.contains("%signature")) {
									List<String> islandSignature = visit.getSiganture();
									
									if (islandSignature.size() == 0) {
										itemLore.add(configLoad.getString("Menu.Visit.Item.Island.Vote.Word.Empty"));
									} else {
										for (String signatureList : visit.getSiganture()) {
											itemLore.add(signatureList);
										}
									}
								} else {
									itemLore.add(itemLoreList);
								}
							}
						} else {
							itemLore.addAll(configLoad.getStringList("Menu.Visit.Item.Island.Vote.Disabled.Signature.Disabled.Lore"));
						}
						
						inv.addItem(inv.createItem(SkullUtil.create(targetPlayerTexture[0], targetPlayerTexture[1]), configLoad.getString("Menu.Visit.Item.Island.Displayname").replace("%player", targetPlayerName), itemLore, inv.createItemLoreVariable(new String[] { "%level#" + visit.getLevel().getLevel(), "%members#" + visit.getMembers(), "%visits#" + visit.getVisitors().size(), "%players#" + playersAtIsland, "%player_capacity#" + playerCapacity }), null, null), inventorySlot);
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
			SkyBlock skyblock = SkyBlock.getInstance();
			
			PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
			MessageManager messageManager = skyblock.getMessageManager();
			SoundManager soundManager = skyblock.getSoundManager();
			FileManager fileManager = skyblock.getFileManager();
			
			Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = languageConfig.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Title")))) {
				event.setCancelled(true);
				
				PlayerData playerData = playerDataManager.getPlayerData(player);
				
				if (playerData.getType() == null || playerData.getSort() == null) {
					playerData.setType(Visit.Type.Default);
					playerData.setSort(Visit.Sort.Default);
				}
				
				if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Item.Barrier.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Item.Exit.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
		    		player.closeInventory();
		    	} else if ((event.getCurrentItem().getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Item.Statistics.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.HOPPER) && (is.hasItemMeta())) {
		    		if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Item.Type.Displayname")))) {
			    		Visit.Type type = (Visit.Type) playerData.getType();
		    			
		    			if (type.ordinal()+1 == Visit.Type.values().length) {
		    				playerData.setType(Visit.Type.Default);
		    			} else {
		    				playerData.setType(Visit.Type.values()[type.ordinal() + 1]);
		    			}
		    		} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Item.Sort.Displayname")))) {
			    		Visit.Sort sort = (Visit.Sort) playerData.getSort();
		    			
		    			if (sort.ordinal()+1 == Visit.Sort.values().length) {
		    				playerData.setSort(Visit.Sort.Default);
		    			} else {
		    				playerData.setSort(Visit.Sort.values()[sort.ordinal() + 1]);
		    			}
	    			}
	    			
	    			open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
	    			soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Item.Nothing.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
		    		VisitManager visitManager = skyblock.getVisitManager();
		    		IslandManager islandManager = skyblock.getIslandManager();
		    		
		    		String targetPlayerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
		    		UUID targetPlayerUUID;
		    		
		    		Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerName);
		    		
		    		if (targetPlayer == null) {
		    			targetPlayerUUID = new OfflinePlayer(targetPlayerName).getUniqueId();
		    		} else {
		    			targetPlayerUUID = targetPlayer.getUniqueId();
		    		}
		    		
		    		if (visitManager.hasIsland(targetPlayerUUID)) {
		    			me.goodandevil.skyblock.visit.Visit visit = visitManager.getIsland(targetPlayerUUID);
		    			
		    			if (visit.isOpen()) {
		    				if (!islandManager.containsIsland(targetPlayerUUID)) {
		    					islandManager.loadIsland(targetPlayerUUID);
		    				}
		    				
		    				Island island = islandManager.getIsland(targetPlayerUUID);
		    				
				    		if ((!island.isRole(Role.Member, player.getUniqueId()) && !island.isRole(Role.Operator, player.getUniqueId()) && !island.isRole(Role.Owner, player.getUniqueId())) && fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
				    			if (event.getClick() == ClickType.RIGHT) {
				    				if (playerData.getIsland() != null && playerData.getIsland().equals(island.getOwnerUUID())) {
				    					List<UUID> islandVotes = visit.getVoters();
				    					
				    					if (islandVotes.contains(player.getUniqueId())) {
				    						visit.removeVoter(player.getUniqueId());
					    					
				    						messageManager.sendMessage(player, configLoad.getString("Island.Visit.Vote.Removed.Message").replace("%player", targetPlayerName));
					    					soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
				    					} else {
				    						visit.addVoter(player.getUniqueId());
					    					
				    						messageManager.sendMessage(player, configLoad.getString("Island.Visit.Vote.Added.Message").replace("%player", targetPlayerName));
					    					soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
				    					}
				    					
				    	    			open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
				    	    			soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
				    				} else {
				    					messageManager.sendMessage(player, configLoad.getString("Island.Visit.Vote.Island.Message"));
				    					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				    				}
				    				
				    				islandManager.unloadIsland(targetPlayerUUID);
				    				
				    				return;
				    			} else if (event.getClick() != ClickType.LEFT) {
				    				return;
				    			}
				    		}

		    				for (Location.World worldList : Location.World.values()) {
			    				if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
			    					messageManager.sendMessage(player, configLoad.getString("Island.Visit.Already.Message").replace("%player", targetPlayerName));
			    					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			    					
			    					return;
			    				}
		    				}
		    				
		    				islandManager.visitIsland(player, island);
		    				
							messageManager.sendMessage(player, configLoad.getString("Island.Visit.Teleported.Message").replace("%player", targetPlayerName));
							soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
		    			} else {
							messageManager.sendMessage(player, configLoad.getString("Island.Visit.Closed.Menu.Message").replace("%player", targetPlayerName));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
		    			}
		    			
		    			return;
		    		}
		    		
					messageManager.sendMessage(player, configLoad.getString("Island.Visit.Exist.Message").replace("%player", targetPlayerName));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
		    	}
			}
		}
	}
	
	public enum Type {
		
		Default,
		Solo,
		Team;
		
	}
	
	public enum Sort {
		
		Default,
		Players,
		Level,
		Members,
		Visits,
		Votes;
		
	}
}
