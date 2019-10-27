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
import java.util.*;

public class UpgradeManager {

    private SkyBlock skyblock;
    private Map<Upgrade.Type, List<Upgrade>> upgradeStorage = new HashMap<>();

    public UpgradeManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "upgrades.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (Upgrade.Type typeList : Upgrade.Type.values()) {
            if (typeList != Upgrade.Type.Size) {
                List<Upgrade> upgrades = new ArrayList<>();

                Upgrade upgrade = new Upgrade(configLoad.getDouble("Upgrades." + typeList.name() + ".Cost"));
                upgrade.setEnabled(configLoad.getBoolean("Upgrades." + typeList.name() + ".Enable"));
                upgrades.add(upgrade);

                upgradeStorage.put(typeList, upgrades);
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

            upgradeStorage.put(Upgrade.Type.Size, upgrades);
        }

        // Task for applying the speed & jump boost upgrades if the player is on an island that has them
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyBlock.getInstance(), this::applyUpgrades, 5L, 20L);
    }

    public List<Upgrade> getUpgrades(Upgrade.Type type) {
        return upgradeStorage.get(type);

    }

    public void addUpgrade(Upgrade.Type type, int value) {
        List<Upgrade> upgrades = new ArrayList<>();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "upgrades.yml"));
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

        upgradeStorage.put(type, upgrades);

        try {
            configLoad.save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeUpgrade(Upgrade.Type type, double cost, int value) {
        for (Upgrade upgradeList : upgradeStorage.get(type)) {
            if (upgradeList.getCost() == cost && upgradeList.getValue() == value) {
                List<Upgrade> upgrades = upgradeStorage.get(type);
                upgrades.remove(upgradeList);

                Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "upgrades.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                configLoad.set("Upgrades.Size", null);

                for (int i = 0; i < upgrades.size(); i++) {
                    Upgrade upgrade = upgrades.get(i);
                    configLoad.set("Upgrades.Size." + i + ".Value", upgrade.getValue());
                    configLoad.set("Upgrades.Size." + i + ".Cost", upgrade.getCost());
                }

                try {
                    configLoad.save(config.getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }
        }
    }

    public boolean hasUpgrade(Upgrade.Type type, int value) {
        if (upgradeStorage.containsKey(type)) {
            for (Upgrade upgradeList : upgradeStorage.get(type)) {
                if (upgradeList.getValue() == value) {
                    return true;
                }
            }
        }

        return false;
    }

    private void applyUpgrades() {
        IslandManager islandManager = skyblock.getIslandManager();
        UpgradeManager upgradeManager = skyblock.getUpgradeManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Island island = islandManager.getIslandAtLocation(player.getLocation());
            if (island == null) continue;

            // Apply potion effect upgrades
            Collection<PotionEffect> potionEffects = player.getActivePotionEffects();
            PotionEffect speed = null, jump = null;
            for (PotionEffect potionEffect : potionEffects) {
                if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                    speed = potionEffect;
                } else if (potionEffect.getType().equals(PotionEffectType.JUMP)) {
                    jump = potionEffect;
                }
                if (speed != null && jump != null) break;
            }

            // Speed
            List<Upgrade> speedUpgrades = upgradeManager.getUpgrades(Upgrade.Type.Speed);
            if (speedUpgrades != null && speedUpgrades.size() > 0 && speedUpgrades.get(0).isEnabled() && island.isUpgrade(Upgrade.Type.Speed)) {
                if (speed == null) {
                    speed = new PotionEffect(PotionEffectType.SPEED, 60, 1);
                } else if (speed.getAmplifier() == 1 && speed.getDuration() < 60) {
                    speed = new PotionEffect(PotionEffectType.SPEED, speed.getDuration() + 21, 1);
                }
                player.addPotionEffect(speed, true);
            }

            // Jump boost
            List<Upgrade> jumpUpgrades = upgradeManager.getUpgrades(Upgrade.Type.Jump);
            if (jumpUpgrades != null && jumpUpgrades.size() > 0 && jumpUpgrades.get(0).isEnabled() && island.isUpgrade(Upgrade.Type.Jump)) {
                if (jump == null) {
                    jump = new PotionEffect(PotionEffectType.JUMP, 60, 1);
                } else if (jump.getAmplifier() == 1 && jump.getDuration() < 60) {
                    jump = new PotionEffect(PotionEffectType.JUMP, jump.getDuration() + 21, 1);
                }
                player.addPotionEffect(jump, true);
            }
        }
    }
}
