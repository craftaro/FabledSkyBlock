package com.songoda.skyblock.generator;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorManager {

    private final SkyBlock skyblock;
    private List<Generator> generatorStorage = new ArrayList<>();

    public GeneratorManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
        registerGenerators();
    }

    public void registerGenerators() {
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "generators.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Generators") == null)
            return;

        Materials[] oreMaterials = new Materials[]{Materials.COAL, Materials.CHARCOAL, Materials.DIAMOND,
                Materials.IRON_INGOT, Materials.GOLD_INGOT, Materials.EMERALD};
        Random rnd = new Random();

        for (String generatorList : configLoad.getConfigurationSection("Generators").getKeys(false)) {
            if (configLoad.getString("Generators." + generatorList + ".Name") == null)
                continue;

            List<GeneratorMaterial> generatorMaterials = new ArrayList<>();
            if (configLoad.getString("Generators." + generatorList + ".Materials") != null) {
                for (String materialList : configLoad.getConfigurationSection("Generators." + generatorList + ".Materials").getKeys(false)) {
                    Materials materials = Materials.fromString(materialList);
                    if (materials != null) {
                        generatorMaterials.add(new GeneratorMaterial(materials, configLoad.getDouble(
                                "Generators." + generatorList + ".Materials." + materialList + ".Chance")));
                    }
                }
            }

            generatorStorage.add(new Generator(configLoad.getString("Generators." + generatorList + ".Name"),
                    oreMaterials[rnd.nextInt(oreMaterials.length)], generatorMaterials,
                    configLoad.getBoolean("Generators." + generatorList + ".Permission")));
        }
    }

    public void unregisterGenerators() {
        generatorStorage.clear();
    }

    private boolean isFlowingTowardsBlock(Block from) {
        if (!from.isLiquid())
            return false;

        return isWater(from) && isFlowingBlock(from);
    }

    private boolean isLava(Block block) {
        return block.getType().name().contains("LAVA");
    }

    private boolean isWater(Block block) {
        return block.getType().name().contains("WATER");
    }

    public boolean isGenerator(Block block) {
        BlockFace[] blockFaces = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

        for (BlockFace blockFace1 : blockFaces) {
            for (BlockFace blockFace2 : blockFaces) {
                if (blockFace1.equals(blockFace2))
                    continue;

                Block from1 = block.getRelative(blockFace1);
                Block from2 = block.getRelative(blockFace2);
                if (isLava(from1) && isWater(from2) && isFlowingTowardsBlock(from2))
                    return true;
            }
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    private int getLiquidLevel(Block block) {
        if (NMSUtil.getVersionNumber() > 12 && block.getState().getBlockData() instanceof Levelled) {
            Levelled levelled = (Levelled) block.getState().getBlockData();
            return levelled.getLevel();
        } else {
            return block.getData();
        }
    }

    private boolean isFlowingBlock(Block block) {
        return getLiquidLevel(block) != 0;
    }

    @SuppressWarnings("deprecation")
    public BlockState generateBlock(Generator generator, Block block) {
        Materials materials = getRandomMaterials(generator);
        if (materials == null)
            return block.getState();

        skyblock.getSoundManager().playSound(block.getLocation(), Sounds.FIZZ.bukkitSound(), 1.0F, 10.0F);

        if (NMSUtil.getVersionNumber() > 12) {
            block.setType(materials.parseMaterial());
        } else {
            ItemStack is = materials.parseItem();
            block.setType(is.getType());

            try {
                block.getClass().getMethod("setData", byte.class).invoke(block, (byte) is.getDurability());
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }

        return block.getState();
    }

    public Materials getRandomMaterials(Generator generator) {
        if (generator.getGeneratorMaterials() != null && generator.getGeneratorMaterials().stream().anyMatch(x -> x.getChance() > 0)) {
            List<Materials> weightedList = new ArrayList<>();
            for (GeneratorMaterial generatorMaterial : generator.getGeneratorMaterials())
                for (int i = 0; i < generatorMaterial.getChance() * 30; i++)
                    weightedList.add(generatorMaterial.getMaterials());

            int choice = new Random().nextInt(weightedList.size());
            return weightedList.get(choice);
        }

        return Materials.COBBLESTONE;
    }

    public void addGenerator(String name, List<GeneratorMaterial> generatorMaterials, boolean permission) {
        Materials[] oreMaterials = new Materials[]{Materials.COAL, Materials.CHARCOAL, Materials.DIAMOND,
                Materials.IRON_INGOT, Materials.GOLD_INGOT, Materials.EMERALD};
        generatorStorage.add(new Generator(name, oreMaterials[new Random().nextInt(oreMaterials.length)],
                generatorMaterials, permission));
    }

    public void removeGenerator(Generator generator) {
        generatorStorage.remove(generator);
    }

    public Generator getGenerator(String name) {
        for (Generator generatorList : generatorStorage) {
            if (generatorList.getName().equalsIgnoreCase(name)) {
                return generatorList;
            }
        }

        return null;
    }

    public boolean containsGenerator(String name) {
        for (Generator generatorList : generatorStorage) {
            if (generatorList.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public List<Generator> getGeneratorStorage() {
        return generatorStorage;
    }

    public List<Generator> getGenerators() {
        return generatorStorage;
    }
}
