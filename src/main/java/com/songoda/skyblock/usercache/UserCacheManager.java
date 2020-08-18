package com.songoda.skyblock.usercache;

import com.eatthepath.uuid.FastUUID;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.utils.player.NameFetcher;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public final class UserCacheManager {

    private final SkyBlock plugin;
    private final Config config;

    public UserCacheManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "usercache.yml"));

        final FileManager fileManager = plugin.getFileManager();
        final File configFile = new File(plugin.getDataFolder().toString() + "/island-data");

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (configFile.exists()) {
                int usersIgnored = 0;

                Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Fetching user information from island data. This may take a while...");

                for (File fileList : configFile.listFiles()) {

                    if (fileList == null) continue;

                    final String fileName = fileList.getName();

                    if (fileName.length() < 35 || !fileName.endsWith(".yml")) continue;

                    try {
                        final FileConfiguration configLoad = new Config(fileManager, fileList).getFileConfiguration();
                        final String ownerUUIDString = fileName.substring(0, fileName.indexOf('.'));

                        Set<UUID> islandMembers = new HashSet<>();
                        islandMembers.add(FastUUID.parseUUID(ownerUUIDString));

                        for (String memberList : configLoad.getStringList("Members")) {
                            islandMembers.add(FastUUID.parseUUID(memberList));
                        }

                        for (String operatorList : configLoad.getStringList("Operators")) {
                            islandMembers.add(FastUUID.parseUUID(operatorList));
                        }

                        for (UUID islandMemberList : islandMembers) {
                            if (!hasUser(islandMemberList)) {
                                NameFetcher.Names[] names = NameFetcher.getNames(islandMemberList);

                                if (names.length >= 1) {
                                    addUser(islandMemberList, names[0].getName());
                                }
                            }
                        }
                    } catch (Exception e) {
                        usersIgnored++;
                    }

                }

                save();

                if (usersIgnored != 0) {
                    Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Finished fetching user information from island data. There were " + usersIgnored + " users that were skipped.");
                } else {
                    Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Finished fetching user information from island data. No users were ignored.");
                }
            }
        });
    }

    public void onDisable() {
        save();
    }

    public void addUser(UUID uuid, String name) {
        config.getFileConfiguration().set(FastUUID.toString(uuid), name);
    }

    public String getUser(UUID uuid) {
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString(FastUUID.toString(uuid)) != null) {
            return configLoad.getString(FastUUID.toString(uuid));
        }

        return null;
    }

    public UUID getUser(String name) {
        FileConfiguration configLoad = config.getFileConfiguration();

        for (String userList : configLoad.getConfigurationSection("").getKeys(false)) {
            if (configLoad.getString(userList).equalsIgnoreCase(name)) {
                return FastUUID.parseUUID(userList);
            }
        }

        return null;
    }

    public boolean hasUser(UUID uuid) {
        return config.getFileConfiguration().getString(FastUUID.toString(uuid)) != null;
    }

    public boolean hasUser(String name) {
        FileConfiguration configLoad = config.getFileConfiguration();

        for (String userList : configLoad.getConfigurationSection("").getKeys(false)) {
            if (configLoad.getString(userList).equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public void saveAsync() {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> save());
    }

    public synchronized void save() {
        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
