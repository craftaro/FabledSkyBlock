package com.songoda.skyblock.utils.world.block;

import com.craftaro.core.compatibility.ClassMapping;
import com.craftaro.core.compatibility.MethodMapping;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.BlockUtils;
import com.craftaro.core.utils.NMSUtils;
import com.songoda.skyblock.utils.item.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Barrel;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.EndGateway;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class BlockUtil extends BlockUtils {
    public static BlockData convertBlockToBlockData(Block block, int x, int y, int z) {
        BlockData blockData = new BlockData(block.getType().name(), block.getData(), x, y, z, block.getBiome().toString());

        blockData.setVersion(Integer.parseInt(ServerVersion.getVersionReleaseNumber()));

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
            blockData.setBlockData(block.getBlockData().getAsString());
        }

        BlockState blockState = block.getState();
        MaterialData materialData = blockState.getData();

        if (blockState instanceof Banner) {
            Banner banner = (Banner) blockState;
            blockData.setBaseColor(banner.getBaseColor().toString());

            final List<Pattern> bannerPatterns = banner.getPatterns();
            final List<String> stringPatterns = bannerPatterns.stream().map(patternList -> patternList.getPattern().toString() + ":" + patternList.getColor().toString()).collect(Collectors.toCollection(() -> new ArrayList<>(bannerPatterns.size())));

            blockData.setPatterns(stringPatterns);
            blockData.setStateType(BlockStateType.BANNER.toString());
        } else if (blockState instanceof Beacon) {
            Beacon beacon = (Beacon) blockState;
            String primaryEffectName = beacon.getPrimaryEffect() != null ? beacon.getPrimaryEffect().toString() : "null";
            String secondaryEffectName = beacon.getSecondaryEffect() != null ? beacon.getSecondaryEffect().toString() : "null";

            blockData.setPotionEffect(primaryEffectName + ":" + secondaryEffectName);
            blockData.setStateType(BlockStateType.BEACON.toString());
        } else if (blockState instanceof BrewingStand && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
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

                if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
                    blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                }
            }

            blockData.setStateType(BlockStateType.FURNACE.toString());
        } else if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            for (int i = 0; i < chest.getInventory().getSize(); i++) {
                ItemStack is = chest.getInventory().getItem(i);

                if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
                    blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                }
            }

            blockData.setStateType(BlockStateType.CHEST.toString());
        } else if (blockState instanceof Dispenser) {
            Dispenser dispenser = (Dispenser) blockState;

            for (int i = 0; i < dispenser.getInventory().getSize(); i++) {
                ItemStack is = dispenser.getInventory().getItem(i);

                if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
                    blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                }
            }

            blockData.setStateType(BlockStateType.DISPENSER.toString());
        } else if (blockState instanceof Dropper) {
            Dropper dropper = (Dropper) blockState;

            for (int i = 0; i < dropper.getInventory().getSize(); i++) {
                ItemStack is = dropper.getInventory().getItem(i);

                if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
                    blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                }
            }

            blockData.setStateType(BlockStateType.DROPPER.toString());
        } else if (blockState instanceof Hopper) {
            Hopper hopper = (Hopper) blockState;

            for (int i = 0; i < hopper.getInventory().getSize(); i++) {
                ItemStack is = hopper.getInventory().getItem(i);

                if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
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

            if (creatureSpawner.getSpawnedType() != null) {
                blockData.setEntity(creatureSpawner.getSpawnedType().toString());
            }

            blockData.setDelay(creatureSpawner.getDelay());
            blockData.setStateType(BlockStateType.CREATURESPAWNER.toString());
        } else if (blockState instanceof Jukebox) {
            Jukebox jukebox = (Jukebox) blockState;

            jukebox.getPlaying();
            blockData.setPlaying(jukebox.getPlaying().toString());

            blockData.setStateType(BlockStateType.JUKEBOX.toString());
        } else if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;

            String[] signLines = sign.getLines();

            List<String> correctedSignLines = new ArrayList<>();

            for (String signLineList : signLines) {
                for (ChatColor chatColorList : ChatColor.values()) {
                    signLineList = signLineList.replace(chatColorList + "", "&" + chatColorList.toString().substring(chatColorList.toString().length() - 1));
                }

                correctedSignLines.add(signLineList);
            }

            signLines = correctedSignLines.toArray(new String[correctedSignLines.size()]);

            blockData.setSignLines(signLines);
            blockData.setStateType(BlockStateType.SIGN.toString());
        } else if (blockState instanceof Skull) {
            Skull skull = (Skull) blockState;
            blockData.setSkullOwner(skull.getOwner());
            blockData.setSkullType(skull.getSkullType().toString());
            blockData.setRotateFace(skull.getRotation().toString());
            blockData.setStateType(BlockStateType.SKULL.toString());
        } else {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                if (blockState instanceof EndGateway) {
                    EndGateway endGateway = (EndGateway) blockState;
                    blockData.setExactTeleport(endGateway.isExactTeleport());

                    Location location = endGateway.getExitLocation();
                    blockData.setExitLocation(location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getWorld().getName());
                    blockData.setStateType(BlockStateType.ENDGATEWAY.toString());
                }

                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;

                        for (int i = 0; i < shulkerBox.getInventory().getSize(); i++) {
                            ItemStack is = shulkerBox.getInventory().getItem(i);

                            if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
                                blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                            }
                        }

                        blockData.setStateType(BlockStateType.SHULKERBOX.toString());
                    }
                }

                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
                    if (blockState instanceof Barrel) {
                        Barrel barrel = (Barrel) blockState;

                        for (int i = 0; i < barrel.getInventory().getSize(); i++) {
                            ItemStack is = barrel.getInventory().getItem(i);

                            if (is != null && is.getType() != XMaterial.AIR.parseMaterial()) {
                                blockData.addItem(i, ItemStackUtil.serializeItemStack(is));
                            }
                        }

                        blockData.setStateType(BlockStateType.BARREL.toString());
                    }

                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
                        if (blockState instanceof RespawnAnchor) {
                            RespawnAnchor respawnAnchor = (RespawnAnchor) blockState;
                            blockData.setCharges(respawnAnchor.getCharges());
                            blockData.setStateType(BlockStateType.RESPAWN_ANCHOR.toString());
                        }
                    }
                }
            }
        }

        if (materialData instanceof Stairs) {
            blockData.setFacing(((Stairs) materialData).getFacing().toString());
            blockData.setDataType(BlockDataType.STAIRS.toString());
        } else if (materialData instanceof org.bukkit.material.FlowerPot) {
            if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_12)) {
                try {
                    World world = block.getWorld();

                    Class<?> blockPositionClass = ClassMapping.BLOCK_POSITION.getClazz();

                    Object worldHandle = world.getClass().getMethod("getHandle").invoke(world);
                    Object blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ());
                    Object tileEntity = worldHandle.getClass().getMethod("getTileEntity", blockPositionClass).invoke(worldHandle, blockPosition);

                    Field aField = tileEntity.getClass().getDeclaredField("a");
                    aField.setAccessible(true);

                    Object item = aField.get(tileEntity);

                    if (item != null) {
                        Object itemStackNMS = ClassMapping.ITEM_STACK.getClazz().getConstructor(ClassMapping.ITEM.getClazz()).newInstance(item);

                        ItemStack itemStack = (ItemStack) NMSUtils.getCraftClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemStackNMS.getClass()).invoke(null, itemStackNMS);

                        Field fField = tileEntity.getClass().getDeclaredField("f");
                        fField.setAccessible(true);

                        int data = (int) fField.get(tileEntity);

                        blockData.setFlower(itemStack.getType().name() + ":" + data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                org.bukkit.material.FlowerPot flowerPot = (org.bukkit.material.FlowerPot) materialData;

                if (flowerPot.getContents() != null && flowerPot.getContents().getItemType() != XMaterial.AIR.parseMaterial()) {
                    blockData.setFlower(flowerPot.getContents().getItemType().toString() + ":" + flowerPot.getContents().getData());
                }
            }

            blockData.setDataType(BlockDataType.FLOWERPOT.toString());
        }

        return blockData;
    }

    public static void convertBlockDataToBlock(Block block, BlockData blockData) {
        String materialStr = blockData.getMaterial();
        if (materialStr == null) {
            return;
        }
        Material material = Material.valueOf(materialStr);
        if (material == Material.AIR) {
            return;
        }

        if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_12)) {
            setBlockFast(block.getWorld(), block.getX(), block.getY(), block.getZ(), material, blockData.getData());
        } else {
            block.setBlockData(Bukkit.getServer().createBlockData(blockData.getBlockData()));
        }

        // TODO Create a class to support biome changes
        // block.setBiome(Biome.valueOf(blockData.getBiome().toUpperCase()));

        BlockStateType blockTypeState = BlockStateType.valueOf(blockData.getStateType());

        BlockState state = block.getState();

        if (blockTypeState == BlockStateType.BANNER) {
            Banner banner = (Banner) state;
            banner.setBaseColor(DyeColor.valueOf(blockData.getBaseColor().toUpperCase()));

            for (String patternList : blockData.getPatterns()) {
                String[] pattern = patternList.split(":");
                banner.addPattern(new Pattern(DyeColor.valueOf(pattern[1].toUpperCase()), PatternType.valueOf(pattern[0].toUpperCase())));
            }
            state.update();
        } else if (blockTypeState == BlockStateType.BEACON) {
            Beacon beacon = (Beacon) state;
            String[] potionEffect = blockData.getPotionEffect().split(":");
            if (!potionEffect[0].equals("null")) {
                beacon.setPrimaryEffect(PotionEffectType.getByName(potionEffect[0].toUpperCase()));
            }

            if (!potionEffect[1].equals("null")) {
                beacon.setSecondaryEffect(PotionEffectType.getByName(potionEffect[1].toUpperCase()));
            }
            state.update();
        } else if (blockTypeState == BlockStateType.BREWINGSTAND && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
            BrewingStand brewingStand = (BrewingStand) state;
            brewingStand.setBrewingTime(blockData.getBrewingTime());
            brewingStand.setFuelLevel(blockData.getFuelLevel());
            state.update();
        } else if (blockTypeState == BlockStateType.COMMANDBLOCK) {
            CommandBlock commandBlock = (CommandBlock) state;
            commandBlock.setCommand(blockData.getCommand());
            commandBlock.setName(blockData.getCommandBlockName());
            state.update();
        } else if (blockTypeState == BlockStateType.CHEST) {
            Chest chest = (Chest) state;

            for (Integer slotList : blockData.getInventory().keySet()) {
                if (slotList < chest.getInventory().getSize()) {
                    ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                    chest.getInventory().setItem(slotList, is);
                }
            }
        } else if (blockTypeState == BlockStateType.DISPENSER) {
            Dispenser dispenser = (Dispenser) state;

            for (Integer slotList : blockData.getInventory().keySet()) {
                if (slotList < dispenser.getInventory().getSize()) {
                    ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                    dispenser.getInventory().setItem(slotList, is);
                }
            }
        } else if (blockTypeState == BlockStateType.DROPPER) {
            Dropper dropper = (Dropper) state;

            for (Integer slotList : blockData.getInventory().keySet()) {
                if (slotList < dropper.getInventory().getSize()) {
                    ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                    dropper.getInventory().setItem(slotList, is);
                }
            }
        } else if (blockTypeState == BlockStateType.HOPPER) {
            Hopper hopper = (Hopper) state;

            for (Integer slotList : blockData.getInventory().keySet()) {
                if (slotList < hopper.getInventory().getSize()) {
                    ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                    hopper.getInventory().setItem(slotList, is);
                }
            }
        } else if (blockTypeState == BlockStateType.CREATURESPAWNER) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) state;

            if (blockData.getEntity() != null) {
                creatureSpawner.setSpawnedType(EntityType.valueOf(blockData.getEntity().toUpperCase()));
            }

            creatureSpawner.setDelay(blockData.getDelay());
            state.update();
        } else if (blockTypeState == BlockStateType.FURNACE) {
            Furnace furnace = (Furnace) state;
            furnace.setBurnTime(blockData.getBurnTime());
            furnace.setCookTime(blockData.getCookTime());

            state.update();

            for (Integer slotList : blockData.getInventory().keySet()) {
                if (slotList < furnace.getInventory().getSize()) {
                    ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                    furnace.getInventory().setItem(slotList, is);
                }
            }
        } else if (blockTypeState == BlockStateType.JUKEBOX) {
            Jukebox jukebox = (Jukebox) state;

            if (blockData.getPlaying() != null) {
                jukebox.setPlaying(Material.valueOf(blockData.getPlaying().toUpperCase()));
            }
            state.update();
        } else if (blockTypeState == BlockStateType.SIGN) {
            Sign sign = (Sign) state;

            for (int i = 0; i < blockData.getSignLines().length; i++) {
                sign.setLine(i, ChatColor.translateAlternateColorCodes('&', blockData.getSignLines()[i]));
            }
            state.update();
        } else if (blockTypeState == BlockStateType.SKULL) {
            Skull skull = (Skull) state;

            skull.setRotation(BlockFace.valueOf(blockData.getRotateFace().toUpperCase()));
            skull.setSkullType(SkullType.valueOf(blockData.getSkullType().toUpperCase()));

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_10)) {
                skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(blockData.getSkullOwner()));
            } else {
                skull.setOwner(blockData.getSkullOwner());
            }
            state.update();
        } else {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                if (blockTypeState == BlockStateType.ENDGATEWAY) {
                    EndGateway endGateway = (EndGateway) state;
                    endGateway.setExactTeleport(blockData.isExactTeleport());

                    String[] exitLocation = blockData.getExitLocation().split(":");
                    World exitLocationWorld = Bukkit.getServer().getWorld(exitLocation[3]);

                    double exitLocationX = Double.parseDouble(exitLocation[0]);
                    double exitLocationY = Double.parseDouble(exitLocation[1]);
                    double exitLocationZ = Double.parseDouble(exitLocation[2]);

                    endGateway.setExitLocation(new Location(exitLocationWorld, exitLocationX, exitLocationY, exitLocationZ));
                    state.update();
                }

                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                    if (blockTypeState == BlockStateType.SHULKERBOX) {
                        ShulkerBox shulkerBox = (ShulkerBox) state;

                        for (Integer slotList : blockData.getInventory().keySet()) {
                            if (slotList < shulkerBox.getInventory().getSize()) {
                                ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                                shulkerBox.getInventory().setItem(slotList, is);
                            }
                        }
                    }
                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
                        if (blockTypeState == BlockStateType.BARREL) {
                            Barrel barrel = (Barrel) state;

                            for (Integer slotList : blockData.getInventory().keySet()) {
                                if (slotList < barrel.getInventory().getSize()) {
                                    ItemStack is = ItemStackUtil.deserializeItemStack(blockData.getInventory().get(slotList));
                                    barrel.getInventory().setItem(slotList, is);
                                }
                            }
                        }
                    }

                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
                        if (blockTypeState == BlockStateType.RESPAWN_ANCHOR) {
                            RespawnAnchor respawnAnchor = (RespawnAnchor) state;
                            respawnAnchor.setCharges(blockData.getCharges());
                            state.update();
                        }
                    }
                }
            }
        }

        BlockDataType blockDataType = BlockDataType.valueOf(blockData.getDataType());

        if (blockDataType == BlockDataType.STAIRS) {
            Stairs stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.valueOf(blockData.getFacing()));
            state.setData(stairs);
        } else if (blockDataType == BlockDataType.FLOWERPOT) {
            setBlockFast(block.getWorld(), block.getX(), block.getY() - 1, block.getZ(), XMaterial.STONE, (byte) 0);
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_8) && ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_12)) {
                if (block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.AIR) {
                    setBlockFast(block.getWorld(), block.getX(), block.getY() - 1, block.getZ(), XMaterial.STONE, (byte) 0);
                }

                if (blockData.getFlower() != null && !blockData.getFlower().isEmpty()) {
                    try {
                        String[] flower = blockData.getFlower().split(":");
                        int materialData = Integer.parseInt(flower[1]);

                        materialStr = flower[0].toUpperCase();

                        ItemStack is = new ItemStack(Material.getMaterial(materialStr), 1, (byte) materialData);

                        World world = block.getWorld();

                        Class<?> blockPositionClass = ClassMapping.BLOCK_POSITION.getClazz();

                        Object worldHandle = MethodMapping.CB_GENERIC__GET_HANDLE.getMethod(world.getClass()).invoke(world);
                        Object blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ());
                        Object tileEntity = worldHandle.getClass().getMethod("getTileEntity", blockPositionClass).invoke(worldHandle, blockPosition);
                        Object itemStack = MethodMapping.CB_ITEM_STACK__AS_NMS_COPY.getMethod(ClassMapping.CRAFT_ITEM_STACK.getClazz()).invoke(null, is);
                        Object item = itemStack.getClass().getMethod("getItem").invoke(itemStack);
                        Object data = itemStack.getClass().getMethod("getData").invoke(itemStack);

                        Field aField = tileEntity.getClass().getDeclaredField("a");
                        aField.setAccessible(true);
                        aField.set(tileEntity, item);

                        Field fField = tileEntity.getClass().getDeclaredField("f");
                        fField.setAccessible(true);
                        fField.set(tileEntity, data);

                        tileEntity.getClass().getMethod("update").invoke(tileEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (blockData.getFlower() != null && !blockData.getFlower().isEmpty()) {
                    org.bukkit.material.FlowerPot flowerPot = (org.bukkit.material.FlowerPot) state.getData();
                    String[] flower = blockData.getFlower().split(":");
                    materialStr = null;

                    if (blockData.getVersion() > 12) {
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                            materialStr = flower[0].toUpperCase();
                        }
                    } else {
                        if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13)) {
                            materialStr = flower[0].toUpperCase();
                        }
                    }

                    if (materialStr != null) {
                        flowerPot.setContents(new MaterialData(Material.getMaterial(materialStr), (byte) Integer.parseInt(flower[1])));
                    }

                    state.setData(flowerPot);
                }
            }
        }

        if ("DOUBLE_PLANT".equals(materialStr)) {
            Block topBlock = block.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
            Block bottomBlock = block.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();

            if (bottomBlock.getType() == Material.AIR && !topBlock.getType().name().equals("DOUBLE_PLANT")) {
                bottomBlock.setType(XMaterial.LARGE_FERN.parseMaterial());

                if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13)) {
                    try {
                        bottomBlock.getClass().getMethod("setData", byte.class).invoke(bottomBlock, (byte) 2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static List<Block> getNearbyBlocks(Location loc, int rx, int ry, int rz) {
        final List<Block> nearbyBlocks = new ArrayList<>((rx + ry + rz) * 2);

        for (int x = -(rx); x <= rx; ++x) {
            for (int y = -(ry); y <= ry; ++y) {
                for (int z = -(rz); z <= rz; ++z) {
                    nearbyBlocks.add(new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z).getBlock());
                }
            }
        }

        return nearbyBlocks;
    }
}
