package com.songoda.skyblock.levelling.rework;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.levelling.rework.amount.AmountMaterialPair;
import com.songoda.skyblock.levelling.rework.calculator.Calculator;
import com.songoda.skyblock.levelling.rework.calculator.CalculatorRegistry;
import com.songoda.skyblock.levelling.rework.calculator.impl.EpicSpawnerCalculator;
import com.songoda.skyblock.levelling.rework.calculator.impl.UltimateStackerCalculator;
import com.songoda.skyblock.levelling.rework.calculator.impl.WildStackerCalculator;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.NMSUtil;

public final class IslandLevelManager {

    private static final Set<Materials> CHECKED_DOUBLE_TYPES;

    static {
        CHECKED_DOUBLE_TYPES = EnumSet.noneOf(Materials.class);

        CHECKED_DOUBLE_TYPES.add(Materials.SUNFLOWER);
        CHECKED_DOUBLE_TYPES.add(Materials.LILAC);
        CHECKED_DOUBLE_TYPES.add(Materials.LEGACY_DOUBLE_PLANT);
        CHECKED_DOUBLE_TYPES.add(Materials.LARGE_FERN);
        CHECKED_DOUBLE_TYPES.add(Materials.ROSE_BUSH);
        CHECKED_DOUBLE_TYPES.add(Materials.PEONY);
        CHECKED_DOUBLE_TYPES.add(Materials.TALL_GRASS);
    }

    private final static int VERSION = NMSUtil.getVersionNumber();
    private Map<Island, IslandScan> inScan;
    private Map<Materials, Long> worth;
    private Map<Materials, AmountMaterialPair> cachedPairs;

    public IslandLevelManager() {
        this.inScan = new HashMap<>();
        this.worth = new EnumMap<>(Materials.class);
        this.cachedPairs = new EnumMap<>(Materials.class);
        registerCalculators();
        reloadWorth();
    }

    public static boolean isDoubleCheckedBlock(Block block) {
        return CHECKED_DOUBLE_TYPES.contains(parseType(block));
    }

    @SuppressWarnings("deprecation")
    private static Materials parseType(Block block) {
        final Material blockType = block.getType();
        return VERSION > 12 ? Materials.fromString(blockType.name()) : Materials.requestMaterials(blockType.name(), block.getData());
    }

    public void startScan(Player attemptScanner, Island island) {

        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> startScan(attemptScanner, island));
            return;
        }

        if (island == null) throw new IllegalArgumentException("island cannot be null");

        Configuration config = SkyBlock.getInstance().getFileManager().getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml")).getFileConfiguration();
        MessageManager messageManager = SkyBlock.getInstance().getMessageManager();

        if (inScan.containsKey(island)) {
            if (attemptScanner != null) messageManager.sendMessage(attemptScanner, config.getString("Command.Island.Level.Scanning.InScan.Message"));
            return;
        }

        if (attemptScanner != null) {

            if (SkyBlock.getInstance().getIslandManager().getIslandPlayerAt(attemptScanner) != island) {
                messageManager.sendMessage(attemptScanner, config.getString("Command.Island.Level.Scanning.NotOnIsland.Message"));
                return;
            }

            messageManager.sendMessage(attemptScanner, config.getString("Command.Island.Level.Scanning.Started.Message"));
        }

        inScan.put(island, new IslandScan(island).start());
    }

    public boolean isScanning(Island island) {
        return inScan.containsKey(island);
    }

    void stopScan(Island island) {

        final IslandScan scan = inScan.get(island);

        if (scan == null) return;

        inScan.remove(island);
    }

    public void reloadWorth() {
        worth.clear();

        final Configuration config = SkyBlock.getInstance().getFileManager().getConfig(new File(SkyBlock.getInstance().getDataFolder(), "levelling.yml")).getFileConfiguration();
        final ConfigurationSection materialSection = config.getConfigurationSection("Materials");

        if (materialSection == null) return;

        for (String key : materialSection.getKeys(false)) {

            final ConfigurationSection current = materialSection.getConfigurationSection(key);

            final Materials material = Materials.fromString(key);

            if (material == null || !material.isAvailable()) continue;

            worth.put(material, current.getLong("Points"));
        }
    }

    public void addWorth(Materials material, long points) {
        worth.put(material, points);
    }

    public void removeWorth(Materials material) {
        worth.remove(material);
    }

    public List<LevellingMaterial> getWorthsAsLevelingMaterials() {

        final List<LevellingMaterial> materials = new ArrayList<>(worth.size());

        for (Entry<Materials, Long> entry : worth.entrySet()) {
            materials.add(new LevellingMaterial(entry.getKey(), entry.getValue()));
        }

        return materials;
    }

    public Map<Materials, Long> getWorths() {
        return worth;
    }

    public long getWorth(Materials material) {
        return worth.getOrDefault(material, 0L);
    }

    public boolean hasWorth(Materials material) {
        return worth.containsKey(material);
    }

    private void registerCalculators() {
        final Material spawner = Materials.SPAWNER.parseMaterial();
        final PluginManager pm = Bukkit.getPluginManager();

        if (pm.isPluginEnabled("EpicSpawners")) CalculatorRegistry.registerCalculator(new EpicSpawnerCalculator(), spawner);
        if (pm.isPluginEnabled("UltimateStacker")) CalculatorRegistry.registerCalculator(new UltimateStackerCalculator(), spawner);
        if (pm.isPluginEnabled("WildStacker")) CalculatorRegistry.registerCalculator(new WildStackerCalculator(), spawner);
    }

    private static final AmountMaterialPair EMPTY = new AmountMaterialPair(null, 0);

    AmountMaterialPair getAmountAndType(IslandScan scan, BlockInfo info) {

        Block block = info.getWorld().getBlockAt(info.getX(), info.getY(), info.getZ());
        Material blockType = block.getType();

        if (blockType == Material.AIR) return EMPTY;

        Materials finalType = parseType(block);

        if (finalType == null) return EMPTY;

        final Location blockLocation = block.getLocation();

        if (scan.getDoubleBlocks().contains(blockLocation)) return EMPTY;

        if (CHECKED_DOUBLE_TYPES.contains(finalType)) {

            final Block belowBlock = block.getRelative(BlockFace.DOWN);
            final Materials belowType = parseType(belowBlock);

            if (CHECKED_DOUBLE_TYPES.contains(belowType)) {
                block = belowBlock;
                blockType = belowType.parseMaterial();
                scan.getDoubleBlocks().add(belowBlock.getLocation());
            } else {
                scan.getDoubleBlocks().add(block.getRelative(BlockFace.UP).getLocation());
            }

        } else if (finalType == Materials.SPAWNER) {
            finalType = Materials.getSpawner(((CreatureSpawner) block.getState()).getSpawnedType());
        }

        final List<Calculator> calculators = CalculatorRegistry.getCalculators(blockType);
        final StackableManager stackableManager = SkyBlock.getInstance().getStackableManager();

        final long stackSize = stackableManager == null ? 0 : stackableManager.getStackSizeOf(blockLocation, finalType);

        if (calculators == null) {

            if (stackSize > 1) return new AmountMaterialPair(finalType, stackSize);

            AmountMaterialPair cachedPair = cachedPairs.get(finalType);

            if (cachedPair != null) return cachedPair;

            cachedPair = new AmountMaterialPair(finalType, 1);
            cachedPairs.put(finalType, cachedPair);

            return cachedPair;
        }

        long amount = 0;

        for (Calculator calc : calculators) {
            amount += calc.getAmount(block);
        }

        if (amount == 0) amount = 1;

        return new AmountMaterialPair(finalType, amount + stackSize);
    }

}
