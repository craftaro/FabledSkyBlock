package com.songoda.skyblock.world.generator;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {

    @Override
    public @Nonnull ChunkData generateChunkData(@Nonnull World world, @Nonnull Random random, int chunkX, int chunkZ, @Nonnull BiomeGrid biomeGrid) {
        final ChunkData chunkData = createChunkData(world);

        final SkyBlock plugin = SkyBlock.getInstance();
        final Configuration configLoad = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration();
        final ConfigurationSection worldSection = configLoad.getConfigurationSection("Island.World");
        
        Biome biome;
        
        switch (world.getEnvironment()) {
            case NORMAL:
                biome = CompatibleBiome.valueOf(configLoad.getString("Island.Biome.Default.Type", "PLAINS").toUpperCase()).getBiome();
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
        
        if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) { // TODO Should be 1.15 but it works fine there
            setChunkBiome3D(biome, biomeGrid, world);
        } else {
            setChunkBiome2D(biome, biomeGrid);
        }
        

        for (IslandWorld worldList : IslandWorld.values()) {
            if (world.getEnvironment() == World.Environment.NETHER
                    || world.getEnvironment() == World.Environment.NORMAL
                    || world.getEnvironment() == World.Environment.THE_END) {

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
    
    // Do not use - Too laggy
    private void setChunkBiome3D(Biome biome, BiomeGrid grid, World world) {
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(int y = 0; y < world.getMaxHeight(); ++y) {
                    grid.setBiome(z, y, z, biome);
                }
            }
        }
    }
    
    private void setChunkBiome2D(Biome biome, BiomeGrid grid) {
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                if(!grid.getBiome(x, z).equals(biome)){
                    grid.setBiome(x, z, biome);
                }
            }
        }
    }
}
