package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Message;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.AnvilGUI;
import me.goodandevil.skyblock.utils.item.nInventoryUtil;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEvent;
import me.goodandevil.skyblock.utils.item.nInventoryUtil.ClickEventHandler;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.Visit;

public class Settings {

    private static Settings instance;

    public static Settings getInstance(){
        if(instance == null) {
            instance = new Settings();
        }
        
        return instance;
    }
    
    public void open(Player player, Settings.Type menuType, me.goodandevil.skyblock.island.Settings.Role role, Settings.Panel panel) {
    	SkyBlock skyblock = SkyBlock.getInstance();
    	
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
    	
		if (playerDataManager.hasPlayerData(player)) {
	    	Island island = skyblock.getIslandManager().getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
	    	
			Config mainConfig = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
			FileConfiguration configLoad = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();
	    	
	    	if (menuType == Settings.Type.Categories) {
		    	nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (playerDataManager.hasPlayerData(player)) {
							Island island;
							
							if (islandManager.hasIsland(player)) {
								island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
								
								if (!(island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								return;
							}
							
							ItemStack is = event.getItem();
							
					    	if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))))) {
					    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
					    	} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Visitor.Displayname"))))) {
								if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Visitor").getStatus()) {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
									
									event.setWillClose(false);
									event.setWillDestroy(false);
									
									return;
								}
								
					    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					    	
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
									}
					    		}, 1L);
					    	} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Member.Displayname"))))) {
								if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Member").getStatus()) {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
									
									event.setWillClose(false);
									event.setWillDestroy(false);
									
									return;
								}
					    		
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					    		
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Member, null);
									}
					    		}, 1L);
					    	} else if ((is.getType() == Material.ITEM_FRAME) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Operator.Displayname"))))) {
								if (island.isRole(Role.Operator, player.getUniqueId())) {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
									
									event.setWillClose(false);
									event.setWillDestroy(false);
									
									return;
								}
					    		
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					    		
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Operator, null);
									}
					    		}, 1L);
					    	} else if ((is.getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"))))) {
					    		if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Island").getStatus()) {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Permission.Access.Message"));
									soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
									
									event.setWillClose(false);
									event.setWillDestroy(false);
									
									return;
					    		}
					    		
					    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					    		
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
							    		open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Owner, null);
									}
					    		}, 1L);
					    	}
						}
					}
		    	});
		    	
		    	nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"), null, null, null, null), 0, 8);
	    		nInv.addItem(nInv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Settings.Categories.Item.Visitor.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
	    		nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Categories.Item.Member.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
	    		nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME), configLoad.getString("Menu.Settings.Categories.Item.Operator.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Operator.Lore"), null, null, null), 4);
	    		nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(), configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Owner.Lore"), null, null, null), 6);
	    		
		    	nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Title")));
		    	nInv.setRows(1);
		    	nInv.open();
	    	} else if (menuType == Settings.Type.Role && role != null) {
		    	nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (playerDataManager.hasPlayerData(player)) {
							Island island;
							
							if (islandManager.hasIsland(player)) {
								island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
								
								if (!(island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								return;
							}
							
							ItemStack is = event.getItem();
							
							if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + role.name() + ".Item.Return.Displayname"))))) {
					    		soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
					    		
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player, Settings.Type.Categories, null, null);
									}
					    		}, 1L);
					    	} else if ((is.getType() == Material.PAPER) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"))))) {
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
								
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
							    		open(player, Settings.Type.Panel, null, Settings.Panel.Signature);
									}
					    		}, 1L);
					    	} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"))))) {
								soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
								
					    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
							    		open(player, Settings.Type.Panel, null, Settings.Panel.Welcome);
									}
					    		}, 1L);
					    	} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
								if (island.isOpen()) {
									islandManager.closeIsland(island);
									soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
								} else {
									island.setOpen(true);
									soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
								}
								
								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
									}
								}, 1L);
							} else if (is.hasItemMeta()) {
								String roleName = getRoleName(role);
								
								for (String settingList : island.getSettings(role).keySet()) {
									if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + settingList + ".Displayname")))) {
										if (!hasPermission(island, player, role)) {
											messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Permission.Change.Message"));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
											
											return;
										}
										
										me.goodandevil.skyblock.island.Settings setting = island.getSettings(role).get(settingList);
										
										if (setting != null) {
											if (setting.getStatus()) {
												setting.setStatus(false);
											} else {
												setting.setStatus(true);
											}
										}
										
										break;
									}
								}
								
								soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
								
								Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										open(player, Settings.Type.Role, role, null);
									}
								}, 1L);
							}
						}
					}
		    	});
	    		
	    		if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor || role == me.goodandevil.skyblock.island.Settings.Role.Member) {
	    			if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor) {
	    				Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
	    				Visit visit = island.getVisit();
	    				
	    				if (config.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
	        				nInv.addItem(nInv.createItem(new ItemStack(Material.PAPER), configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Signature.Lore"), null, null, null), 3);
	    				}
	    				
	    				if (config.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
	    					if (visit.isOpen()) {
	    						nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Open.Lore"), nInv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%votes#" + visit.getVoters().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
	    					} else {
	    						nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Closed.Lore"), nInv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%votes#" + visit.getVoters().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
	    					}
	    				} else {
	    					if (visit.isOpen()) {
	    						nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Open.Lore"), nInv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
	    					} else {
	    						nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Closed.Lore"), nInv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
	    					}
	    				}
	    				
	    				if (config.getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
	    					nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Welcome.Lore"), null, null, null), 5);
	    				}
	    			}
	    			
	    			nInv.addItemStack(createItem(island, role, "Destroy", Material.DIAMOND_PICKAXE), 9);
	    			nInv.addItemStack(createItem(island, role, "Place", Material.GRASS), 10);
	    			nInv.addItemStack(createItem(island, role, "Anvil", Material.ANVIL), 11);
	    			nInv.addItemStack(createItem(island, role, "ArmorStand", Material.ARMOR_STAND), 12);
	    			nInv.addItemStack(createItem(island, role, "Beacon", Material.BEACON), 13);
	    			nInv.addItemStack(createItem(island, role, "Bed", Materials.WHITE_BED.parseMaterial()), 14);
	    			nInv.addItemStack(createItem(island, role, "AnimalBreeding", Material.WHEAT), 15);
	    			nInv.addItemStack(createItem(island, role, "Brewing", Materials.LEGACY_BREWING_STAND.getPostMaterial()), 16);
	    			nInv.addItemStack(createItem(island, role, "Bucket", Material.BUCKET), 17);
	    			nInv.addItemStack(createItem(island, role, "WaterCollection", Material.POTION), 18);
	    			nInv.addItemStack(createItem(island, role, "Storage", Material.CHEST), 19);
	    			nInv.addItemStack(createItem(island, role, "Workbench", Materials.CRAFTING_TABLE.parseMaterial()), 20);
	    			nInv.addItemStack(createItem(island, role, "Crop", Materials.WHEAT_SEEDS.parseMaterial()), 21);
	    			nInv.addItemStack(createItem(island, role, "Door", Materials.OAK_DOOR.parseMaterial()), 22);
	    			nInv.addItemStack(createItem(island, role, "Gate", Materials.OAK_FENCE_GATE.parseMaterial()), 23);
	    			nInv.addItemStack(createItem(island, role, "Projectile", Material.ARROW), 24);
	    			nInv.addItemStack(createItem(island, role, "Enchant", Materials.ENCHANTING_TABLE.parseMaterial()), 25);
	    			nInv.addItemStack(createItem(island, role, "Fire", Material.FLINT_AND_STEEL), 26);
	    			nInv.addItemStack(createItem(island, role, "Furnace", Material.FURNACE), 27);
	    			nInv.addItemStack(createItem(island, role, "HorseInventory", Materials.CHEST_MINECART.parseMaterial()), 28);
	    			nInv.addItemStack(createItem(island, role, "MobRiding", Material.SADDLE), 29);
	    			nInv.addItemStack(createItem(island, role, "MobHurting", Materials.WOODEN_SWORD.parseMaterial()), 30);
	    			nInv.addItemStack(createItem(island, role, "MobTaming", Materials.POPPY.parseMaterial()), 31);
	    			nInv.addItemStack(createItem(island, role, "Leash", Materials.LEAD.parseMaterial()), 32);
	    			nInv.addItemStack(createItem(island, role, "LeverButton", Material.LEVER), 33);
	    			nInv.addItemStack(createItem(island, role, "Milking", Material.MILK_BUCKET), 34);
	    			nInv.addItemStack(createItem(island, role, "Jukebox", Material.JUKEBOX), 35);
	    			nInv.addItemStack(createItem(island, role, "PressurePlate", Materials.OAK_PRESSURE_PLATE.parseMaterial()), 37);
	    			nInv.addItemStack(createItem(island, role, "Redstone", Material.REDSTONE), 38);
	    			nInv.addItemStack(createItem(island, role, "Shearing", Material.SHEARS), 39);
	    			nInv.addItemStack(createItem(island, role, "Trading", Material.EMERALD), 40);
	    			nInv.addItemStack(createItem(island, role, "ItemDrop", Material.PUMPKIN_SEEDS), 41);
	    			nInv.addItemStack(createItem(island, role, "ItemPickup", Material.MELON_SEEDS), 42);
	    			nInv.addItemStack(createItem(island, role, "Fishing", Material.FISHING_ROD), 43);
	    			nInv.addItemStack(createItem(island, role, "DropperDispenser", Material.DISPENSER), 46);
	    			nInv.addItemStack(createItem(island, role, "SpawnEgg", Material.EGG), 47);
	    			nInv.addItemStack(createItem(island, role, "Cake", Material.CAKE), 48);
	    			nInv.addItemStack(createItem(island, role, "DragonEggUse", Material.DRAGON_EGG), 49);
	    			nInv.addItemStack(createItem(island, role, "MinecartBoat", Material.MINECART), 50);
	    			nInv.addItemStack(createItem(island, role, "Portal", Material.ENDER_PEARL), 51);
	    			nInv.addItemStack(createItem(island, role, "Hopper", Material.HOPPER), 52);
	    			
	    			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + role.name() + ".Title")));
	    			nInv.setRows(6);
	    		} else if (role == me.goodandevil.skyblock.island.Settings.Role.Operator) {
	    			if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
	    				nInv.addItemStack(createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseMaterial()), 10);
	    				nInv.addItemStack(createItem(island, role, "Kick", Material.IRON_DOOR), 11);
	    				nInv.addItemStack(createItem(island, role, "Ban", Material.IRON_AXE), 12);
	    				nInv.addItemStack(createItem(island, role, "Unban", Material.NAME_TAG), 13);
	    				nInv.addItemStack(createItem(island, role, "Visitor", Material.SIGN), 14);
	    				nInv.addItemStack(createItem(island, role, "Member", Material.PAINTING), 15);
	    				nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseMaterial()), 16);
	    				nInv.addItemStack(createItem(island, role, "MainSpawn", Material.EMERALD), 20);
	    				nInv.addItemStack(createItem(island, role, "VisitorSpawn", Material.NETHER_STAR), 21);
	    				nInv.addItemStack(createItem(island, role, "Biome", Material.MAP), 23);
	        			nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseMaterial()), 24);
	    			
		    			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + role.name() + ".Title")));
		    			nInv.setRows(3);
	    			} else {
	    				nInv.addItemStack(createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseMaterial()), 9);
	    				nInv.addItemStack(createItem(island, role, "Kick", Material.IRON_DOOR), 10);
	    				nInv.addItemStack(createItem(island, role, "Visitor", Material.SIGN), 11);
	    				nInv.addItemStack(createItem(island, role, "Member", Material.PAINTING), 12);
	    				nInv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseMaterial()), 13);
	    				nInv.addItemStack(createItem(island, role, "MainSpawn", Material.EMERALD), 14);
	    				nInv.addItemStack(createItem(island, role, "VisitorSpawn", Material.NETHER_STAR), 15);
	    				nInv.addItemStack(createItem(island, role, "Biome", Material.MAP), 16);
	    				nInv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseMaterial()), 17);
	    			
	        			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + role.name() + ".Title")));
	        			nInv.setRows(2);
	    			}
	    		} else if (role == me.goodandevil.skyblock.island.Settings.Role.Owner) {
	    			nInv.addItemStack(createItem(island, role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseMaterial()), 10);
	    			nInv.addItemStack(createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseMaterial()), 11);
	    			nInv.addItemStack(createItem(island, role, "PvP", Material.DIAMOND_SWORD), 12);
	    			nInv.addItemStack(createItem(island, role, "Explosions", Materials.GUNPOWDER.parseMaterial()), 13);
	    			nInv.addItemStack(createItem(island, role, "FireSpread", Material.FLINT_AND_STEEL), 14);
	    			nInv.addItemStack(createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseMaterial()), 15);
	    			nInv.addItemStack(createItem(island, role, "KeepItemsOnDeath", Material.ITEM_FRAME), 16);
	    			
	    			nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + role.name() + ".Title")));
	    			nInv.setRows(2);
	    		}
	    		
				nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings." + role.name() + ".Item.Return.Displayname"), null, null, null, null), 0, 8);
				nInv.open();
	    	} else if (menuType == Settings.Type.Panel) {
	    		if (panel == Settings.Panel.Welcome) {
			    	nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (playerDataManager.hasPlayerData(player)) {
								Island island;
								
								if (islandManager.hasIsland(player)) {
									island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
									
									if (!(island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
										messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										
										return;
									}
								} else {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
								
								if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
									messageManager.sendMessage(player, configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
								
								ItemStack is = event.getItem();
								
								if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))))) {
						    		soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
						    		
						    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
										}
						    		}, 1L);
								} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
									if (island.isOpen()) {
										islandManager.closeIsland(island);
										soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
									} else {
										island.setOpen(true);
										soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
									}
									
						    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
										}
						    		}, 1L);
								} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"))))) {
									soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
									
									event.setWillClose(false);
									event.setWillDestroy(false);
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"))))) {
						    		if (island.getMessage(Message.Welcome).size() >= skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
						    			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						    			
						    			event.setWillClose(false);
						    			event.setWillDestroy(false);
						    		} else {
						    			soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
						    			
										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
											@Override
											public void run() {
												AnvilGUI gui = new AnvilGUI(player, event1 -> {
												    if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												    	Island island1;
												    	
														if (islandManager.hasIsland(player)) {
															island1 = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
															
															if (!(island1.isRole(Role.Operator, player.getUniqueId()) || island1.isRole(Role.Owner, player.getUniqueId()))) {
																messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
																soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
																player.closeInventory();
																
																event1.setWillClose(true);
														        event1.setWillDestroy(true);
																
																return;
															} else if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
																messageManager.sendMessage(player, configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message"));
																soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
																
																event1.setWillClose(true);
														        event1.setWillDestroy(true);
																
																return;
															}
														} else {
															messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
															soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
															player.closeInventory();
															
															event1.setWillClose(true);
													        event1.setWillDestroy(true);
															
															return;
														}
														
														Config config1 = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
														FileConfiguration configLoad1 = config1.getFileConfiguration();
														
														if (island1.getMessage(Message.Welcome).size() > configLoad1.getInt("Island.Visitor.Welcome.Lines") || event1.getName().length() > configLoad1.getInt("Island.Visitor.Welcome.Length")) {
															soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
														} else {
															List<String> welcomeMessage = island1.getMessage(Message.Welcome);
															welcomeMessage.add(event1.getName());
															island1.setMessage(Message.Welcome, player.getName(), welcomeMessage);
															soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
														}
														
														Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
															@Override
															public void run() {
																open(player, Settings.Type.Panel, null, Settings.Panel.Welcome);
															}
														}, 1L);
														
												        event1.setWillClose(true);
												        event1.setWillDestroy(true);
												    } else {
												        event1.setWillClose(false);
												        event1.setWillDestroy(false);
												    }
												});
									    		
									            ItemStack is = new ItemStack(Material.NAME_TAG);
									            ItemMeta im = is.getItemMeta();
									            im.setDisplayName(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Word.Enter"));
									            is.setItemMeta(im);
									            
									            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
									            gui.open();
											}
										}, 1L);
						    		}
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"))))) {
									List<String> welcomeMessage = island.getMessage(Message.Welcome);
									
									if (welcomeMessage.size() == 0) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										welcomeMessage.remove(welcomeMessage.size() - 1);
										island.setMessage(Message.Welcome, island.getMessageAuthor(Message.Welcome), welcomeMessage);
										soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
										
										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
											@Override
											public void run() {
												open(player, Settings.Type.Panel, null, Settings.Panel.Welcome);
											}
										}, 1L);
									}
								}
							}
						}
			    	});
			    	
	    			List<String> welcomeMessage = island.getMessage(Message.Welcome);
	    			
	    			if (welcomeMessage.size() == mainConfig.getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Limit.Lore"), null, null, null), 1);
	    			} else {
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.More.Lore"), null, null, null), 1);
	    			}
	    			
	    			if (welcomeMessage.size() == 0) {
	    				List<String> itemLore = new ArrayList<>();
	    				itemLore.add(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Word.Empty"));
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"), itemLore, null, null, null), 2);
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.None.Lore"), null, null, null), 3);
	    			} else {
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.SIGN, welcomeMessage.size()), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"), welcomeMessage, null, null, null), 2);
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Lines.Lore"), null, null, null), 3);
	    			}
	    			
	    			nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"), null, null, null, null), 0, 4);
			    	
			    	nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title")));
			    	nInv.setType(InventoryType.HOPPER);
			    	nInv.open();
	    		} else if (panel == Settings.Panel.Signature) {
	    			nInventoryUtil nInv = new nInventoryUtil(player, new ClickEventHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (playerDataManager.hasPlayerData(player)) {
								Island island;
								
								if (islandManager.hasIsland(player)) {
									island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
									
									if (!(island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
										messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										
										return;
									}
								} else {
									messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
								
								if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
									messageManager.sendMessage(player, configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
								
								ItemStack is = event.getItem();
								
								if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"))))) {
						    		soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
						    		
						    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
										}
						    		}, 1L);
								} else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
									if (island.isOpen()) {
										islandManager.closeIsland(island);
										soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
									} else {
										island.setOpen(true);
										soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
									}
									
						    		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
										@Override
										public void run() {
											open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
										}
						    		}, 1L);
								} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"))))) {
									soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
									
									event.setWillClose(false);
									event.setWillDestroy(false);
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"))))) {
						    		if (island.getMessage(Message.Signature).size() >= skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Visitor.Signature.Lines")) {
						    			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						    			
						    			event.setWillClose(false);
						    			event.setWillDestroy(false);
						    		} else {
						    			soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
						    			
										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
											@Override
											public void run() {
												AnvilGUI gui = new AnvilGUI(player, event1 -> {
												    if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												    	Island island1;
												    	
														if (islandManager.hasIsland(player)) {
															island1 = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
															
															if (!(island1.isRole(Role.Operator, player.getUniqueId()) || island1.isRole(Role.Owner, player.getUniqueId()))) {
																messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Role.Message"));
																soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
																player.closeInventory();
																
																event1.setWillClose(true);
														        event1.setWillDestroy(true);
																
																return;
															} else if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
																messageManager.sendMessage(player, configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message"));
																soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
																
																event1.setWillClose(true);
														        event1.setWillDestroy(true);
																
																return;
															}
														} else {
															messageManager.sendMessage(player, configLoad.getString("Command.Island.Settings.Owner.Message"));
															soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
															player.closeInventory();
															
															event1.setWillClose(true);
													        event1.setWillDestroy(true);
															
															return;
														}
														
														Config config1 = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
														FileConfiguration configLoad1 = config1.getFileConfiguration();
														
														if (island1.getMessage(Message.Signature).size() > configLoad1.getInt("Island.Visitor.Signature.Lines") || event1.getName().length() > configLoad1.getInt("Island.Visitor.Signature.Length")) {
															soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
														} else {
															List<String> signatureMessage = island1.getMessage(Message.Signature);
															signatureMessage.add(event1.getName());
															island1.setMessage(Message.Signature, player.getName(), signatureMessage);
															soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
														}
														
														Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
															@Override
															public void run() {
																open(player, Settings.Type.Panel, null, Settings.Panel.Signature);
															}
														}, 1L);
														
												        event1.setWillClose(true);
												        event1.setWillDestroy(true);
												    } else {
												        event1.setWillClose(false);
												        event1.setWillDestroy(false);
												    }
												});
									    		
									            ItemStack is = new ItemStack(Material.NAME_TAG);
									            ItemMeta im = is.getItemMeta();
									            im.setDisplayName(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Word.Enter"));
									            is.setItemMeta(im);
									            
									            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
									            gui.open();
											}
										}, 1L);
						    		}
								} else if ((is.getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"))))) {
									List<String> signatureMessage = island.getMessage(Message.Signature);
									
									if (signatureMessage.size() == 0) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
										event.setWillClose(false);
										event.setWillDestroy(false);
									} else {
										signatureMessage.remove(signatureMessage.size() - 1);
										island.setMessage(Message.Signature, island.getMessageAuthor(Message.Signature), signatureMessage);
										soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
										
										Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(skyblock, new Runnable() {
											@Override
											public void run() {
												open(player, Settings.Type.Panel, null, Settings.Panel.Signature);
											}
										}, 1L);
									}
								}
							}
						}
			    	});
			    	
	    			List<String> signatureMessage = island.getMessage(Message.Signature);
	    			
	    			if (signatureMessage.size() == mainConfig.getFileConfiguration().getInt("Island.Visitor.Signature.Lines")) {
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Limit.Lore"), null, null, null), 1);
	    			} else {
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.More.Lore"), null, null, null), 1);
	    			}
	    			
	    			if (signatureMessage.size() == 0) {
	    				List<String> itemLore = new ArrayList<>();
	    				itemLore.add(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Word.Empty"));
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"), itemLore, null, null, null), 2);
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.None.Lore"), null, null, null), 3);
	    			} else {
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.SIGN, signatureMessage.size()), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"), signatureMessage, null, null, null), 2);
	    				nInv.addItem(nInv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Lines.Lore"), null, null, null), 3);
	    			}
	    			
	    			nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"), null, null, null, null), 0, 4);
			    	
			    	nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")));
			    	nInv.setType(InventoryType.HOPPER);
			    	nInv.open();
	    		}
	    	}
		}
    }
    
    private ItemStack createItem(Island island, me.goodandevil.skyblock.island.Settings.Role role, String setting, Material material) {
		SkyBlock skyblock = SkyBlock.getInstance();
    	
    	Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
    	
		List<String> itemLore = new ArrayList<>();
		
    	ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		
		String roleName = role.name();
		
		if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor || role == me.goodandevil.skyblock.island.Settings.Role.Member) {
			roleName = "Default";
		}
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));
		
		if (island.getSetting(role, setting).getStatus()) {
			for (String itemLoreList : configLoad.getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		} else {
			for (String itemLoreList : configLoad.getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		}
		
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.setLore(itemLore);
		is.setItemMeta(im);
		
		return is;
    }
    
	private String getRoleName(me.goodandevil.skyblock.island.Settings.Role role) {
		if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor || role == me.goodandevil.skyblock.island.Settings.Role.Member) {
			return "Default";
		}
		
		return role.name();
	}
	
	private boolean hasPermission(Island island, Player player, me.goodandevil.skyblock.island.Settings.Role role) {
		if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor || role == me.goodandevil.skyblock.island.Settings.Role.Member || role == me.goodandevil.skyblock.island.Settings.Role.Owner) {
			String roleName = role.name();
			
			if (role == me.goodandevil.skyblock.island.Settings.Role.Owner) {
				roleName = "Island";
			}
			
			if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, roleName).getStatus()) {
				return false;
			}
		} else if (role == me.goodandevil.skyblock.island.Settings.Role.Operator) {
			if (!island.isRole(Role.Owner, player.getUniqueId())) {
				return false;
			}
		}
		
		return true;
	}
	
	public enum Panel {
		
		Welcome,
		Signature;
		
	}
	
	public enum Type {
		
		Categories,
		Panel,
		Role;
		
	}
}
