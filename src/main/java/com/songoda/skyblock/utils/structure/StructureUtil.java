package com.songoda.skyblock.utils.structure;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.nms.NmsManager;
import com.songoda.core.nms.nbt.NBTEntity;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.utils.Compression;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.utils.world.block.BlockData;
import com.songoda.skyblock.utils.world.block.BlockDegreesType;
import com.songoda.skyblock.utils.world.block.BlockUtil;
import com.songoda.skyblock.utils.world.entity.EntityData;
import com.songoda.skyblock.utils.world.entity.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public final class StructureUtil {

    public static void saveStructure(File configFile, org.bukkit.Location originLocation, org.bukkit.Location[] positions) throws Exception {
        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        LinkedHashMap<Block, Location> blocks = SelectionLocation.getBlocks(originLocation, positions[0], positions[1]);
        LinkedHashMap<Entity, Location> entities = SelectionLocation.getEntities(originLocation, positions[0], positions[1]);

        List<BlockData> blockData = new ArrayList<>();
        List<EntityData> entityData = new ArrayList<>();

        String originBlockLocation = "";

        for (Block block : blocks.keySet()) {
            Location location = blocks.get(block);
            CompatibleMaterial material = CompatibleMaterial.getMaterial(block);

            if (location.isOriginLocation()) {
                originBlockLocation = location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + positions[0].getWorld().getName();

                if (material == CompatibleMaterial.AIR) {
                    blockData.add(BlockUtil.convertBlockToBlockData(block, location.getX(), location.getY(), location.getZ()));
                }
            }

            if (material == CompatibleMaterial.AIR) {
                continue;
            }

            blockData.add(BlockUtil.convertBlockToBlockData(block, location.getX(), location.getY(), location.getZ()));
        }

        for (Entity entityList : entities.keySet()) {
            if (entityList.getType() == EntityType.PLAYER) {
                continue;
            }

            Location location = entities.get(entityList);
            entityData.add(EntityUtil.convertEntityToEntityData(entityList, location.getX(), location.getY(), location.getZ()));
        }

        if (!originBlockLocation.isEmpty()) {
            originBlockLocation = originBlockLocation + ":" + originLocation.getYaw() + ":" + originLocation.getPitch();
        }

        String JSONString = new Gson().toJson(new Storage(new Gson().toJson(blockData), new Gson().toJson(entityData), originBlockLocation, System.currentTimeMillis(), NMSUtil.getVersionNumber()), Storage.class);

        FileOutputStream fileOutputStream = new FileOutputStream(configFile, false);
        fileOutputStream.write(Base64.getEncoder().encode(JSONString.getBytes(StandardCharsets.UTF_8)));
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private static String getBase64String(File file) {
        if (!file.exists()) return null;

        String firstLine = null;

        try {
            firstLine = Files.asCharSource(file, StandardCharsets.UTF_8).readFirstLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return firstLine;
    }

    public static Structure loadStructure(File configFile) throws IOException {

        byte[] content = new byte[(int) configFile.length()];
        FileInputStream fileInputStream = new FileInputStream(configFile);
        fileInputStream.read(content);
        fileInputStream.close();
        Storage storage;

        Pattern pattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

        if (!pattern.matcher(new String(content)).find()) {
            try {
                storage = new Gson().fromJson(Compression.decompress(content), Storage.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            String base64 = getBase64String(configFile);

            if (base64 == null) {
                base64 = getBase64String(new File(SkyBlock.getInstance().getDataFolder() + "/" + "structures", "default.structure"));
                SkyBlock.getInstance().getLogger().log(Level.SEVERE, "Unable to load structure '" + configFile.getAbsolutePath() + "' using default instead.");
            }

            if (base64 == null) {
                throw new IllegalArgumentException("Couldn't load the default structure file.");
            }
            try {
                storage = new Gson().fromJson(new String(Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8))), Storage.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }

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

        List<BlockData> blockData = new Gson().fromJson(storage.getBlocks(), new TypeToken<List<BlockData>>() {}.getType());

        for (BlockData blockDataList : blockData) {

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
                try {
                    org.bukkit.Location blockRotationLocation = LocationUtil.rotateLocation(new org.bukkit.Location(location.getWorld(), blockDataList.getX(), blockDataList.getY(), blockDataList.getZ()), type);
                    org.bukkit.Location blockLocation = new org.bukkit.Location(location.getWorld(), location.getX() - Math.abs(Integer.parseInt(storage.getOriginLocation().split(":")[0])),
                            location.getY() - Integer.parseInt(storage.getOriginLocation().split(":")[1]), location.getZ() + Math.abs(Integer.parseInt(storage.getOriginLocation().split(":")[2])));
                    blockLocation.add(blockRotationLocation);
                    BlockUtil.convertBlockDataToBlock(blockLocation.getBlock(), blockDataList);
                } catch (Exception e) {
                    SkyBlock.getInstance().getLogger()
                            .warning("Unable to convert BlockData to Block for type {" + blockDataList.getMaterial() + ":" + blockDataList.getData() + "} in structure {" + structure.getStructureFile() + "}");
                }
            });
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
            for (EntityData entityDataList : (List<EntityData>) new Gson().fromJson(storage.getEntities(), new TypeToken<List<EntityData>>() {
            }.getType())) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
                    try {
                        if (entityDataList.getSerializedNBT() != null) {
                            org.bukkit.Location blockRotationLocation = LocationUtil.rotateLocation(new org.bukkit.Location(location.getWorld(), entityDataList.getX(), entityDataList.getY(), entityDataList.getZ()), type);
                            org.bukkit.Location blockLocation = new org.bukkit.Location(location.getWorld(), location.getX() - Math.abs(Integer.parseInt(storage.getOriginLocation().split(":")[0])),
                                    location.getY() - Integer.parseInt(storage.getOriginLocation().split(":")[1]), location.getZ() + Math.abs(Integer.parseInt(storage.getOriginLocation().split(":")[2])));
                            blockLocation.add(blockRotationLocation);
                            NBTEntity nbtEntity = NmsManager.getNbt().newEntity();
                            nbtEntity.deSerialize(entityDataList.getSerializedNBT());
                            nbtEntity.set("UUID", UUID.randomUUID());
                            nbtEntity.spawn(blockLocation);
                        } else {
                            org.bukkit.Location blockRotationLocation = LocationUtil.rotateLocation(new org.bukkit.Location(location.getWorld(), entityDataList.getX(), entityDataList.getY(), entityDataList.getZ()), type);
                            org.bukkit.Location blockLocation = new org.bukkit.Location(location.getWorld(), location.getX() - Math.abs(Integer.parseInt(storage.getOriginLocation().split(":")[0])),
                                    location.getY() - Integer.parseInt(storage.getOriginLocation().split(":")[1]), location.getZ() + Math.abs(Integer.parseInt(storage.getOriginLocation().split(":")[2])));
                            blockLocation.add(blockRotationLocation);
                            EntityUtil.convertEntityDataToEntity(entityDataList, blockLocation, type);
                        }
                    } catch (Exception e) {
                        SkyBlock.getInstance().getLogger().warning("Unable to convert EntityData to Entity for type {" + entityDataList.getEntityType() + "} in structure {" + structure.getStructureFile() + "}");
                    }
                });
            }
        }, 60L);

        return new Float[] { yaw, pitch };
    }

    public static ItemStack getTool() throws Exception {
        SkyBlock plugin = SkyBlock.getInstance();

        FileManager fileManager = plugin.getFileManager();

        FileConfiguration configLoad = plugin.getLanguage();

        ItemStack is = new ItemStack(Material.valueOf(plugin.getConfiguration().getString("Island.Admin.Structure.Selector")));
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Structure.Tool.Item.Displayname")));

        List<String> itemLore = new ArrayList<>();

        for (String itemLoreList : configLoad.getStringList("Island.Structure.Tool.Item.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }

        im.setLore(itemLore);
        is.setItemMeta(im);

        return is;
    }

    public static org.bukkit.Location[] getFixedLocations(org.bukkit.Location location1, org.bukkit.Location location2) {
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
