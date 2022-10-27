package com.songoda.skyblock.database;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataConverter {

    /**
     * Convert the old flatfile data to the new database system
     * Replace ownerUUID with islandUUID. New system uses islandUUID instead of ownerUUID
     */
    public static void updateData() {
        //Load all player data
        File islandsDirectory = new File(SkyBlock.getInstance().getDataFolder(), "player-data");
        if (islandsDirectory.exists() && islandsDirectory.isDirectory()) {
            List<String> players = new ArrayList<>();
            for (File file : Objects.requireNonNull(islandsDirectory.listFiles())) {
                if (file.getName().endsWith(".yml")) {
                    players.add(file.getName().split("\\.")[0]);
                }
            }

            for (String player : players) {
                File file = new File(islandsDirectory, player + ".yml");
                if (file.exists()) {
                    FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
                    String island = playerData.getString("Island.Owner");
                    if (island != null) {
                        File islandFile = new File(SkyBlock.getInstance().getDataFolder(), "islands/" + island + ".yml");
                        FileConfiguration islandData = YamlConfiguration.loadConfiguration(islandFile);
                        islandData.set("Owner", player);
                        String islandUUID = islandData.getString("UUID");
                        //Delete UUID, File name will be the UUID
                        islandData.set("UUID", null);
                        //Save changes
                        try {
                            islandData.save(new File(SkyBlock.getInstance().getDataFolder(), "island-data/" + islandUUID + ".yml"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Rename file
                        file.renameTo(new File(islandsDirectory, islandUUID + ".yml"));

                        playerData.set("Island.UUID", islandUUID);
                        playerData.set("Island.Owner", null);
                        playerData.set("Island.Biome", null);
                        //Moved to island data
                        playerData.set("Bank", null);
                        try {
                            playerData.save(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //Make it async, kick all players and prevent joining until it's done
    public static void convertTadabase(DatabaseType current, DatabaseType target) {
        //TODO
    }

}
