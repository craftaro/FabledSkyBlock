package com.songoda.skyblock.limit.impl;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.limit.EnumLimitation;
import com.songoda.skyblock.utils.player.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.Set;


public final class BlockLimitation extends EnumLimitation<CompatibleMaterial> {

    public BlockLimitation() {
        super(CompatibleMaterial.class);
    }

    @Override
    public String getSectionName() {
        return "block";
    }

    @Override
    public boolean hasTooMuch(long currentAmount, Enum<CompatibleMaterial> type) {
        throw new UnsupportedOperationException("Not implemented. Use getBlockLimit and isBlockLimitExceeded instead.");
    }

    @Override
    public void reload(ConfigurationSection loadFrom) {
        unload();

        if (loadFrom == null) return;

        final Set<String> keys = loadFrom.getKeys(false);

        removeAndLoadDefaultLimit(loadFrom, keys);

        for (String key : keys) {
            final String enumName = key.toUpperCase(Locale.ENGLISH);
            CompatibleMaterial type = CompatibleMaterial.getMaterial(enumName);

            if (type == null)
                throw new IllegalArgumentException("Unable to parse Materials from '" + enumName + "' in the Section '" + loadFrom.getCurrentPath() + "'");
    
            getMap().put(type, loadFrom.getLong(key));
        }

    }

    @Deprecated
    public long getBlockLimit(Player player, Block block) {
        return this.getBlockLimit(player, block.getType());
    }

    public long getBlockLimit(Player player, Material type) {
        if (player == null || type == null) return -1;

        if (player.hasPermission("fabledskyblock.limit.block.*")) return -1;

        CompatibleMaterial material = null;
        if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (type.toString().toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER;
                    break;
            }
        }
        if(material == null) {
            material = CompatibleMaterial.getMaterial(type);
        }

        if (material == null) return -1;

        final String name = material.name().toLowerCase();

        return Math.max(getMap().getOrDefault(material, getDefault()), PlayerUtil.getNumberFromPermission(player, "fabledskyblock.limit.block." + name, true, -1));
    }

    public boolean isBlockLimitExceeded(Block block, long limit) {
        return this.isBlockLimitExceeded(block.getType(), block.getLocation(), limit);
    }

    public boolean isBlockLimitExceeded(Material type, Location loc, long limit) {
        if (limit == -1) return false;

        final IslandManager islandManager = SkyBlock.getInstance().getIslandManager();
        final Island island = islandManager.getIslandAtLocation(loc);
        final long totalPlaced;

        if (type == CompatibleMaterial.SPAWNER.getBlockMaterial()) {
            totalPlaced = island.getLevel().getMaterials().entrySet().stream().filter(x -> x.getKey().contains("SPAWNER")).mapToLong(Map.Entry::getValue).sum();
        } else {
            CompatibleMaterial material = null;
            if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                switch (type.toString().toUpperCase()) {
                    case "DIODE_BLOCK_OFF":
                    case "DIODE_BLOCK_ON":
                        material = CompatibleMaterial.REPEATER;
                        break;
                }
            }
            if(material == null) {
                material = CompatibleMaterial.getMaterial(type);
            }
            totalPlaced = island.getLevel().getMaterialAmount(material.name());
        }

        return limit <= totalPlaced;
    }

}
