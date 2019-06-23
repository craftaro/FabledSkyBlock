package me.goodandevil.skyblock.limit;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.utils.version.Materials;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LimitManager {

    private final SkyBlock skyblock;
    private Map<Materials, Long> blockLimits;

    public LimitManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
        this.blockLimits = new HashMap<>();

        this.reload();
    }

    public void reload() {
        this.blockLimits.clear();

        FileConfiguration limitsConfig = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "limits.yml")).getFileConfiguration();
        ConfigurationSection blockLimitSection = limitsConfig.getConfigurationSection("block");

        if (blockLimitSection != null) {
            for (String materialString : blockLimitSection.getKeys(false)) {
                Materials material = Materials.fromString(materialString);
                if (material != null) {
                    long limit = blockLimitSection.getLong(materialString);
                    this.blockLimits.put(material, limit);
                }
            }
        }
    }

    /**
     * Gets the max number of a type of block a player can place
     * @param player The player to check
     * @param block The block to check
     * @return The max number of the type of block the player can place
     */
    public long getBlockLimit(Player player, Block block) {
        if (player.hasPermission("fabledskyblock.limit.block.*"))
            return -1;

        long limit = -1;
        Materials material = Materials.getMaterials(block.getType(), block.getData());

        if (this.blockLimits.containsKey(material))
            limit = Math.max(limit, this.blockLimits.get(material));

        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions()
                .stream()
                .filter(x -> x.getPermission().toLowerCase().startsWith("fabledskyblock.limit.block." + material.name().toLowerCase()))
                .collect(Collectors.toSet());

        for (PermissionAttachmentInfo permission : permissions) {
            try {
                String permString = permission.getPermission();
                String numberString = permString.substring(permString.lastIndexOf(".") + 1);
                if (numberString.equals("*"))
                    return -1;

                limit = Math.max(limit, Integer.parseInt(numberString));
            } catch (Exception ignored) { }
        }

        return limit;
    }

    /**
     * Checks if a player has exceeded the number of blocks they can place
     * @param player The player to check
     * @param block The block to check
     * @return true if the player has exceeded the block limit, otherwise false
     */
    public boolean isBlockLimitExceeded(Player player, Block block) {
        IslandManager islandManager = this.skyblock.getIslandManager();

        long limit = this.getBlockLimit(player, block);
        if (limit == -1)
            return false;

        Island island = islandManager.getIslandAtLocation(block.getLocation());
        long totalPlaced;
        if (block.getType() == Materials.SPAWNER.parseMaterial()) {
            totalPlaced = island.getLevel().getMaterials().entrySet().stream().filter(x -> x.getKey().contains("SPAWNER")).mapToLong(Map.Entry::getValue).sum();
        } else {
            totalPlaced = island.getLevel().getMaterialAmount(Materials.getMaterials(block.getType(), block.getData()).name());
        }

        return limit < totalPlaced + 1;
    }

}
