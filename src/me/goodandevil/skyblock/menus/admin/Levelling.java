package me.goodandevil.skyblock.menus.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.AnvilGUI;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.item.MaterialUtil;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Levelling implements Listener {

    private static Levelling instance;

    public static Levelling getInstance(){
        if(instance == null) {
            instance = new Levelling();
        }
        
        return instance;
    }
	
	@SuppressWarnings("deprecation")
	public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
    	LevellingManager levellingManager = plugin.getLevellingManager();
    	FileManager fileManager = plugin.getFileManager();
    	
    	PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
    	
    	List<me.goodandevil.skyblock.levelling.Material> levellingMaterials = levellingManager.getMaterials();
    	
    	Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
    	
    	InventoryUtil inv = new InventoryUtil(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Title")), null, 6);
		inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Admin.Levelling.Item.Exit.Displayname"), null, null, null, null), 0, 8);
		inv.addItem(inv.createItem(new ItemStack(org.bukkit.Material.SIGN), configLoad.getString("Menu.Admin.Levelling.Item.Information.Displayname"), configLoad.getStringList("Menu.Admin.Levelling.Item.Information.Lore"), inv.createItemLoreVariable(new String[] { "%materials#" + levellingMaterials.size(), "%division#" + fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Levelling.Division") }), null, null), 4);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Admin.Levelling.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);
		
		int playerMenuPage = playerData.getPage(), nextEndIndex = levellingMaterials.size() - playerMenuPage * 36;
		
		if (playerMenuPage != 1) {
			inv.addItem(inv.createItem(SkullUtil.create("ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=", "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="), configLoad.getString("Menu.Admin.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
		}
		
		if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
			inv.addItem(inv.createItem(SkullUtil.create("wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=", "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="), configLoad.getString("Menu.Admin.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
		}
		
		if (levellingMaterials.size() == 0) {
			inv.addItem(inv.createItem(new ItemStack(Material.BARRIER), configLoad.getString("Menu.Admin.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
		} else {
			int index = playerMenuPage * 36 - 36, endIndex = index >= levellingMaterials.size() ? levellingMaterials.size() - 1 : index + 36, inventorySlot = 17;
			
			for (; index < endIndex; index++) {
				if (levellingMaterials.size() > index) {
					inventorySlot++;
					
					me.goodandevil.skyblock.levelling.Material material = levellingMaterials.get(index);
					inv.addItem(inv.createItem(new ItemStack(MaterialUtil.correctMaterial(material.getItemStack().getType()), 1, material.getItemStack().getDurability()), ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Material.Displayname").replace("%material", material.getMaterials().name())), configLoad.getStringList("Menu.Admin.Levelling.Item.Material.Lore"), inv.createItemLoreVariable(new String[] { "%points#" + NumberUtil.formatNumber(material.getPoints()) }), null, null), inventorySlot);
				}
			}	
		}
		
    	player.openInventory(inv.getInventory());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			
			LevellingManager levellingManager = plugin.getLevellingManager();
			MessageManager messageManager = plugin.getMessageManager();
			SoundManager soundManager = plugin.getSoundManager();
			FileManager fileManager = plugin.getFileManager();
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Title")))) {
				PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
				
				if (!(player.hasPermission("skyblock.admin.level") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*"))) {
					messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Permission.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
				
				if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Barrier.Displayname"))))) {
					event.setCancelled(true);
					soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    		
		    		return;
				} else if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Exit.Displayname"))))) {
					event.setCancelled(true);
					soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
		    		player.closeInventory();
		    	
		    		return;
		    	} else if ((event.getCurrentItem().getType() == Material.SIGN) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Information.Displayname"))))) {
		    		event.setCancelled(true);
		    		soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
	    			
					AnvilGUI gui = new AnvilGUI(player, event1 -> {
					    if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
							if (!(player.hasPermission("skyblock.admin.level") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*"))) {
								messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Permission.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							} else if (event1.getName().matches("[0-9]+")) {
								int pointDivision = Integer.valueOf(event1.getName());
								
								messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Division.Message").replace("%division", NumberUtil.formatNumber(pointDivision)));
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
								
								Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
									@Override
									public void run() {
										Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
										FileConfiguration configLoad = config.getFileConfiguration();
										
										configLoad.set("Island.Levelling.Division", pointDivision);
											
										try {
											configLoad.save(config.getFile());
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
									
								Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
									@Override
									public void run() {
										open(player);
									}
								}, 3L);
							} else {
								messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Numerical.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							}
							
					        event1.setWillClose(true);
					        event1.setWillDestroy(true);
					    } else {
					        event1.setWillClose(false);
					        event1.setWillDestroy(false);
					    }
					});
		    		
		            is = new ItemStack(Material.NAME_TAG);
		            ItemMeta im = is.getItemMeta();
		            im.setDisplayName(configLoad.getString("Menu.Admin.Levelling.Item.Information.Word.Enter"));
		            is.setItemMeta(im);
		            
		            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
		            gui.open();
		    	
		    		return;
		    	} else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Nothing.Displayname"))))) {
		    		event.setCancelled(true);
		    		soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		    		
		    		return;
		    	} else if ((event.getCurrentItem().getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
		    		if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Previous.Displayname")))) {
		    			event.setCancelled(true);
		    			playerData.setPage(playerData.getPage() - 1);
		    			open(player);
		    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
		    		
		    			return;
		    		} else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Item.Next.Displayname")))) {
		    			event.setCancelled(true);
		    			playerData.setPage(playerData.getPage() + 1);
		    			open(player);
		    			soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
		    		
		    			return;
		    		}
		    	}
				
				if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
					for (me.goodandevil.skyblock.levelling.Material materialList : levellingManager.getMaterials()) {
						Materials materials = materialList.getMaterials();
						
						if (event.getCurrentItem().getType() == MaterialUtil.correctMaterial(materials.parseMaterial()) && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(materials.name())) {
							event.setCancelled(true);
							
							if (event.getClick() == ClickType.LEFT) {
								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
				    			
								AnvilGUI gui = new AnvilGUI(player, event1 -> {
								    if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
										if (!(player.hasPermission("skyblock.admin.level") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*"))) {
											messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Permission.Message"));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										} else if (levellingManager.containsMaterials(materials)) {
											if (event1.getName().matches("[0-9]+")) {
												int materialPoints = Integer.valueOf(event1.getName());
												materialList.setPoints(materialPoints);
												
												messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Points.Message").replace("%material", materials.name()).replace("%points", NumberUtil.formatNumber(materialPoints)));
												soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
												
												Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
													@Override
													public void run() {
														open(player);
													}
												}, 3L);
												
												Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
													@Override
													public void run() {
														Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
														FileConfiguration configLoad = config.getFileConfiguration();
														
														configLoad.set("Materials." + materials.name() + ".Points", materialPoints);
														
														try {
															configLoad.save(config.getFile());
														} catch (IOException e) {
															e.printStackTrace();
														}
													}
												});
											} else {
												messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Numerical.Message"));
												soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
											}
										} else {
											messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Exist.Message"));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										}
										
								        event1.setWillClose(true);
								        event1.setWillDestroy(true);
								    } else {
								        event1.setWillClose(false);
								        event1.setWillDestroy(false);
								    }
								});
					    		
					            is = new ItemStack(Material.NAME_TAG);
					            ItemMeta im = is.getItemMeta();
					            im.setDisplayName(configLoad.getString("Menu.Admin.Levelling.Item.Material.Word.Enter"));
					            is.setItemMeta(im);
					            
					            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
					            gui.open();
							} else if (event.getClick() == ClickType.RIGHT) {
								levellingManager.removeMaterial(materialList);
								open(player);
								
								messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Removed.Message").replace("%material", materials.name()));
								soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
								
								Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
									@Override
									public void run() {
										Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
										FileConfiguration configLoad = config.getFileConfiguration();
										
										configLoad.set("Materials." + materials.name(), null);
										
										try {
											configLoad.save(config.getFile());
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
							}
							
							return;
						}
					}
				}
				
				event.setCancelled(true);
				
				Materials materials;
				
				if (NMSUtil.getVersionNumber() < 13) {
					materials = Materials.requestMaterials(event.getCurrentItem().getType().name(), (byte) event.getCurrentItem().getDurability());
				} else {
					materials = Materials.fromString(event.getCurrentItem().getType().name());
				}
				
				if (levellingManager.containsMaterials(materials)) {
					messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Already.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
				
				levellingManager.addMaterial(materials, 0);
				open(player);
				
				messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Added.Message").replace("%material", materials.name()));
				soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
				
				Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
						FileConfiguration configLoad = config.getFileConfiguration();
						
						configLoad.set("Materials." + materials.name() + ".Points", 0);
						
						try {
							configLoad.save(config.getFile());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
}
