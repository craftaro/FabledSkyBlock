package com.songoda.skyblock.levelling;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.levelling.calculator.Calculator;
import com.songoda.skyblock.levelling.calculator.CalculatorRegistry;
import com.songoda.skyblock.levelling.calculator.impl.EpicSpawnerCalculator;
import com.songoda.skyblock.levelling.calculator.impl.UltimateStackerCalculator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.levelling.amount.AmountMaterialPair;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.stackable.StackableManager;
 
import com.songoda.skyblock.utils.version.NMSUtil;

public final class IslandLevelManager {

    private final static int VERSION = NMSUtil.getVersionNumber();
    private Map<Island, IslandScan> inScan;
    private Map<CompatibleMaterial, Long> worth;
    private Map<CompatibleMaterial, AmountMaterialPair> cachedPairs;
    private final SkyBlock plugin;

    public IslandLevelManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.inScan = new HashMap<>();
        this.worth = new EnumMap<>(CompatibleMaterial.class);
        this.cachedPairs = new EnumMap<>(CompatibleMaterial.class);
        registerCalculators();
        reloadWorth();
    }

    public void startScan(Player attemptScanner, Island island){
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

        inScan.put(island, new IslandScan(plugin, island).start());
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

            final CompatibleMaterial material = CompatibleMaterial.getMaterial(key);

            if (material == null) continue;

            worth.put(material, current.getLong("Points"));
        }
    }

    public void addWorth(CompatibleMaterial material, long points) {
        worth.put(material, points);
    }

    public void removeWorth(CompatibleMaterial material) {
        worth.remove(material);
    }

    public List<LevellingMaterial> getWorthsAsLevelingMaterials() {

        final List<LevellingMaterial> materials = new ArrayList<>(worth.size());

        for (Entry<CompatibleMaterial, Long> entry : worth.entrySet()) {
            materials.add(new LevellingMaterial(entry.getKey(), entry.getValue()));
        }

        return materials;
    }

    public Map<CompatibleMaterial, Long> getWorths() {
        return worth;
    }

    public long getWorth(CompatibleMaterial material) {
        return worth.getOrDefault(material, 0L);
    }

    public boolean hasWorth(CompatibleMaterial material) {
        return worth.containsKey(material);
    }

    private void registerCalculators() {
        final CompatibleMaterial spawner = CompatibleMaterial.SPAWNER;
        final PluginManager pm = Bukkit.getPluginManager();

        if (pm.isPluginEnabled("EpicSpawners")) CalculatorRegistry.registerCalculator(new EpicSpawnerCalculator(), spawner);
        if (pm.isPluginEnabled("UltimateStacker")) CalculatorRegistry.registerCalculator(new UltimateStackerCalculator(), spawner);
    }

    private static final AmountMaterialPair EMPTY = new AmountMaterialPair(null, 0);

    AmountMaterialPair getAmountAndType(IslandScan scan, BlockInfo info) {

        Block block = info.getWorld().getBlockAt(info.getX(), info.getY(), info.getZ());
        CompatibleMaterial blockType = CompatibleMaterial.getBlockMaterial(block.getType());

        if (blockType == CompatibleMaterial.AIR) return EMPTY;

        CompatibleMaterial compMaterial = CompatibleMaterial.getMaterial(block);

        if (compMaterial == null) return EMPTY;

        final Location blockLocation = block.getLocation();

        if (scan.getDoubleBlocks().contains(blockLocation)) return EMPTY;

        if (compMaterial.isTall()) {
            final Block belowBlock = block.getRelative(BlockFace.DOWN);
            final CompatibleMaterial belowMaterial = CompatibleMaterial.getMaterial(belowBlock);

            if (belowMaterial.isTall()) {
                block = belowBlock;
                blockType = belowMaterial;
                scan.getDoubleBlocks().add(belowBlock.getLocation());
            } else {
                scan.getDoubleBlocks().add(block.getRelative(BlockFace.UP).getLocation());
            }

        }

        final List<Calculator> calculators = CalculatorRegistry.getCalculators(blockType);
        final StackableManager stackableManager = SkyBlock.getInstance().getStackableManager();


        final long stackSize = stackableManager == null ? 0 : stackableManager.getStackSizeOf(blockLocation, compMaterial);

        if (calculators == null) {

            if (stackSize > 1) return new AmountMaterialPair(compMaterial, stackSize);

            AmountMaterialPair cachedPair = cachedPairs.get(compMaterial);

            if (cachedPair != null) return cachedPair;

            cachedPair = new AmountMaterialPair(compMaterial, 1);
            cachedPairs.put(compMaterial, cachedPair);

            return cachedPair;
        }

        long amount = 0;

        for (Calculator calc : calculators) {
            amount += calc.getAmount(block);
        }

        if (amount == 0) amount = 1;

        return new AmountMaterialPair(compMaterial, amount + stackSize);
    }

}
