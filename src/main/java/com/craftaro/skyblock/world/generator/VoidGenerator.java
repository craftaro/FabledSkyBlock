package com.craftaro.skyblock.world.generator;

import com.craftaro.core.compatibility.CompatibleBiome;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.IslandWorld;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    private final IslandWorld islandWorld;
    private final SkyBlock plugin;

    public VoidGenerator(IslandWorld islandWorld, SkyBlock plugin) {
        this.islandWorld = islandWorld;
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    public ChunkData generateChunkData(@Nonnull World world, @Nonnull Random random, int chunkX, int chunkZ, @Nonnull BiomeGrid biomeGrid) {
        final ChunkData chunkData = createChunkData(world);

        final Configuration configLoad = this.plugin.getConfiguration();
        final ConfigurationSection worldSection = configLoad.getConfigurationSection("Island.World");

        Biome biome;

        switch (world.getEnvironment()) {
            case NORMAL:
                biome = Arrays.stream(CompatibleBiome.values())
                        .filter(compatibleBiome -> compatibleBiome.name().equals(configLoad.getString("Island.Biome.Default.Type", "PLAINS").toUpperCase()) && compatibleBiome.isCompatible())
                        .findFirst()
                        .orElse(CompatibleBiome.PLAINS).getBiome();
                break;
            case NETHER:
                biome = CompatibleBiome.NETHER_WASTES.getBiome();
                break;
            case THE_END:
                biome = CompatibleBiome.THE_END.getBiome();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + world.getEnvironment());
        }

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) { // TODO Should be 1.15 but it works fine there
            setChunkBiome3D(biome, biomeGrid, world);
        } else {
            setChunkBiome2D(biome, biomeGrid);
        }

        ConfigurationSection section = worldSection.getConfigurationSection(this.islandWorld.getFriendlyName());

        if (section.getBoolean("Liquid.Enable")) {
            if (section.getBoolean("Liquid.Lava")) {
                setBlock(chunkData, XMaterial.LAVA.parseMaterial(), section.getInt("Liquid.Height"));
            } else {
                setBlock(chunkData, XMaterial.WATER.parseMaterial(), section.getInt("Liquid.Height"));
            }
        }


        return chunkData;
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(final @NotNull World world) {
        return Collections.emptyList();
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        return new byte[world.getMaxHeight() / 16][];
    }

    private void setBlock(ChunkData chunkData, Material material, int height) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < height; ++y) {
                    chunkData.setBlock(x, y, z, material);
                }
            }
        }
    }

    // Do not use - Too laggy
    private void setChunkBiome3D(Biome biome, BiomeGrid grid, World world) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < world.getMaxHeight(); ++y) {
                    grid.setBiome(z, y, z, biome);
                }
            }
        }
    }

    private void setChunkBiome2D(Biome biome, BiomeGrid grid) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                if (!grid.getBiome(x, z).equals(biome)) {
                    grid.setBiome(x, z, biome);
                }
            }
        }
    }
}
