package com.craftaro.skyblock.limit.impl;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.limit.EnumLimitation;
import com.craftaro.skyblock.utils.player.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public final class BlockLimitation extends EnumLimitation<XMaterial> {
    public BlockLimitation() {
        super(XMaterial.class);
    }

    @Override
    public String getSectionName() {
        return "block";
    }

    @Override
    public boolean hasTooMuch(long currentAmount, Enum<XMaterial> type) {
        throw new UnsupportedOperationException("Not implemented. Use getBlockLimit and isBlockLimitExceeded instead.");
    }

    @Override
    public void reload(ConfigurationSection loadFrom) {
        unload();
        if (loadFrom == null) {
            return;
        }

        final Set<String> keys = loadFrom.getKeys(false);

        removeAndLoadDefaultLimit(loadFrom, keys);

        for (String key : keys) {
            final String enumName = key.toUpperCase(Locale.ENGLISH);
            Optional<XMaterial> type = CompatibleMaterial.getMaterial(enumName);

            if (!type.isPresent()) {
                throw new IllegalArgumentException("Unable to parse Materials from '" + enumName + "' in the Section '" + loadFrom.getCurrentPath() + "'");
            }

            getMap().put(type.get(), loadFrom.getLong(key));
        }
    }

    @Deprecated
    public long getBlockLimit(Player player, Block block) {
        return this.getBlockLimit(player, block.getType());
    }

    public long getBlockLimit(Player player, Material type) {
        if (player == null || type == null) {
            return -1;
        }

        if (player.hasPermission("fabledskyblock.limit.block.*")) {
            return -1;
        }

        XMaterial material = null;
        if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (type.toString().toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = XMaterial.REPEATER;
                    break;
            }
        }
        if (material == null) {
            Optional<XMaterial> material1 = CompatibleMaterial.getMaterial(type);
            if (!material1.isPresent()) {
                return -1;
            }
            material = material1.get();
        }

        final String name = material.name().toLowerCase();

        return Math.max(getMap().getOrDefault(material, getDefault()), PlayerUtil.getNumberFromPermission(player, "fabledskyblock.limit.block." + name, true, -1));
    }

    public boolean isBlockLimitExceeded(Block block, long limit) {
        return this.isBlockLimitExceeded(CompatibleMaterial.getMaterial(block.getType()).get(), block.getLocation(), limit);
    }

    public boolean isBlockLimitExceeded(XMaterial type, Location loc, long limit) {
        if (limit == -1) {
            return false;
        }

        final IslandManager islandManager = SkyBlock.getPlugin(SkyBlock.class).getIslandManager();
        final Island island = islandManager.getIslandAtLocation(loc);
        final long totalPlaced;

        if (type == XMaterial.SPAWNER) {
            totalPlaced = island.getLevel().getMaterials().entrySet().stream().filter(x -> x.getKey().contains("SPAWNER")).mapToLong(Map.Entry::getValue).sum();
        } else {
            XMaterial material = null;
            if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                switch (type.toString().toUpperCase()) {
                    case "DIODE_BLOCK_OFF":
                    case "DIODE_BLOCK_ON":
                        material = XMaterial.REPEATER;
                        break;
                }
            }
            if (material == null) {
                material = type;
            }
            totalPlaced = island.getLevel().getMaterialAmount(material.name());
        }

        return limit <= totalPlaced;
    }
}
