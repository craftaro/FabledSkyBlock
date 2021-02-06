package com.songoda.skyblock.cooldown;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

                if (cooldownTypeList == CooldownType.Biome || cooldownTypeList == CooldownType.Creation || cooldownTypeList == CooldownType.Deletion) {
                    cooldownPlayer = loadCooldownPlayer(cooldownTypeList, all);
                } else if (cooldownTypeList == CooldownType.Levelling || cooldownTypeList == CooldownType.Ownership) {
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

            cooldownStorage.put(cooldownTypeList, cooldownPlayers);
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
        if (cooldownType == CooldownType.Biome || cooldownType == CooldownType.Creation || cooldownType == CooldownType.Deletion) {
            Config config = plugin.getFileManager()
                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString("Island." + cooldownType.name() + ".Cooldown") != null) {
                return new CooldownPlayer(player.getUniqueId(), new Cooldown(configLoad.getInt("Island." + cooldownType.name() + ".Cooldown")));
            }
        } else if (cooldownType == CooldownType.Levelling || cooldownType == CooldownType.Ownership) {
            Config config = plugin.getFileManager()
                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString(cooldownType.name() + ".Cooldown") != null) {
                return new CooldownPlayer(player.getUniqueId(), new Cooldown(configLoad.getInt(cooldownType.name() + ".Cooldown")));
            }
        }

        return null;
    }

    public void createPlayer(CooldownType cooldownType, OfflinePlayer player) {
        FileManager fileManager = plugin.getFileManager();

        List<CooldownPlayer> cooldowns = cooldownStorage.get(cooldownType);

        if (cooldowns == null) return;

        int time = 0;

        if (cooldownType == CooldownType.Biome || cooldownType == CooldownType.Creation || cooldownType == CooldownType.Deletion || cooldownType == CooldownType.Preview) {
            time = this.plugin.getConfiguration()
                    .getInt("Island." + cooldownType.name() + ".Cooldown.Time");

            Config config = fileManager
                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
            File configFile = config.getFile();
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set("Island." + cooldownType.name() + ".Cooldown", time);

            try {
                configLoad.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (cooldownType == CooldownType.Levelling || cooldownType == CooldownType.Ownership) {
            time = this.plugin.getConfiguration()
                    .getInt("Island." + cooldownType.name() + ".Cooldown.Time");

            Config config = plugin.getFileManager()
                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml"));
            File configFile = config.getFile();
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set(cooldownType.name() + ".Cooldown", time);

            try {
                configLoad.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cooldowns.add(new CooldownPlayer(player.getUniqueId(), new Cooldown(time)));
    }

    public void deletePlayer(CooldownType cooldownType, OfflinePlayer player) {
        for (Iterator<CooldownPlayer> it = getCooldownPlayersOrEmptyList(cooldownType).iterator(); it.hasNext();) {
            if (it.next().getUUID().equals(player.getUniqueId())) {
                if (cooldownType == CooldownType.Biome || cooldownType == CooldownType.Creation || cooldownType == CooldownType.Deletion) {
                    plugin.getFileManager()
                            .getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"),
                                    player.getUniqueId().toString() + ".yml"))
                            .getFileConfiguration().set("Island." + cooldownType.name() + ".Cooldown", null);
                } else if (cooldownType == CooldownType.Levelling || cooldownType == CooldownType.Ownership) {
                    plugin.getFileManager().getConfig(
                            new File(new File(plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml"))
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
        for (Iterator<CooldownPlayer> it = getCooldownPlayersOrEmptyList(cooldownType).iterator(); it.hasNext();) {
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
                if (cooldownType == CooldownType.Biome || cooldownType == CooldownType.Creation || cooldownType == CooldownType.Deletion) {
                    plugin.getFileManager()
                            .getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"),
                                    player.getUniqueId().toString() + ".yml"))
                            .getFileConfiguration().set("Island." + cooldownType + ".Cooldown", cooldownPlayerList.getCooldown().getTime());
                } else if (cooldownType == CooldownType.Levelling || cooldownType == CooldownType.Ownership) {
                    plugin.getFileManager()
                            .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                                    player.getUniqueId().toString() + ".yml"))
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
        Config config = null;

        if (cooldownType == CooldownType.Biome || cooldownType == CooldownType.Creation || cooldownType == CooldownType.Deletion) {
            config = plugin.getFileManager()
                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
        } else if (cooldownType == CooldownType.Levelling || cooldownType == CooldownType.Ownership) {
            config = plugin.getFileManager()
                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml"));
        }

        if (config != null) {
            try {
                config.getFileConfiguration().save(config.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addCooldownPlayer(CooldownType cooldownType, CooldownPlayer cooldownPlayer) {

        if (cooldownType == null || cooldownPlayer == null) return;

        List<CooldownPlayer> cooldowns = cooldownStorage.get(cooldownType);

        if (cooldowns == null) return;

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
        return cooldownStorage.get(cooldownType);
    }

    /**
     * Convenience method. This method functions the same as
     * {@link CooldownManager#getCooldownPlayers(CooldownType)} but returns a
     * {@link Collections#emptyList()} when no value is present in the map under the
     * key, cooldownType.
     */
    public List<CooldownPlayer> getCooldownPlayersOrEmptyList(CooldownType cooldownType) {
        return cooldownStorage.getOrDefault(cooldownType, Collections.emptyList());
    }

    public boolean hasCooldownType(CooldownType cooldownType) {
        return cooldownStorage.containsKey(cooldownType);
    }

}
