package com.songoda.skyblock.utils.world.block;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockData {

    private String material;
    private String blockData = "";
    private String biome;
    private String stateType = BlockStateType.NORMAL.toString();
    private String dataType = BlockDataType.NORMAL.toString();
    private String baseColor = Color.WHITE.toString();
    private String potionEffect = PotionEffectType.SPEED.toString();
    private String command = "";
    private String commandBlockName = "";
    private String entity = EntityType.COW.toString();
    private String exitLocation = "";
    private String flower = CompatibleMaterial.RED_DYE.getMaterial().toString() + ":0";
    private String playing = CompatibleMaterial.MUSIC_DISC_CHIRP.getMaterial().toString();
    private String[] signLines = {};
    private String rotateFace = BlockFace.NORTH.toString();
    private String skullOwner = "Notch";
    private String skullType = SkullType.PLAYER.toString();
    private String facing;
    private int charges = 0;

    private final Map<Integer, String> inventory = new HashMap<>();

    private int version;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private int brewingTime = 0;
    private int fuelLevel = 0;
    private int delay = 0;

    private byte data;

    private short burnTime = (short) 0;
    private short cookTime = (short) 0;

    private List<String> patterns = new ArrayList<>();

    private boolean exactTeleport = true;

    public BlockData(String material, byte data, int x, int y, int z, String biome) {
        this.material = material;
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;

        this.biome = biome;
    }

    public String getMaterial() {
        CompatibleMaterial material = CompatibleMaterial.getMaterial(this.material);
        return material == null ? this.material : CompatibleMaterial.getMaterial(this.material).getMaterial().name();
    }

    public void setMaterial(Material material) {
        this.material = material.name();
    }

    public String getBlockData() {
        return blockData;
    }

    public void setBlockData(String blockData) {
        this.blockData = blockData;
    }


    public String getBiome() {
        return this.biome;
    }

    public void setBiome(String biome) {
        this.biome = biome;
    }

    public String getStateType() {
        return this.stateType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getBaseColor() {
        return this.baseColor;
    }

    public void setBaseColor(String baseColor) {
        this.baseColor = baseColor;
    }

    public String getPotionEffect() {
        return this.potionEffect;
    }

    public void setPotionEffect(String potionEffect) {
        this.potionEffect = potionEffect;
    }

    public Map<Integer, String> getInventory() {
        return this.inventory;
    }

    public void addItem(int slot, String is) {
        this.inventory.put(slot, is);
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandBlockName() {
        return this.commandBlockName;
    }

    public void setCommandBlockName(String commandBlockName) {
        this.commandBlockName = commandBlockName;
    }

    public String getEntity() {
        return this.entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getExitLocation() {
        return this.exitLocation;
    }

    public void setExitLocation(String exitLocation) {
        this.exitLocation = exitLocation;
    }

    public String getFlower() {
        return this.flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getPlaying() {
        return this.playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String[] getSignLines() {
        return this.signLines;
    }

    public void setSignLines(String[] signLines) {
        this.signLines = signLines;
    }

    public String getRotateFace() {
        return this.rotateFace;
    }

    public void setRotateFace(String rotateFace) {
        this.rotateFace = rotateFace;
    }

    public String getSkullOwner() {
        return this.skullOwner;
    }

    public void setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
    }

    public String getSkullType() {
        return this.skullType;
    }

    public void setSkullType(String skullType) {
        this.skullType = skullType;
    }

    public String getFacing() {
        return this.facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getBrewingTime() {
        return this.brewingTime;
    }

    public void setBrewingTime(int brewingTime) {
        this.brewingTime = brewingTime;
    }

    public int getFuelLevel() {
        return this.fuelLevel;
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public byte getData() {
        return this.data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public short getBurnTime() {
        return this.burnTime;
    }

    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    public short getCookTime() {
        return this.cookTime;
    }

    public void setCookTime(short cookTime) {
        this.cookTime = cookTime;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public boolean isExactTeleport() {
        return this.exactTeleport;
    }

    public void setExactTeleport(boolean exactTeleport) {
        this.exactTeleport = exactTeleport;
    }
    
    public int getCharges() {
        return charges;
    }
    
    public void setCharges(int charges) {
        this.charges = charges;
    }
}
