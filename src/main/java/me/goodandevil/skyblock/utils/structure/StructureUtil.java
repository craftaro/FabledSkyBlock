package me.goodandevil.skyblock.utils.structure;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.utils.GZipUtil;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.utils.world.block.BlockData;
import me.goodandevil.skyblock.utils.world.block.BlockDegreesType;
import me.goodandevil.skyblock.utils.world.block.BlockUtil;
import me.goodandevil.skyblock.utils.world.entity.EntityData;
import me.goodandevil.skyblock.utils.world.entity.EntityUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class StructureUtil {

	public static void saveStructure(File configFile, org.bukkit.Location originLocation,
			org.bukkit.Location[] positions) throws Exception {
		if (!configFile.exists()) {
			configFile.createNewFile();
		}

		LinkedHashMap<Block, Location> blocks = SelectionLocation.getBlocks(originLocation, positions[0], positions[1]);
		LinkedHashMap<Entity, Location> entities = SelectionLocation.getEntities(originLocation, positions[0],
				positions[1]);

		List<BlockData> blockData = new ArrayList<>();
		List<EntityData> entityData = new ArrayList<>();

		String originBlockLocation = "";

		for (Block blockList : blocks.keySet()) {
			Location location = blocks.get(blockList);

			if (location.isOriginLocation()) {
				originBlockLocation = location.getX() + ":" + location.getY() + ":" + location.getZ() + ":"
						+ positions[0].getWorld().getName();

				if (blockList.getType() == Material.AIR) {
					blockData.add(BlockUtil.convertBlockToBlockData(blockList, location.getX(), location.getY(),
							location.getZ()));
				}
			}

			if (blockList.getType() == Material.AIR) {
				continue;
			}

			blockData.add(
					BlockUtil.convertBlockToBlockData(blockList, location.getX(), location.getY(), location.getZ()));
		}

		for (Entity entityList : entities.keySet()) {
			if (entityList.getType() == EntityType.PLAYER) {
				continue;
			}

			Location location = entities.get(entityList);
			entityData.add(EntityUtil.convertEntityToEntityData(entityList, location.getX(), location.getY(),
					location.getZ()));
		}

		if (!originBlockLocation.isEmpty()) {
			originBlockLocation = originBlockLocation + ":" + originLocation.getYaw() + ":" + originLocation.getPitch();
		}

		String JSONString = new Gson().toJson(new Storage(new Gson().toJson(blockData), new Gson().toJson(entityData),
				originBlockLocation, System.currentTimeMillis(), NMSUtil.getVersionNumber()), new TypeToken<Storage>() {
				}.getType());

		FileOutputStream fileOutputStream = new FileOutputStream(configFile, false);
		fileOutputStream.write(GZipUtil.compress(JSONString.getBytes(StandardCharsets.UTF_8)));
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public static Structure loadStructure(File configFile) throws IOException {
		if (!configFile.exists()) {
			return null;
		}

		byte[] content = new byte[(int) configFile.length()];

		FileInputStream fileInputStream = new FileInputStream(configFile);
		fileInputStream.read(content);
		fileInputStream.close();

		String JSONString = new String(GZipUtil.decompress(content));
		Storage storage = new Gson().fromJson(JSONString, new TypeToken<Storage>() {
		}.getType());

		return new Structure(storage, configFile.getName());
	}

	@SuppressWarnings("unchecked")
	public static Float[] pasteStructure(Structure structure, org.bukkit.Location location, BlockDegreesType type) {
		Storage storage = structure.getStructureStorage();

		String[] originLocationPositions = null;

		if (!storage.getOriginLocation().isEmpty()) {
			originLocationPositions = storage.getOriginLocation().split(":");
		}

		float yaw = 0.0F, pitch = 0.0F;

		if (originLocationPositions.length == 6) {
			yaw = Float.valueOf(originLocationPositions[4]);
			pitch = Float.valueOf(originLocationPositions[5]);
		}

		List<BlockData> blockData = new Gson().fromJson(storage.getBlocks(),
				new TypeToken<List<BlockData>>() {
				}.getType());

		for (BlockData blockDataList : blockData) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
				try {
					org.bukkit.Location blockRotationLocation = LocationUtil
							.rotateLocation(new org.bukkit.Location(location.getWorld(), blockDataList.getX(),
									blockDataList.getY(), blockDataList.getZ()), type);
					org.bukkit.Location blockLocation = new org.bukkit.Location(location.getWorld(),
							location.getX() - Math.abs(Integer.valueOf(storage.getOriginLocation().split(":")[0])),
							location.getY() - Integer.valueOf(storage.getOriginLocation().split(":")[1]),
							location.getZ() + Math.abs(Integer.valueOf(storage.getOriginLocation().split(":")[2])));
					blockLocation.add(blockRotationLocation);
					BlockUtil.convertBlockDataToBlock(blockLocation.getBlock(), blockDataList);
				} catch (Exception e) {
					SkyBlock.getInstance().getLogger().warning("Unable to convert BlockData to Block for type {" + blockDataList.getMaterial() +
							":" + blockDataList.getData() + "} in structure {" + structure.getStructureFile() + "}");
				}
			});
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
			for (EntityData entityDataList : (List<EntityData>) new Gson().fromJson(storage.getEntities(),
					new TypeToken<List<EntityData>>() {
					}.getType())) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
					try {
						org.bukkit.Location blockRotationLocation = LocationUtil
								.rotateLocation(new org.bukkit.Location(location.getWorld(), entityDataList.getX(),
										entityDataList.getY(), entityDataList.getZ()), type);
						org.bukkit.Location blockLocation = new org.bukkit.Location(location.getWorld(),
								location.getX() - Math.abs(Integer.valueOf(storage.getOriginLocation().split(":")[0])),
								location.getY() - Integer.valueOf(storage.getOriginLocation().split(":")[1]),
								location.getZ() + Math.abs(Integer.valueOf(storage.getOriginLocation().split(":")[2])));
						blockLocation.add(blockRotationLocation);
						EntityUtil.convertEntityDataToEntity(entityDataList, blockLocation, type);
					} catch (Exception e) {
						SkyBlock.getInstance().getLogger().warning("Unable to convert EntityData to Entity for type {" + entityDataList.getEntityType() +
								"} in structure {" + structure.getStructureFile() + "}");
					}
				});
			}
		}, 60L);

		return new Float[] { yaw, pitch };
	}

	public static ItemStack getTool() throws Exception {
		SkyBlock skyblock = SkyBlock.getInstance();

		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		ItemStack is = new ItemStack(
				Material.valueOf(fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getString("Island.Admin.Structure.Selector")));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				configLoad.getString("Island.Structure.Tool.Item.Displayname")));

		List<String> itemLore = new ArrayList<>();

		for (String itemLoreList : configLoad.getStringList("Island.Structure.Tool.Item.Lore")) {
			itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
		}

		im.setLore(itemLore);
		is.setItemMeta(im);

		return is;
	}

	public static org.bukkit.Location[] getFixedLocations(org.bukkit.Location location1,
			org.bukkit.Location location2) {
		org.bukkit.Location location1Fixed = location1.clone();
		org.bukkit.Location location2Fixed = location2.clone();

		if (location1.getX() > location2.getX()) {
			location1Fixed.setX(location2.getX());
			location2Fixed.setX(location1.getX());
		}

		if (location1.getZ() < location2.getZ()) {
			location1Fixed.setZ(location2.getZ());
			location2Fixed.setZ(location1.getZ());
		}

		return new org.bukkit.Location[] { location1Fixed, location2Fixed };
	}
}
