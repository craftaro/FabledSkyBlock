package com.songoda.skyblock.stackable;

import java.io.File;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.version.Sounds;

public class Stackable {

    private UUID uuid;

    private Location location;
    private Materials material;
    private int size = 2;
    private ArmorStand display;
    private int maxSize;

    public Stackable(Location location, Materials material) {
        this.uuid = UUID.randomUUID();
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.material = material;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    public Stackable(Location location, Materials material, int maxSize) {
        this.uuid = UUID.randomUUID();
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.material = material;
        this.maxSize = maxSize;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    public Stackable(UUID uuid, Location location, Materials material, int size) {
        this.uuid = uuid;
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.material = material;
        this.size = size;
        this.updateDisplay();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Materials getMaterial() {
        return this.material;
    }

    public void setMaterial(Materials material) {
        this.material = material;
        this.save();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.updateDisplay();
        this.save();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addOne() {
        this.size++;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(this.location, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    public void takeOne() {
        this.size--;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(this.location, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    public boolean isMaxSize(){
        return size > maxSize;
    }

    private void updateDisplay() {
        // The chunk needs to be loaded otherwise the getNearbyEntities() in
        // removeDisplay() won't find anything
        if (!this.location.getWorld().isChunkLoaded(this.location.getBlockX() >> 4, this.location.getBlockZ() >> 4)) this.location.getChunk().load();

        if (this.size > 1) {

            if (display == null || !display.isValid()) {
                this.createDisplay();
            } else {
                this.display.setCustomName(this.getCustomName());
                this.display.setCustomNameVisible(true);
            }

        } else {
            this.removeDisplay();
        }
    }

    private void createDisplay() {
        this.removeDisplay();

        Location dropLocation = this.location.clone().add(0.5, 1, 0.5);
        ArmorStand as = (ArmorStand) this.location.getWorld().spawnEntity(dropLocation, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);
        if (NMSUtil.getVersionNumber() > 8) {
            as.setMarker(true);
        }
        as.setBasePlate(true);
        as.setHelmet(material.parseItem());
        as.setCustomName(this.getCustomName());
        as.setCustomNameVisible(true);
        as.setMetadata("StackableArmorStand", new FixedMetadataValue(SkyBlock.getInstance(), ""));
        this.display = as;
    }

    private void removeDisplay() {
        if (this.display != null) {
            this.display.remove();
        }

        // Find any stragglers
        for (Entity entity : this.location.getWorld().getNearbyEntities(this.location.clone().add(0.5, 0.55, 0.5), 0.25, 0.5, 0.25))
            if (entity instanceof ArmorStand) entity.remove();
    }

    private void save() {
        File configFile = new File(SkyBlock.getInstance().getDataFolder().toString() + "/island-data");
        FileManager.Config config = SkyBlock.getInstance().getFileManager().getConfig(new File(configFile, SkyBlock.getInstance().getIslandManager().getIslandAtLocation(this.location).getOwnerUUID() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (this.getSize() == 0) {
            configLoad.set("Stackables." + this.getUuid().toString(), null);
        } else {
            ConfigurationSection section = configLoad.createSection("Stackables." + this.getUuid().toString());
            section.set("Location", this.getLocation());
            section.set("Size", this.getSize());
        }
    }

    private String getCustomName() {
        return ChatColor
                .translateAlternateColorCodes('&', SkyBlock.getInstance().getFileManager().getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml")).getFileConfiguration().getString("Hologram.Stackable.Message"))
                .replace("%block", SkyBlock.getInstance().getLocalizationManager().getLocalizationFor(Materials.class).getLocale(material)).replace("%amount", NumberUtil.formatNumber(this.size));
    }
}
