package com.songoda.skyblock.scoreboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final SkyBlock skyblock;
    private Map<UUID, Scoreboard> scoreboardStorage = new HashMap<>();

    public ScoreboardManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        new BukkitRunnable() {
            @Override
            public void run() {
                IslandManager islandManager = skyblock.getIslandManager();
                FileManager fileManager = skyblock.getFileManager();

                if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                        .getBoolean("Island.Scoreboard.Enable")) {
                    Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        Scoreboard scoreboard = new Scoreboard(skyblock, all);
                        Island island = islandManager.getIsland(all);

                        if (island == null) {
                            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
                            scoreboard.setDisplayList(
                                    config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
                        } else {
                            if (island.getRole(IslandRole.Member).size() == 0
                                    && island.getRole(IslandRole.Operator).size() == 0) {
                                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));

                                if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                                    scoreboard.setDisplayList(config.getFileConfiguration()
                                            .getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
                                } else {
                                    scoreboard.setDisplayList(config.getFileConfiguration()
                                            .getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
                                }
                            } else {
                                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));

                                if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                                    scoreboard.setDisplayList(config.getFileConfiguration()
                                            .getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
                                } else {
                                    scoreboard.setDisplayList(config.getFileConfiguration()
                                            .getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
                                }

                                Map<String, String> displayVariables = new HashMap<>();
                                displayVariables.put("%owner",
                                        config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
                                displayVariables.put("%operator", config.getFileConfiguration()
                                        .getString("Scoreboard.Island.Team.Word.Operator"));
                                displayVariables.put("%member",
                                        config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));

                                scoreboard.setDisplayVariables(displayVariables);
                            }
                        }

                        scoreboard.run();
                        storeScoreboard(all, scoreboard);
                    }
                }
            }
        }.runTaskLater(skyblock, 20L);
    }

    public void resendScoreboard() {
        IslandManager islandManager = skyblock.getIslandManager();
        FileManager fileManager = skyblock.getFileManager();

        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Scoreboard.Enable")) return;
        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!hasScoreboard(all)) continue;

            Scoreboard scoreboard = getScoreboard(all);

            Island island = islandManager.getIsland(all);

            if (island == null) {
                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
                scoreboard.setDisplayList(
                        config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
            } else {
                if (island.getRole(IslandRole.Member).size() == 0
                        && island.getRole(IslandRole.Operator).size() == 0) {
                    scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));

                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                        scoreboard.setDisplayList(config.getFileConfiguration()
                                .getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
                    } else {
                        scoreboard.setDisplayList(config.getFileConfiguration()
                                .getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
                    }
                } else {
                    scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));

                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                        scoreboard.setDisplayList(config.getFileConfiguration()
                                .getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
                    } else {
                        scoreboard.setDisplayList(config.getFileConfiguration()
                                .getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
                    }

                    Map<String, String> displayVariables = new HashMap<>();
                    displayVariables.put("%owner",
                            config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
                    displayVariables.put("%operator",
                            config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Operator"));
                    displayVariables.put("%member",
                            config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));

                    scoreboard.setDisplayVariables(displayVariables);
                }
            }
            scoreboard.run();
        }
    }

    public void storeScoreboard(Player player, Scoreboard scoreboard) {
        scoreboardStorage.put(player.getUniqueId(), scoreboard);
    }

    public void unloadPlayer(Player player) {
        scoreboardStorage.remove(player.getUniqueId());
    }

    public Scoreboard getScoreboard(Player player) {
        if (scoreboardStorage.containsKey(player.getUniqueId())) {
            return scoreboardStorage.get(player.getUniqueId());
        }

        return null;
    }

    public boolean hasScoreboard(Player player) {
        return scoreboardStorage.containsKey(player.getUniqueId());
    }
}
