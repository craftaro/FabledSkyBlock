package me.goodandevil.skyblock.generator;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

        if (configLoad.getString("Generators") == null) return;

        Materials[] oreMaterials = new Materials[]{Materials.COAL, Materials.CHARCOAL, Materials.DIAMOND,
                Materials.IRON_INGOT, Materials.GOLD_INGOT, Materials.EMERALD};
        Random rnd = new Random();

        for (String generatorList : configLoad.getConfigurationSection("Generators").getKeys(false)) {
            if (configLoad.getString("Generators." + generatorList + ".Name") == null) continue;
            List<GeneratorMaterial> generatorMaterials = new ArrayList<>();

            if (configLoad.getString("Generators." + generatorList + ".Materials") != null) {
                for (String materialList : configLoad
                        .getConfigurationSection("Generators." + generatorList + ".Materials").getKeys(false)) {
                    Materials materials = Materials.fromString(materialList);

                    if (materials != null) {
                        generatorMaterials.add(new GeneratorMaterial(materials, configLoad.getInt(
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

        if (isWater(from) && isFlowingBlock(from))
            return true;

        return false;
    }

    private boolean isLava(Block block) {
        return block.getType().equals(Materials.LAVA.parseMaterial()) || block.getType().equals(Materials.LEGACY_STATIONARY_LAVA.parseMaterial());
    }

    private boolean isWater(Block block) {
        return block.getType().equals(Materials.WATER.parseMaterial()) || block.getType().equals(Materials.LEGACY_STATIONARY_WATER.parseMaterial());
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

        //region GoodAndEvil his old code (garbage)
		/*
		if (block.getRelative(BlockFace.UP).getType() != Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
				&& block.getRelative(BlockFace.UP).getType() != Materials.WATER.parseMaterial()) {
			Block flowBlock = null;

			if ((block.getRelative(BlockFace.EAST).getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
					|| block.getRelative(BlockFace.EAST).getType() == Materials.WATER.parseMaterial())
					&& (block.getRelative(BlockFace.WEST).getType() == Materials.LEGACY_STATIONARY_LAVA
							.getPostMaterial()
							|| block.getRelative(BlockFace.WEST).getType() == Materials.LAVA.parseMaterial())
					&& (block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(BlockFace.EAST)
							.getType() == Material.AIR
							|| block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock()
									.getRelative(BlockFace.EAST)
									.getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial())) {
				if (!isFlowingTowardsBlock(block, BlockFace.NORTH, BlockFace.SOUTH)) {
					return false;
				} else if (!isFlowingTowardsBlock(block, BlockFace.SOUTH, BlockFace.NORTH)) {
					return false;
				}

				flowBlock = block.getRelative(BlockFace.EAST);
			} else if ((block.getRelative(BlockFace.EAST).getType() == Materials.LEGACY_STATIONARY_LAVA
					.getPostMaterial() || block.getRelative(BlockFace.EAST).getType() == Materials.LAVA.parseMaterial())
					&& (block.getRelative(BlockFace.WEST).getType() == Materials.LEGACY_STATIONARY_WATER
							.getPostMaterial()
							|| block.getRelative(BlockFace.WEST).getType() == Materials.WATER.parseMaterial())
					&& (block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(BlockFace.WEST)
							.getType() == Material.AIR
							|| block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock()
									.getRelative(BlockFace.WEST)
									.getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial())) {
				if (!isFlowingTowardsBlock(block, BlockFace.NORTH, BlockFace.SOUTH)) {
					return false;
				} else if (!isFlowingTowardsBlock(block, BlockFace.SOUTH, BlockFace.NORTH)) {
					return false;
				}

				flowBlock = block.getRelative(BlockFace.WEST);
			} else if (((block.getRelative(BlockFace.NORTH).getType() == Materials.LEGACY_STATIONARY_WATER
					.getPostMaterial()
					|| block.getRelative(BlockFace.NORTH).getType() == Materials.WATER.parseMaterial()))
					&& (block.getRelative(BlockFace.SOUTH).getType() == Materials.LEGACY_STATIONARY_LAVA
							.getPostMaterial()
							|| block.getRelative(BlockFace.SOUTH).getType() == Materials.LAVA.parseMaterial())
					&& (block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(BlockFace.NORTH)
							.getType() == Material.AIR
							|| block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock()
									.getRelative(BlockFace.NORTH)
									.getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial())) {
				if (!isFlowingTowardsBlock(block, BlockFace.WEST, BlockFace.EAST)) {
					return false;
				} else if (!isFlowingTowardsBlock(block, BlockFace.EAST, BlockFace.WEST)) {
					return false;
				}

				flowBlock = block.getRelative(BlockFace.NORTH);
			} else if (((block.getRelative(BlockFace.NORTH).getType() == Materials.LEGACY_STATIONARY_LAVA
					.getPostMaterial()
					|| block.getRelative(BlockFace.NORTH).getType() == Materials.LAVA.parseMaterial()))
					&& (block.getRelative(BlockFace.SOUTH).getType() == Materials.LEGACY_STATIONARY_WATER
							.getPostMaterial()
							|| block.getRelative(BlockFace.SOUTH).getType() == Materials.WATER.parseMaterial())
					&& (block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(BlockFace.SOUTH)
							.getType() == Material.AIR
							|| block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock()
									.getRelative(BlockFace.SOUTH)
									.getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial())) {
				if (!isFlowingTowardsBlock(block, BlockFace.WEST, BlockFace.EAST)) {
					return false;
				} else if (!isFlowingTowardsBlock(block, BlockFace.EAST, BlockFace.WEST)) {
					return false;
				}

				flowBlock = block.getRelative(BlockFace.SOUTH);
			}

			if (flowBlock != null) {
				return isFlowingBlock(flowBlock);
			}
		}

		return false;
	}

	private boolean isFlowingTowardsBlock(Block block, BlockFace blockFace1, BlockFace blockFace2) {
		if (block.getRelative(blockFace1).getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
				|| block.getRelative(blockFace1).getType() == Materials.WATER.parseMaterial()) {
			if (isFlowingBlock(block.getRelative(blockFace1)) && (block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D)
					.getBlock().getRelative(blockFace1).getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
					|| block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(blockFace1)
							.getType() == Materials.WATER.parseMaterial())) {
				if (block.getRelative(blockFace2).getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
						|| block.getRelative(blockFace2).getType() == Materials.WATER.parseMaterial()) {
					if (isFlowingBlock(block.getRelative(blockFace2)) && (block.getLocation().clone()
							.subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(blockFace2)
							.getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
							|| block.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getRelative(blockFace2)
									.getType() == Materials.WATER.parseMaterial())) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		return true;
		*/
        //endregion
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
    public void generateBlock(Generator generator, Block block) {
        block.setType(Material.AIR);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, () -> {

            Materials materials = getRandomMaterials(generator);

            if (materials == null) return;
            Bukkit.getScheduler().runTask(skyblock, () -> {
                skyblock.getSoundManager().playSound(block.getLocation(), Sounds.FIZZ.bukkitSound(),
                        1.0F, 10.0F);

                if (NMSUtil.getVersionNumber() > 12) {
                    block.setType(materials.parseMaterial());
                } else {
                    ItemStack is = materials.parseItem();
                    block.setType(is.getType());

                    try {
                        block.getClass().getMethod("setData", byte.class).invoke(block,
                                (byte) is.getDurability());
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public Materials getRandomMaterials(Generator generator) {
        if (generator.getGeneratorMaterials() != null && generator.getGeneratorMaterials().size() != 0) {
            Map<Integer, Integer> chances = new HashMap<>();

            for (int index = 0; index < generator.getGeneratorMaterials().size(); index++) {
                GeneratorMaterial generatorMaterial = generator.getGeneratorMaterials().get(index);

                for (int i = 0; i < generatorMaterial.getChance(); i++) {
                    chances.put(chances.size() + 1, index);
                }
            }

            if (chances.size() != 0) {
                int rndNum = new Random().nextInt(chances.size());

                if (rndNum != 0) {
                    return generator.getGeneratorMaterials().get(chances.get(rndNum)).getMaterials();
                }
            }
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
