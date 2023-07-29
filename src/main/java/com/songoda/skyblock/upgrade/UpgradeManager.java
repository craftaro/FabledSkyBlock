package com.songoda.skyblock.upgrade;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpgradeManager {
    private final SkyBlock plugin;
    private final Map<Upgrade.Type, List<Upgrade>> upgradeStorage = new HashMap<>();

    public UpgradeManager(SkyBlock plugin) {
        this.plugin = plugin;

        FileConfiguration configLoad = plugin.getUpgrades();

        for (Upgrade.Type typeList : Upgrade.Type.values()) {
            if (typeList != Upgrade.Type.SIZE && typeList != Upgrade.Type.MEMBERS) {
                List<Upgrade> upgrades = new ArrayList<>();

                Upgrade upgrade = new Upgrade(configLoad.getDouble("Upgrades." + typeList.name() + ".Cost"));
                upgrade.setEnabled(configLoad.getBoolean("Upgrades." + typeList.name() + ".Enable"));
                upgrades.add(upgrade);

                this.upgradeStorage.put(typeList, upgrades);
            }
        }

        if (configLoad.getString("Upgrades.Size") != null) {
            List<Upgrade> upgrades = new ArrayList<>();

            for (String tierList : configLoad.getConfigurationSection("Upgrades.Size").getKeys(false)) {
                if (configLoad.getString("Upgrades.Size." + tierList + ".Value") != null) {
                    if (configLoad.getInt("Upgrades.Size." + tierList + ".Value") > 1000) {
                        continue;
                    }
                }

                upgrades.add(new Upgrade(configLoad.getDouble("Upgrades.Size." + tierList + ".Cost"),
                        configLoad.getInt("Upgrades.Size." + tierList + ".Value")));
            }

            this.upgradeStorage.put(Upgrade.Type.SIZE, upgrades);
        }

        if (configLoad.getString("Upgrades.Members") != null) {
            List<Upgrade> upgrades = new LinkedList<>();

            for (String tierList : configLoad.getConfigurationSection("Upgrades.Members").getKeys(false)) {
                if (configLoad.getString("Upgrades.Members." + tierList + ".Value") != null) {
                    if (configLoad.getInt("Upgrades.Members." + tierList + ".Value") > 1000) {
                        continue;
                    }
                }

                upgrades.add(new Upgrade(configLoad.getDouble("Upgrades.Members." + tierList + ".Cost"),
                        configLoad.getInt("Upgrades.Members." + tierList + ".Value")));
            }

            this.upgradeStorage.put(Upgrade.Type.MEMBERS, upgrades);
        }

        // Task for applying the speed & jump boost upgrades if the player is on an island that has them
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyBlock.getPlugin(SkyBlock.class), this::applyUpgrades, 5L, 20L);
    }

    public List<Upgrade> getUpgrades(Upgrade.Type type) {
        return this.upgradeStorage.get(type);

    }

    public synchronized void addUpgrade(Upgrade.Type type, int value) {
        List<Upgrade> upgrades = new ArrayList<>();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "upgrades.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Upgrades.Size") != null) {
            for (String tierList : configLoad.getConfigurationSection("Upgrades.Size").getKeys(false)) {
                upgrades.add(new Upgrade(configLoad.getDouble("Upgrades.Size." + tierList + ".Cost"),
                        configLoad.getInt("Upgrades.Size." + tierList + ".Value")));
            }
        }

        upgrades.add(new Upgrade(0, value));
        configLoad.set("Upgrades.Size", null);

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            configLoad.set("Upgrades.Size." + i + ".Value", upgrade.getValue());
            configLoad.set("Upgrades.Size." + i + ".Cost", upgrade.getCost());
        }

        if (configLoad.getString("Upgrades.Members") != null) {
            for (String tierList : configLoad.getConfigurationSection("Upgrades.Members").getKeys(false)) {
                upgrades.add(new Upgrade(configLoad.getDouble("Upgrades.Members." + tierList + ".Cost"),
                        configLoad.getInt("Upgrades.Members." + tierList + ".Value")));
            }
        }

        upgrades.add(new Upgrade(0, value));
        configLoad.set("Upgrades.Members", null);

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            configLoad.set("Upgrades.Members." + i + ".Value", upgrade.getValue());
            configLoad.set("Upgrades.Members." + i + ".Cost", upgrade.getCost());
        }

        this.upgradeStorage.put(type, upgrades);

        try {
            configLoad.save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeUpgrade(Upgrade.Type type, double cost, int value) {
        for (Upgrade upgradeList : this.upgradeStorage.get(type)) {
            if (upgradeList.getCost() == cost && upgradeList.getValue() == value) {
                List<Upgrade> upgrades = this.upgradeStorage.get(type);
                upgrades.remove(upgradeList);

                Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "upgrades.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                configLoad.set("Upgrades.Size", null);

                for (int i = 0; i < upgrades.size(); i++) {
                    Upgrade upgrade = upgrades.get(i);
                    configLoad.set("Upgrades.Size." + i + ".Value", upgrade.getValue());
                    configLoad.set("Upgrades.Size." + i + ".Cost", upgrade.getCost());
                }

                configLoad.set("Upgrades.Members", null);

                for (int i = 0; i < upgrades.size(); i++) {
                    Upgrade upgrade = upgrades.get(i);
                    configLoad.set("Upgrades.Members." + i + ".Value", upgrade.getValue());
                    configLoad.set("Upgrades.Members." + i + ".Cost", upgrade.getCost());
                }

                try {
                    configLoad.save(config.getFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                return;
            }
        }
    }

    public boolean hasUpgrade(Upgrade.Type type, int value) {
        if (this.upgradeStorage.containsKey(type)) {
            for (Upgrade upgradeList : this.upgradeStorage.get(type)) {
                if (upgradeList.getValue() == value) {
                    return true;
                }
            }
        }

        return false;
    }

    private void applyUpgrades() {
        IslandManager islandManager = this.plugin.getIslandManager();
        UpgradeManager upgradeManager = this.plugin.getUpgradeManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Island island = islandManager.getIslandAtLocation(player.getLocation());
            if (island == null) {
                continue;
            }

            // Apply potion effect upgrades
            Collection<PotionEffect> potionEffects = player.getActivePotionEffects();
            PotionEffect speed = null, jump = null;
            for (PotionEffect potionEffect : potionEffects) {
                if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                    speed = potionEffect;
                } else if (potionEffect.getType().equals(PotionEffectType.JUMP)) {
                    jump = potionEffect;
                }
                if (speed != null && jump != null) {
                    break;
                }
            }

            // Speed
            List<Upgrade> speedUpgrades = upgradeManager.getUpgrades(Upgrade.Type.SPEED);
            if (speedUpgrades != null && !speedUpgrades.isEmpty() && speedUpgrades.get(0).isEnabled() && island.isUpgrade(Upgrade.Type.SPEED)) {
                if (speed == null) {
                    speed = new PotionEffect(PotionEffectType.SPEED, 60, 1);
                } else if (speed.getAmplifier() == 1 && speed.getDuration() < 60) {
                    speed = new PotionEffect(PotionEffectType.SPEED, speed.getDuration() + 21, 1);
                }
                player.addPotionEffect(speed, true);
            }

            // Jump boost
            List<Upgrade> jumpUpgrades = upgradeManager.getUpgrades(Upgrade.Type.JUMP);
            if (jumpUpgrades != null && !jumpUpgrades.isEmpty() && jumpUpgrades.get(0).isEnabled() && island.isUpgrade(Upgrade.Type.JUMP)) {
                if (jump == null) {
                    jump = new PotionEffect(PotionEffectType.JUMP, 60, 1);
                } else if (jump.getAmplifier() == 1 && jump.getDuration() < 60) {
                    jump = new PotionEffect(PotionEffectType.JUMP, jump.getDuration() + 21, 1);
                }
                player.addPotionEffect(jump, true);
                player.addPotionEffect(jump);
            }
        }
    }
}
