package com.songoda.skyblock.world.generator;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandWorld;

public class VoidGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        final ChunkData chunkData = createChunkData(world);

        final SkyBlock skyblock = SkyBlock.getInstance();
        final Configuration configLoad = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration();
        final ConfigurationSection worldSection = configLoad.getConfigurationSection("Island.World");

        for (IslandWorld worldList : IslandWorld.values()) {
            if (world.getEnvironment() == worldList.getUncheckedEnvironment()) {

                ConfigurationSection section = worldSection.getConfigurationSection(worldList.name());

                if (section.getBoolean("Liquid.Enable")) {
                    if (section.getBoolean("Liquid.Lava")) {
                        setBlock(chunkData, CompatibleMaterial.LAVA.getBlockMaterial(), section.getInt("Liquid.Height"));
                    } else {
                        setBlock(chunkData, CompatibleMaterial.WATER.getBlockMaterial(), section.getInt("Liquid.Height"));
                    }
                }

                break;
            }
        }

        return chunkData;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return Arrays.asList(new BlockPopulator[0]);
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        return new byte[world.getMaxHeight() / 16][];
    }

    private void setBlock(ChunkData chunkData, Material material, int height) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < height; y++) {
                    chunkData.setBlock(x, y, z, material);
                }
            }
        }
    }
}
