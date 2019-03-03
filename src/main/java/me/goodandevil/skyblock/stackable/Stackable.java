package me.goodandevil.skyblock.stackable;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Stackable {

    private UUID uuid;

    private Location location;
    private Material material;
    private Integer size = 2;
    private ArmorStand display;

    public Stackable(Location location, Material material) {
        this.uuid = UUID.randomUUID();
        this.location = location;
        this.material = material;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    public Stackable(UUID uuid, Location location, Material material, int size) {
        this.uuid = uuid;
        this.location = location;
        this.material = material;
        this.size = size;
        this.updateDisplay();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return location.clone();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        this.save();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
        this.updateDisplay();
        this.save();
    }

    public void addOne() {
        this.size ++;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    public void takeOne() {
        this.size --;
        this.updateDisplay();
        SkyBlock.getInstance().getSoundManager().playSound(location, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
        this.save();
    }

    private void updateDisplay() {
        if (this.size > 1) {
            if (this.display == null && !this.findExistingDisplay()) {
                this.createDisplay();
            }
            
            if (this.display.isDead()) {
                this.createDisplay();
            }
            
            this.display.setCustomName(WordUtils.capitalize(material.name().toLowerCase()).replace("_", " ") + "s: " + size);
            this.display.setCustomNameVisible(true);
        } else {
            this.removeDisplay();
        }
    }
    
    private void createDisplay() {
        this.removeDisplay();
        
        Location dropLocation = location.clone().add(0.5, 1, 0.5);
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(dropLocation, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);
        if (NMSUtil.getVersionNumber() > 8) {
            as.setMarker(true);
        }
        as.setBasePlate(true);
        as.setHelmet(new ItemStack(material));
        as.setCustomName(WordUtils.capitalize(material.name().toLowerCase()).replace("_", " ") + "s: " + size);
        as.setCustomNameVisible(true);
        this.display = as;
    }
    
    private boolean findExistingDisplay() {
        for (Entity entity : this.location.getWorld().getNearbyEntities(this.location.clone().add(0.5, 0.55, 0.5), 0.1, 0.5, 0.1)) {
            if (entity instanceof ArmorStand && !entity.isDead()) {
                this.display = (ArmorStand) entity;
                return true;
            }
        }
        return false;
    }

    private void removeDisplay() {
        if (this.display != null) {
            this.display.remove();
        }
    }

    private void save() {
        File configFile = new File(SkyBlock.getInstance().getDataFolder().toString() + "/island-data");
        FileManager.Config config = SkyBlock.getInstance().getFileManager().getConfig(new File(configFile,
                SkyBlock.getInstance().getIslandManager().getIslandAtLocation(location).getOwnerUUID() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        
        if (getSize() == 0) {
            configLoad.set("Stackables." + getUuid().toString(), null);
        } else {
            ConfigurationSection section = configLoad.createSection("Stackables." + getUuid().toString());
            section.set("Location", getLocation());
            section.set("Material", getMaterial().name());
            section.set("Size", getSize());
        }
    }
}
