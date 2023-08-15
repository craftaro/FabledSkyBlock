package com.craftaro.skyblock.cooldown;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CooldownManager {
    private final SkyBlock plugin;

    private final Map<CooldownType, List<CooldownPlayer>> cooldownStorage = new EnumMap<>(CooldownType.class);

    public CooldownManager(SkyBlock plugin) {
        this.plugin = plugin;

        IslandManager islandManager = plugin.getIslandManager();

        for (CooldownType cooldownTypeList : CooldownType.getTypes()) {
            List<CooldownPlayer> cooldownPlayers = new ArrayList<>();

            for (Player all : Bukkit.getOnlinePlayers()) {
                CooldownPlayer cooldownPlayer = null;

                if (cooldownTypeList == CooldownType.BIOME || cooldownTypeList == CooldownType.CREATION || cooldownTypeList == CooldownType.DELETION) {
                    cooldownPlayer = loadCooldownPlayer(cooldownTypeList, all);
                } else if (cooldownTypeList == CooldownType.LEVELLING || cooldownTypeList == CooldownType.OWNERSHIP) {
                    Island island = islandManager.getIsland(all);

                    if (island != null) {
                        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());

                        if (!hasPlayer(cooldownTypeList, offlinePlayer)) {
                            cooldownPlayer = loadCooldownPlayer(cooldownTypeList, offlinePlayer);
                        }
                    }
                }

                if (cooldownPlayer != null) {
                    cooldownPlayers.add(cooldownPlayer);
                }
            }

            this.cooldownStorage.put(cooldownTypeList, cooldownPlayers);
        }

        new CooldownTask(this).runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    public void onDisable() {
        for (CooldownType cooldownTypeList : CooldownType.getTypes()) {
            setCooldownPlayer(cooldownTypeList);
            saveCooldownPlayer(cooldownTypeList);
        }
    }

    public CooldownPlayer loadCooldownPlayer(CooldownType cooldownType, OfflinePlayer player) {
        if (cooldownType == CooldownType.BIOME || cooldownType == CooldownType.CREATION || cooldownType == CooldownType.DELETION) {
            FileManager.Config config = this.plugin.getFileManager()
                    .getConfig(new File(new File(this.plugin.getDataFolder(), "player-data"), player.getUniqueId() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString("Island." + cooldownType.name() + ".Cooldown") != null) {
                return new CooldownPlayer(player.getUniqueId(), new Cooldown(configLoad.getInt("Island." + cooldownType.name() + ".Cooldown")));
            }
        } else if (cooldownType == CooldownType.LEVELLING || cooldownType == CooldownType.OWNERSHIP) {
            FileManager.Config config = this.plugin.getFileManager()
                    .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"), player.getUniqueId() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString(cooldownType.name() + ".Cooldown") != null) {
                return new CooldownPlayer(player.getUniqueId(), new Cooldown(configLoad.getInt(cooldownType.name() + ".Cooldown")));
            }
        }

        return null;
    }

    public void createPlayer(CooldownType cooldownType, OfflinePlayer player) {
        FileManager fileManager = this.plugin.getFileManager();

        List<CooldownPlayer> cooldowns = this.cooldownStorage.get(cooldownType);

        if (cooldowns == null) {
            return;
        }

        int time = 0;

        if (cooldownType == CooldownType.BIOME || cooldownType == CooldownType.CREATION || cooldownType == CooldownType.DELETION || cooldownType == CooldownType.PREVIEW) {
            time = this.plugin.getConfiguration()
                    .getInt("Island." + cooldownType.name() + ".Cooldown.Time");

            FileManager.Config config = fileManager
                    .getConfig(new File(new File(this.plugin.getDataFolder(), "player-data"), player.getUniqueId() + ".yml"));
            File configFile = config.getFile();
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set("Island." + cooldownType.name() + ".Cooldown", time);

            try {
                configLoad.save(configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (cooldownType == CooldownType.LEVELLING || cooldownType == CooldownType.OWNERSHIP) {
            time = this.plugin.getConfiguration()
                    .getInt("Island." + cooldownType.name() + ".Cooldown.Time");

            FileManager.Config config = this.plugin.getFileManager()
                    .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"), player.getUniqueId() + ".yml"));
            File configFile = config.getFile();
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set(cooldownType.name() + ".Cooldown", time);

            try {
                configLoad.save(configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        cooldowns.add(new CooldownPlayer(player.getUniqueId(), new Cooldown(time)));
    }

    public void deletePlayer(CooldownType cooldownType, OfflinePlayer player) {
        for (Iterator<CooldownPlayer> it = getCooldownPlayersOrEmptyList(cooldownType).iterator(); it.hasNext(); ) {
            if (it.next().getUUID().equals(player.getUniqueId())) {
                if (cooldownType == CooldownType.BIOME || cooldownType == CooldownType.CREATION || cooldownType == CooldownType.DELETION) {
                    this.plugin.getFileManager()
                            .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/player-data"),
                                    player.getUniqueId().toString() + ".yml"))
                            .getFileConfiguration().set("Island." + cooldownType.name() + ".Cooldown", null);
                } else if (cooldownType == CooldownType.LEVELLING || cooldownType == CooldownType.OWNERSHIP) {
                    this.plugin.getFileManager().getConfig(
                                    new File(new File(this.plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml"))
                            .getFileConfiguration().set(cooldownType.name() + ".Cooldown", null);
                }
                it.remove();
                break;
            }
        }
    }

    public boolean hasPlayer(CooldownType cooldownType, OfflinePlayer player) {
        for (CooldownPlayer cooldownPlayerList : getCooldownPlayersOrEmptyList(cooldownType)) {
            if (cooldownPlayerList.getUUID().equals(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public void transferPlayer(CooldownType cooldownType, OfflinePlayer player1, OfflinePlayer player2) {
        for (CooldownPlayer cooldownPlayerList : getCooldownPlayersOrEmptyList(cooldownType)) {
            if (cooldownPlayerList.getUUID().equals(player1.getUniqueId())) {
                cooldownPlayerList.setUUID(player2.getUniqueId());
                break;
            }
        }
    }

    public void removeCooldownPlayer(CooldownType cooldownType, CooldownPlayer cooldownPlayer) {
        getCooldownPlayersOrEmptyList(cooldownType).remove(cooldownPlayer);
    }

    public void removeCooldownPlayer(CooldownType cooldownType, OfflinePlayer player) {
        for (Iterator<CooldownPlayer> it = getCooldownPlayersOrEmptyList(cooldownType).iterator(); it.hasNext(); ) {
            if (it.next().getUUID().equals(player.getUniqueId())) {
                it.remove();
                break;
            }
        }
    }

    public void setCooldownPlayer(CooldownType cooldownType) {
        for (CooldownPlayer cooldownPlayerList : getCooldownPlayersOrEmptyList(cooldownType)) {
            setCooldownPlayer(cooldownType, Bukkit.getServer().getOfflinePlayer(cooldownPlayerList.getUUID()));
        }
    }

    public void setCooldownPlayer(CooldownType cooldownType, OfflinePlayer player) {
        for (CooldownPlayer cooldownPlayerList : getCooldownPlayersOrEmptyList(cooldownType)) {
            if (cooldownPlayerList.getUUID().equals(player.getUniqueId())) {
                if (cooldownType == CooldownType.BIOME || cooldownType == CooldownType.CREATION || cooldownType == CooldownType.DELETION) {
                    this.plugin.getFileManager()
                            .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/player-data"),
                                    player.getUniqueId().toString() + ".yml"))
                            .getFileConfiguration().set("Island." + cooldownType + ".Cooldown", cooldownPlayerList.getCooldown().getTime());
                } else if (cooldownType == CooldownType.LEVELLING || cooldownType == CooldownType.OWNERSHIP) {
                    this.plugin.getFileManager()
                            .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"), player.getUniqueId() + ".yml"))
                            .getFileConfiguration().set(cooldownType.name() + ".Cooldown", cooldownPlayerList.getCooldown().getTime());
                }
                break;
            }
        }
    }

    public void saveCooldownPlayer(CooldownType cooldownType) {
        for (CooldownPlayer cooldownPlayerList : getCooldownPlayersOrEmptyList(cooldownType)) {
            saveCooldownPlayer(cooldownType, Bukkit.getServer().getOfflinePlayer(cooldownPlayerList.getUUID()));
        }
    }

    public void saveCooldownPlayer(CooldownType cooldownType, OfflinePlayer player) {
        FileManager.Config config = null;

        if (cooldownType == CooldownType.BIOME || cooldownType == CooldownType.CREATION || cooldownType == CooldownType.DELETION) {
            config = this.plugin.getFileManager()
                    .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
        } else if (cooldownType == CooldownType.LEVELLING || cooldownType == CooldownType.OWNERSHIP) {
            config = this.plugin.getFileManager()
                    .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml"));
        }

        if (config != null) {
            try {
                config.getFileConfiguration().save(config.getFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addCooldownPlayer(CooldownType cooldownType, CooldownPlayer cooldownPlayer) {
        if (cooldownType == null || cooldownPlayer == null) {
            return;
        }

        List<CooldownPlayer> cooldowns = this.cooldownStorage.get(cooldownType);
        if (cooldowns == null) {
            return;
        }

        cooldowns.add(cooldownPlayer);
    }

    public CooldownPlayer getCooldownPlayer(CooldownType cooldownType, OfflinePlayer player) {
        for (CooldownPlayer cooldownPlayerList : getCooldownPlayersOrEmptyList(cooldownType)) {
            if (cooldownPlayerList.getUUID().equals(player.getUniqueId())) {
                return cooldownPlayerList;
            }
        }

        return null;
    }

    public List<CooldownPlayer> getCooldownPlayers(CooldownType cooldownType) {
        return this.cooldownStorage.get(cooldownType);
    }

    /**
     * Convenience method. This method functions the same as
     * {@link CooldownManager#getCooldownPlayers(CooldownType)} but returns a
     * {@link Collections#emptyList()} when no value is present in the map under the
     * key, cooldownType.
     */
    public List<CooldownPlayer> getCooldownPlayersOrEmptyList(CooldownType cooldownType) {
        return this.cooldownStorage.getOrDefault(cooldownType, Collections.emptyList());
    }

    public boolean hasCooldownType(CooldownType cooldownType) {
        return this.cooldownStorage.containsKey(cooldownType);
    }
}
