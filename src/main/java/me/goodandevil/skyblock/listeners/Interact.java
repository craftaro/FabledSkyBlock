package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.utils.structure.StructureUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Interact implements Listener {
	
	private final SkyBlock skyblock;
	
 	public Interact(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (event.getClickedBlock().getType() == Material.ANVIL) {
					if (!islandManager.hasPermission(player, "Anvil")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.BEACON) {
					if (!islandManager.hasPermission(player, "Beacon")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Materials.LEGACY_BED_BLOCK.parseMaterial() || event.getClickedBlock().getType() == Materials.WHITE_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.ORANGE_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.MAGENTA_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.LIGHT_BLUE_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.YELLOW_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.LIME_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.PINK_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.GRAY_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.LIGHT_GRAY_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.CYAN_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.CYAN_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.PURPLE_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.BLUE_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.BROWN_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.GREEN_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.RED_BED.parseMaterial() || event.getClickedBlock().getType() == Materials.BLACK_BED.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Bed")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.BREWING_STAND) {
					if (!islandManager.hasPermission(player, "Brewing")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST || (NMSUtil.getVersionNumber() > 9 && (event.getClickedBlock().getType() == Materials.BLACK_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.BLUE_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.BROWN_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.CYAN_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.GRAY_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.GREEN_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.LIGHT_BLUE_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.LIGHT_GRAY_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.LIME_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.MAGENTA_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.ORANGE_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.PINK_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.PURPLE_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.RED_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.WHITE_SHULKER_BOX.parseMaterial() || event.getClickedBlock().getType() == Materials.YELLOW_SHULKER_BOX.parseMaterial()))) {
					if (!islandManager.hasPermission(player, "Storage")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Materials.CRAFTING_TABLE.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Workbench")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.BIRCH_DOOR || event.getClickedBlock().getType() == Material.ACACIA_DOOR || event.getClickedBlock().getType() == Material.DARK_OAK_DOOR || event.getClickedBlock().getType() == Material.JUNGLE_DOOR || event.getClickedBlock().getType() == Material.SPRUCE_DOOR || event.getClickedBlock().getType() == Materials.LEGACY_WOODEN_DOOR.parseMaterial() || event.getClickedBlock().getType() == Materials.OAK_DOOR.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Door")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Materials.ENCHANTING_TABLE.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Enchant")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.FURNACE || event.getClickedBlock().getType() == Materials.LEGACY_BURNING_FURNACE.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Furnace")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.STONE_BUTTON || event.getClickedBlock().getType() == Materials.OAK_BUTTON.parseMaterial() || event.getClickedBlock().getType() == Materials.SPRUCE_BUTTON.parseMaterial() || event.getClickedBlock().getType() == Materials.BIRCH_BUTTON.parseMaterial() || event.getClickedBlock().getType() == Materials.JUNGLE_BUTTON.parseMaterial() || event.getClickedBlock().getType() == Materials.ACACIA_BUTTON.parseMaterial() || event.getClickedBlock().getType() == Materials.DARK_OAK_BUTTON.parseMaterial()) {
					if (!islandManager.hasPermission(player, "LeverButton")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.JUKEBOX) {
					if (!islandManager.hasPermission(player, "Jukebox")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Materials.OAK_TRAPDOOR.parseMaterial() || event.getClickedBlock().getType() == Materials.SPRUCE_TRAPDOOR.parseMaterial() || event.getClickedBlock().getType() == Materials.BIRCH_TRAPDOOR.parseMaterial() || event.getClickedBlock().getType() == Materials.JUNGLE_TRAPDOOR.parseMaterial() || event.getClickedBlock().getType() == Materials.ACACIA_TRAPDOOR.parseMaterial() || event.getClickedBlock().getType() == Materials.DARK_OAK_TRAPDOOR.parseMaterial() || event.getClickedBlock().getType() == Material.NOTE_BLOCK || event.getClickedBlock().getType() == Material.HOPPER || event.getClickedBlock().getType() == Materials.COMPARATOR.parseMaterial() || event.getClickedBlock().getType() == Materials.LEGACY_REDSTONE_COMPARATOR_OFF.parseMaterial() || event.getClickedBlock().getType() == Materials.LEGACY_REDSTONE_COMPARATOR_ON.parseMaterial() || event.getClickedBlock().getType() == Materials.REPEATER.parseMaterial() || event.getClickedBlock().getType() == Materials.LEGACY_DIODE_BLOCK_OFF.parseMaterial() || event.getClickedBlock().getType() == Materials.LEGACY_DIODE_BLOCK_ON.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Redstone")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Materials.OAK_FENCE_GATE.parseMaterial() || event.getClickedBlock().getType() == Material.ACACIA_FENCE_GATE || event.getClickedBlock().getType() == Material.BIRCH_FENCE_GATE || event.getClickedBlock().getType() == Material.DARK_OAK_FENCE_GATE || event.getClickedBlock().getType() == Material.JUNGLE_FENCE_GATE || event.getClickedBlock().getType() == Material.SPRUCE_FENCE_GATE) {
					if (!islandManager.hasPermission(player, "Gate")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.DROPPER || event.getClickedBlock().getType() == Material.DISPENSER) {
					if (!islandManager.hasPermission(player, "DropperDispenser")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Materials.LEGACY_CAKE_BLOCK.getPostMaterial()) {
					if (player.getFoodLevel() < 20 && !islandManager.hasPermission(player, "Cake")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else if (event.getClickedBlock().getType() == Material.DRAGON_EGG) {
					if (!islandManager.hasPermission(player, "DragonEggUse")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}	
				} else if (event.getClickedBlock().getType() == Material.HOPPER) {
					if (!islandManager.hasPermission(player, "Hopper")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				}
				
				if ((event.getItem() != null) && (event.getItem().getType() != Material.AIR)) {
					if (event.getItem().getType() == Material.WATER_BUCKET || event.getItem().getType() == Material.LAVA_BUCKET) {
						if (!islandManager.hasPermission(player, "Bucket")) {
							event.setCancelled(true);
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							player.updateInventory();
						}
					} else if (event.getItem().getType() == Material.GLASS_BOTTLE) {
			    		if (event.getClickedBlock().getType() == Material.WATER || event.getClickedBlock().getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial() || event.getClickedBlock().getType() == Material.CAULDRON) {
							if (!islandManager.hasPermission(player, "WaterCollection")) {
								event.setCancelled(true);
								
								messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
								soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
								player.updateInventory();
							}
			    		}
					} else if (event.getItem().getType() == Materials.BAT_SPAWN_EGG.parseMaterial()) {
						if (!islandManager.hasPermission(player, "SpawnEgg")) {
							event.setCancelled(true);
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							player.updateInventory();
						}
					} else if (event.getItem().getType() == Material.ARMOR_STAND) {
						if (!islandManager.hasPermission(player, "ArmorStandPlacement")) {
							event.setCancelled(true);
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
							player.updateInventory();
						}
					}
				}
			}
			
			if (event.getItem() != null) {
				try {
					ItemStack structureTool = StructureUtil.getTool();
					
					if ((event.getItem().getType() == structureTool.getType()) && (event.getItem().hasItemMeta()) && (event.getItem().getItemMeta().getDisplayName().equals(structureTool.getItemMeta().getDisplayName()))) {
						if (player.hasPermission("skyblock.admin.structure.selection") || player.hasPermission("skyblock.admin.structure.*") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
							event.setCancelled(true);
							
							skyblock.getPlayerDataManager().getPlayerData(player).getArea().setPosition(2, event.getClickedBlock().getLocation());
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Structure.Tool.Position.Message").replace("%position", "2"));
							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
						}
					}
				} catch (Exception e) {}
			}
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (player.getTargetBlock((Set<Material>) null, 5).getType() == Material.FIRE) {
					if (!islandManager.hasPermission(player, "Fire")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
				}	
			}
			
			if (event.getItem() != null) {
				try {
					ItemStack structureTool = StructureUtil.getTool();
					
					if ((event.getItem().getType() == structureTool.getType()) && (event.getItem().hasItemMeta()) && (event.getItem().getItemMeta().getDisplayName().equals(structureTool.getItemMeta().getDisplayName()))) {
						if (player.hasPermission("skyblock.admin.structure.selection") || player.hasPermission("skyblock.admin.structure.*") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
							event.setCancelled(true);
							
							skyblock.getPlayerDataManager().getPlayerData(player).getArea().setPosition(1, event.getClickedBlock().getLocation());
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Structure.Tool.Position.Message").replace("%position", "1"));
							soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);	
						}
					}
				} catch (Exception e) {}
			}
		} else if (event.getAction() == Action.PHYSICAL) {
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
		    	if (event.getClickedBlock().getType() == Materials.FARMLAND.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Crop")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
		    	} else if (event.getClickedBlock().getType() == Materials.STONE_PRESSURE_PLATE.parseMaterial() || event.getClickedBlock().getType() == Materials.OAK_PRESSURE_PLATE.parseMaterial() || event.getClickedBlock().getType() == Materials.SPRUCE_PRESSURE_PLATE.parseMaterial() || event.getClickedBlock().getType() == Materials.BIRCH_PRESSURE_PLATE.parseMaterial() || event.getClickedBlock().getType() == Materials.JUNGLE_PRESSURE_PLATE.parseMaterial() || event.getClickedBlock().getType() == Materials.ACACIA_PRESSURE_PLATE.parseMaterial() || event.getClickedBlock().getType() == Materials.DARK_OAK_PRESSURE_PLATE.parseMaterial()) {
					// INFO This may cause performance drop
		    		
		    		if (!islandManager.hasPermission(player, "PressurePlate")) {
						event.setCancelled(true);
					}
		    	} else if (event.getClickedBlock().getType() == Material.TRIPWIRE) {
					if (!islandManager.hasPermission(player, "Redstone")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
		    	}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		
		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
	    	if ((player.getItemInHand() != null) && (player.getItemInHand().getType() != Material.AIR)) {
	    		if (player.getItemInHand().getType() == Materials.LEAD.parseMaterial()) {
					if (!islandManager.hasPermission(player, "Leash")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					
						return;
					}
	    		}
	    	}
			
			if (event.getRightClicked().getType() == EntityType.HORSE || event.getRightClicked().getType() == EntityType.PIG) {
				if (event.getRightClicked().getType() == EntityType.HORSE) {
					Horse horse = (Horse) event.getRightClicked();
					
					if (horse.getInventory().getSaddle() != null && player.isSneaking()) {
						if (!islandManager.hasPermission(player, "HorseInventory")) {
							event.setCancelled(true);
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
					} else {
						if (!islandManager.hasPermission(player, "MobRiding")) {
							event.setCancelled(true);
							
							messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
					}
				} else if (event.getRightClicked().getType() == EntityType.PIG) {
					if (!islandManager.hasPermission(player, "MobRiding")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				}
			} else if (event.getRightClicked().getType() == EntityType.COW || event.getRightClicked().getType() == EntityType.MUSHROOM_COW) {
    			if (player.getItemInHand().getType() == Material.BUCKET) {
					if (!islandManager.hasPermission(player, "Milking")) {
						event.setCancelled(true);
						
						messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
    			}
			} else if (event.getRightClicked().getType() == EntityType.VILLAGER) {
				if (!islandManager.hasPermission(player, "Trading")) {
					event.setCancelled(true);
					
					messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.MINECART || event.getRightClicked().getType() == EntityType.BOAT) {
				if (!islandManager.hasPermission(player, "MinecartBoat")) {
					event.setCancelled(true);
					
					messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			}
			
			if (event.getRightClicked().getType() == EntityType.HORSE) {
				if (!(player.getItemInHand().getType() == Material.GOLDEN_APPLE || player.getItemInHand().getType() == Material.GOLDEN_CARROT || player.getItemInHand().getType() == Material.SUGAR || player.getItemInHand().getType() == Material.WHEAT || player.getItemInHand().getType() == Material.APPLE || player.getItemInHand().getType() == Material.HAY_BLOCK)) {
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.SHEEP || event.getRightClicked().getType() == EntityType.COW || event.getRightClicked().getType() == EntityType.MUSHROOM_COW) {
				if (!(player.getItemInHand().getType() == Material.WHEAT)) {
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.PIG) {
				if (!(player.getItemInHand().getType() == Materials.CARROT.parseMaterial() || player.getItemInHand().getType() == Materials.POTATO.parseMaterial() || player.getItemInHand().getType() == Material.BAKED_POTATO || player.getItemInHand().getType() == Material.POISONOUS_POTATO)) {
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.CHICKEN) {
				if (!(player.getItemInHand().getType() == Materials.WHEAT_SEEDS.parseMaterial() || player.getItemInHand().getType() == Material.PUMPKIN_SEEDS || player.getItemInHand().getType() == Material.MELON_SEEDS)) {
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.WOLF) {
				if (!(player.getItemInHand().getType() == Material.BONE || player.getItemInHand().getType() == Materials.PORKCHOP.parseMaterial() || player.getItemInHand().getType() == Materials.BEEF.parseMaterial() || player.getItemInHand().getType() == Materials.CHICKEN.parseMaterial() || player.getItemInHand().getType() == Material.RABBIT || player.getItemInHand().getType() == Material.MUTTON || player.getItemInHand().getType() == Material.ROTTEN_FLESH || player.getItemInHand().getType() == Materials.COOKED_PORKCHOP.parseMaterial() || player.getItemInHand().getType() == Material.COOKED_BEEF || player.getItemInHand().getType() == Material.COOKED_CHICKEN || player.getItemInHand().getType() == Material.COOKED_RABBIT || player.getItemInHand().getType() == Material.COOKED_MUTTON || player.getItemInHand().getType() == Materials.COD.parseMaterial() || player.getItemInHand().getType() == Materials.COOKED_COD.parseMaterial())) {
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.OCELOT) {
				if (!(player.getItemInHand().getType() == Materials.COD.parseMaterial())) {
					return;
				}
			} else if (event.getRightClicked().getType() == EntityType.RABBIT) {
				if (!(player.getItemInHand().getType() == Materials.DANDELION.parseMaterial()|| player.getItemInHand().getType() == Materials.CARROTS.parseMaterial() || player.getItemInHand().getType() == Material.GOLDEN_CARROT)) {
					return;
				}
			} else {
				return;
			}
			
			if (!islandManager.hasPermission(player, "AnimalBreeding")) {
				event.setCancelled(true);
				
				messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		IslandManager islandManager = skyblock.getIslandManager();
		
		if (event.getRightClicked() instanceof ArmorStand) {
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!islandManager.hasPermission(player, "ArmorStand")) {
					event.setCancelled(true);
					
					skyblock.getMessageManager().sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			}
		}
	}
}
