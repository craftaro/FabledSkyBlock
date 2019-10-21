package com.songoda.skyblock.limit.impl;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.limit.EnumLimitation;
import com.songoda.skyblock.utils.version.Materials;

public final class BlockLimitation extends EnumLimitation<Materials> {

    public BlockLimitation() {
        super(Materials.class);
    }

    @Override
    public String getSectionName() {
        return "block";
    }

    @Override
    public boolean hasTooMuch(long currentAmount, Enum<Materials> type) {
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
            final Materials type = Materials.fromString(enumName);

            if (type == null) throw new IllegalArgumentException("Unable to parse Materials from '" + enumName + "' in " + loadFrom.getCurrentPath());

            getMap().put(type, loadFrom.getLong(key));
        }

    }

    @SuppressWarnings("deprecation")
    public long getBlockLimit(Player player, Block block) {
        if (player == null || block == null) return -1;

        if (player.hasPermission("fabledskyblock.limit.block.*")) return -1;

        final Materials material = Materials.getMaterials(block.getType(), block.getData());

        if (material == null) return -1;

        long limit = getMap().getOrDefault(material, getDefault());

        final String name = material.name().toLowerCase();

        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions().stream()
                .filter(x -> x.getPermission().toLowerCase().startsWith("fabledskyblock.limit.block." + name)).collect(Collectors.toSet());

        for (PermissionAttachmentInfo permission : permissions) {
            try {
                String permString = permission.getPermission();
                String numberString = permString.substring(permString.lastIndexOf(".") + 1);
                if (numberString.equals("*")) return -1;

                limit = Math.max(limit, Integer.parseInt(numberString));
            } catch (Exception ignored) {
            }
        }

        return limit;
    }

    @SuppressWarnings("deprecation")
    public boolean isBlockLimitExceeded(Player player, Block block, long limit) {

        if (limit == -1) return false;

        final IslandManager islandManager = SkyBlock.getInstance().getIslandManager();
        final Island island = islandManager.getIslandAtLocation(block.getLocation());
        final long totalPlaced;

        if (block.getType() == Materials.SPAWNER.parseMaterial()) {
            totalPlaced = island.getLevel().getMaterials().entrySet().stream().filter(x -> x.getKey().contains("SPAWNER"))
                    .mapToLong(Map.Entry::getValue).sum();
        } else {
            totalPlaced = island.getLevel().getMaterialAmount(Materials.getMaterials(block.getType(), block.getData()).name());
        }

        return limit < totalPlaced + 1;
    }

}
