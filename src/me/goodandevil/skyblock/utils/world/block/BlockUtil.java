package me.goodandevil.skyblock.utils.world.block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.potion.PotionEffectType;

import me.goodandevil.skyblock.utils.item.ItemStackUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;

@SuppressWarnings("deprecation")
public final class BlockUtil {
    
    public static BlockData convertBlockToBlockData(Block block, int x, int y, int z) {
        BlockData blockData = new BlockData(block.getType().toString(), block.getData(), x, y, z, block.getBiome().toString());
    	
        int NMSVersion = NMSUtil.getVersionNumber();
    	blockData.setVersion(NMSVersion);
    	
        if (NMSVersion > 12) {
        	blockData.setBlockData(block.getBlockData().getAsString());
        }
        
        BlockState blockState = block.getState();
        MaterialData materialData = blockState.getData();
        
        if (blockState instanceof Banner) {
            Banner banner = (Banner) blockState;
            blockData.setBaseColor(banner.getBaseColor().toString());
            
            List<String> patterns = new ArrayList<>();
            
            for(Pattern patternList : banner.getPatterns()) {
                patterns.add(patternList.getPattern().toString() + ":" + patternList.getColor().toString());
            }
            
            blockData.setPatterns(patterns);
            blockData.setStateType(BlockStateType.BANNER.toString());
        } else if (blockState instanceof Beacon) {
            Beacon beacon = (Beacon) blockState;
            blockData.setPotionEffect(beacon.getPrimaryEffect().toString() + ":" + beacon.getSecondaryEffect().toString());
            
            for (int i = 0; i < beacon.getInventory().getSize(); i++) {
            	ItemStack is = beacon.getInventory().getItem(i);
            	
            	if (is != null && is.getType() != Material.AIR) {
            		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
            	}
            }
            
            blockData.setStateType(BlockStateType.BEACON.toString());
        } else if (blockState instanceof BrewingStand) {
            BrewingStand brewingStand = (BrewingStand) blockState;
            blockData.setBrewingTime(brewingStand.getBrewingTime());
            blockData.setFuelLevel(brewingStand.getFuelLevel());
            blockData.setStateType(BlockStateType.BREWINGSTAND.toString());
        } else if (blockState instanceof Furnace) {
            Furnace furnace = (Furnace) blockState;
            blockData.setBurnTime(furnace.getBurnTime());
            blockData.setCookTime(furnace.getCookTime());
            
            for (int i = 0; i < furnace.getInventory().getSize(); i++) {
            	ItemStack is = furnace.getInventory().getItem(i);
            	
            	if (is != null && is.getType() != Material.AIR) {
            		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
            	}
            }
            
            blockData.setStateType(BlockStateType.FURNACE.toString());
        } else if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            for (int i = 0; i < chest.getInventory().getSize(); i++) {
            	ItemStack is = chest.getInventory().getItem(i);
            	
            	if (is != null && is.getType() != Material.AIR) {
            		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
            	}
            }
            
            blockData.setStateType(BlockStateType.CHEST.toString());
        } else if (blockState instanceof Dispenser) {
            Dispenser dispenser = (Dispenser) blockState;
            
            for (int i = 0; i < dispenser.getInventory().getSize(); i++) {
            	ItemStack is = dispenser.getInventory().getItem(i);
            	
            	if (is != null && is.getType() != Material.AIR) {
            		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
            	}
            }
            
            blockData.setStateType(BlockStateType.DISPENSER.toString());
        } else if (blockState instanceof Dropper) {
            Dropper dropper = (Dropper) blockState;
            
            for (int i = 0; i < dropper.getInventory().getSize(); i++) {
            	ItemStack is = dropper.getInventory().getItem(i);
            	
            	if (is != null && is.getType() != Material.AIR) {
            		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
            	}
            }
            
            blockData.setStateType(BlockStateType.DROPPER.toString());
        } else if (blockState instanceof Hopper) {
            Hopper hopper = (Hopper) blockState;
            
            for (int i = 0; i < hopper.getInventory().getSize(); i++) {
            	ItemStack is = hopper.getInventory().getItem(i);
            	
            	if (is != null && is.getType() != Material.AIR) {
            		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
            	}
            }
            
            blockData.setStateType(BlockStateType.HOPPER.toString());
        } else if (blockState instanceof CommandBlock) {
            CommandBlock commandBlock = (CommandBlock) blockState;
            blockData.setCommand(commandBlock.getCommand());
            blockData.setCommandBlockName(commandBlock.getName());
            blockData.setStateType(BlockStateType.COMMANDBLOCK.toString());
        } else if (blockState instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
            blockData.setEntity(creatureSpawner.getSpawnedType().toString());
            blockData.setDelay(creatureSpawner.getDelay());
            blockData.setStateType(BlockStateType.CREATURESPAWNER.toString());
        } else if (blockState instanceof Jukebox) {
            Jukebox jukebox = (Jukebox) blockState;
            blockData.setPlaying(jukebox.getPlaying().toString());
            blockData.setStateType(BlockStateType.JUKEBOX.toString());
        } else if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
            blockData.setSignLines(sign.getLines());
            blockData.setStateType(BlockStateType.SIGN.toString());
        } else if (blockState instanceof Skull) {
            Skull skull = (Skull) blockState;
            blockData.setSkullOwner(skull.getOwner());
            blockData.setSkullType(skull.getSkullType().toString());
            blockData.setRotateFace(skull.getRotation().toString());
            blockData.setStateType(BlockStateType.SKULL.toString());
        } else {
        	if (NMSVersion > 8) {
                if (blockState instanceof EndGateway) {
                    EndGateway endGateway = (EndGateway) blockState;
                    blockData.setExactTeleport(endGateway.isExactTeleport());
                    
                    Location location = endGateway.getExitLocation();
                    blockData.setExitLocation(location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getWorld().getName());
                    blockData.setStateType(BlockStateType.ENDGATEWAY.toString());
                } else if (blockState instanceof FlowerPot) {
                    FlowerPot flowerPot = (FlowerPot) blockState;
                    blockData.setFlower(flowerPot.getContents().getItemType().toString() + ":" + flowerPot.getContents().getData());
                    blockData.setStateType(BlockStateType.FLOWERPOT.toString());
                }
                
                if (NMSVersion > 10) {
                    if(blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        
                        for (int i = 0; i < shulkerBox.getInventory().getSize(); i++) {
                        	ItemStack is = shulkerBox.getInventory().getItem(i);
                        	
                        	if (is != null && is.getType() != Material.AIR) {
                        		blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                        	}
                        }
                        
                        blockData.setStateType(BlockStateType.SHULKERBOX.toString());
                    }
                }
        	}
        }
        
        if (materialData instanceof Stairs) {
        	blockData.setFacing(((Stairs) materialData).getFacing().toString());
        	blockData.setDataType(BlockDataType.STAIRS.toString());
        }
        
        return blockData;
    }
    
    public static void convertBlockDataToBlock(Block block, BlockData blockData) {
    	int NMSVersion = NMSUtil.getVersionNumber();
    	
    	Material material = null;
    	
    	if (NMSVersion > 12 && blockData.getVersion() > 12 && blockData.getBlockData() != null) {
    		block.setBlockData(Bukkit.getServer().createBlockData(blockData.getBlockData()));
    	} else {
        	if (NMSVersion > 12) {
        		if (blockData.getVersion() > 12) {
        			material = Material.valueOf(blockData.getMaterial());
        		} else {
        			material = Materials.requestMaterials(blockData.getMaterial(), block.getData()).getPostMaterial();
        		}
        	} else {
        		if (blockData.getVersion() > 12) {
        			material = Materials.fromString(blockData.getMaterial()).parseMaterial();
        		} else {
        			material = Material.valueOf(blockData.getMaterial());
        		}
        	}
        	
        	setBlockFast(block.getWorld(), block.getX(), block.getY(), block.getZ(), material, blockData.getData());
    	}
    	
    	// TODO Create a class to support biome changes
        //block.setBiome(Biome.valueOf(blockData.getBiome().toUpperCase()));
        
        BlockStateType blockTypeState = BlockStateType.valueOf(blockData.getStateType());
        
        if (blockTypeState == BlockStateType.BANNER) {
            Banner banner = (Banner) block.getState();
            banner.setBaseColor(DyeColor.valueOf(blockData.getBaseColor().toUpperCase()));
            
            for(String patternList : blockData.getPatterns()) {
                String[] pattern = patternList.split(":");
                banner.addPattern(new Pattern(DyeColor.valueOf(pattern[1].toUpperCase()), PatternType.valueOf(pattern[0].toUpperCase())));
            }
            
            block.getState().update();
        } else if (blockTypeState == BlockStateType.BEACON) {
            Beacon beacon = (Beacon) block.getState();
            String[] potionEffect = blockData.getPotionEffect().split(":");
            beacon.setPrimaryEffect(PotionEffectType.getByName(potionEffect[0].toUpperCase()));
            beacon.setSecondaryEffect(PotionEffectType.getByName(potionEffect[1].toUpperCase()));
            
            for (Integer slotList : blockData.getInventory().keySet()) {
            	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
            	beacon.getInventory().setItem(slotList, is);
            }
        } else if (blockTypeState == BlockStateType.BREWINGSTAND) {
            BrewingStand brewingStand = (BrewingStand) block.getState();
            brewingStand.setBrewingTime(blockData.getBrewingTime());
            brewingStand.setFuelLevel(blockData.getFuelLevel());
        } else if (blockTypeState == BlockStateType.COMMANDBLOCK) {
            CommandBlock commandBlock = (CommandBlock) block.getState();
            commandBlock.setCommand(blockData.getCommand());
            commandBlock.setName(blockData.getCommandBlockName());
        } else if (blockTypeState == BlockStateType.CHEST) {
            Chest chest = (Chest) block.getState();
            
            for (Integer slotList : blockData.getInventory().keySet()) {
            	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
            	chest.getInventory().setItem(slotList, is);
            }
        } else if (blockTypeState == BlockStateType.DISPENSER) {
            Dispenser dispenser = (Dispenser) block.getState();
            
            for (Integer slotList : blockData.getInventory().keySet()) {
            	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
            	dispenser.getInventory().setItem(slotList, is);
            }
        } else if (blockTypeState == BlockStateType.DROPPER) {
            Dropper dropper = (Dropper) block.getState();
            
            for (Integer slotList : blockData.getInventory().keySet()) {
            	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
            	dropper.getInventory().setItem(slotList, is);
            }
        } else if (blockTypeState == BlockStateType.HOPPER) {
            Hopper hopper = (Hopper) block.getState();
            
            for (Integer slotList : blockData.getInventory().keySet()) {
            	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
            	hopper.getInventory().setItem(slotList, is);
            }
        } else if (blockTypeState == BlockStateType.CREATURESPAWNER) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            creatureSpawner.setDelay(blockData.getDelay());
            creatureSpawner.setSpawnedType(EntityType.valueOf(blockData.getEntity().toUpperCase()));
        } else if (blockTypeState == BlockStateType.FURNACE) {
            Furnace furnace = (Furnace) block.getState();
            furnace.setBurnTime(blockData.getBurnTime());
            furnace.setCookTime(blockData.getCookTime());
            
            for (Integer slotList : blockData.getInventory().keySet()) {
            	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
            	furnace.getInventory().setItem(slotList, is);
            }
        } else if (blockTypeState == BlockStateType.JUKEBOX) {
            Jukebox jukebox = (Jukebox) block.getState();
            jukebox.setPlaying(Material.valueOf(blockData.getPlaying().toUpperCase()));
        } else if (blockTypeState == BlockStateType.SIGN) {
            Sign sign = (Sign) block.getState();
            
            for (int i = 0; i < blockData.getSignLines().length; i++) {
            	sign.setLine(i, blockData.getSignLines()[i]);
            }
            
            sign.update();
        } else if (blockTypeState == BlockStateType.SKULL) {
            Skull skull = (Skull) block.getState();
            skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(blockData.getSkullOwner()));
            skull.setRotation(BlockFace.valueOf(blockData.getRotateFace().toUpperCase()));
            skull.setSkullType(SkullType.valueOf(blockData.getSkullType().toUpperCase()));
        } else {
        	if (NMSVersion > 8) {
                if (blockTypeState == BlockStateType.ENDGATEWAY) {
                    EndGateway endGateway = (EndGateway) block.getState();
                    endGateway.setExactTeleport(blockData.isExactTeleport());
                    
                    String[] exitLocation = blockData.getExitLocation().split(":");
                    World exitLocationWorld = Bukkit.getServer().getWorld(exitLocation[3]);
                    
                    double exitLocationX = Double.parseDouble(exitLocation[0]);
                    double exitLocationY = Double.parseDouble(exitLocation[1]);
                    double exitLocationZ = Double.parseDouble(exitLocation[2]);
                    
                    endGateway.setExitLocation(new Location(exitLocationWorld, exitLocationX, exitLocationY, exitLocationZ));
                } else if (blockTypeState == BlockStateType.FLOWERPOT) {
                    FlowerPot flowerPot = (FlowerPot) block.getState();
                    String[] flower = blockData.getFlower().split(":");
                    Bukkit.broadcastMessage(flower[0] + " | " + flower[1]);
                    flowerPot.setContents(new MaterialData(Material.valueOf(flower[0].toUpperCase()), (byte) Integer.parseInt(flower[1])));
                }
                
                if (NMSVersion > 10) {
                    if(blockTypeState == BlockStateType.SHULKERBOX) {
                        ShulkerBox shulkerBox = (ShulkerBox) block.getState();
                        
                        for (Integer slotList : blockData.getInventory().keySet()) {
                        	ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                        	shulkerBox.getInventory().setItem(slotList, is);
                        }
                    }
                }
        	}
        }
        
        BlockDataType blockDataType = BlockDataType.valueOf(blockData.getDataType());
        
        if (blockDataType == BlockDataType.STAIRS) {
            Stairs stairs = (Stairs) block.getState().getData();
            stairs.setFacingDirection(BlockFace.valueOf(blockData.getFacing()));
            block.getState().setData(stairs);
        }
        
        if (NMSVersion < 13) {
        	block.getState().update();
        }
        
    	if (blockData.getMaterial().equals("DOUBLE_PLANT")) {
    		Block topBlock = block.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock();
    		Block bottomBlock = block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock();
    		
    		if (bottomBlock.getType() == Material.AIR && !topBlock.getType().name().equals("DOUBLE_PLANT")) {
    			bottomBlock.setType(Materials.LEGACY_DOUBLE_PLANT.getPostMaterial());
    			
    	        if (NMSVersion < 13) {
    	        	try {
    	        		bottomBlock.getClass().getMethod("setData", byte.class).invoke(bottomBlock, (byte) 2);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    	        }
    		}
    	}
    }
    
    public static List<Block> getNearbyBlocks(Location loc, int rx, int ry, int rz){
        List<Block> nearbyBlocks = new ArrayList<>();
        
        for (int x = -(rx); x <= rx; x++){
            for (int y = -(ry); y <= ry; y++) {
                for (int z = -(rz); z <= rz; z++) {
                	nearbyBlocks.add(new Location(loc.getWorld(),loc.getX() + x, loc.getY() + y, loc.getZ() + z).getBlock());
                }
            }
        }
        
        return nearbyBlocks;
    }
    
    public static void setBlockFast(World world, int x, int y, int z, Material material, byte data) {
    	try {
    		Class<?> IBlockDataClass = NMSUtil.getNMSClass("IBlockData");
    		
    		Object worldHandle = world.getClass().getMethod("getHandle").invoke(world);
    		Object chunk = worldHandle.getClass().getMethod("getChunkAt", int.class, int.class).invoke(worldHandle, x >> 4, z >> 4);
    		Object blockPosition = NMSUtil.getNMSClass("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(x & 0xF, y, z & 0xF);
    		
        	if (NMSUtil.getVersionNumber() > 12) {
        		Object block = NMSUtil.getNMSClass("Blocks").getField(material.name()).get(null);
        		Object IBlockData = block.getClass().getMethod("getBlockData").invoke(block);
        		worldHandle.getClass().getMethod("setTypeAndData", blockPosition.getClass(), IBlockDataClass, int.class).invoke(worldHandle, blockPosition, IBlockData, 2);
        		chunk.getClass().getMethod("setType", blockPosition.getClass(), IBlockDataClass, boolean.class).invoke(chunk, blockPosition, IBlockData, true);
        	} else {
        		Object IBlockData = NMSUtil.getNMSClass("Block").getMethod("getByCombinedId", int.class).invoke(null, material.getId() + (data << 12));
        		worldHandle.getClass().getMethod("setTypeAndData", blockPosition.getClass(), IBlockDataClass, int.class).invoke(worldHandle, blockPosition, IBlockData, 3);
        		chunk.getClass().getMethod("a", blockPosition.getClass(), IBlockDataClass).invoke(chunk, blockPosition, IBlockData);
        	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
