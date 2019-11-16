package com.songoda.skyblock.scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.utils.version.NMSUtil;

public class ScoreboardManager extends BukkitRunnable {

    private final static int VERSION = NMSUtil.getVersionNumber();
    private final SkyBlock skyblock;
    private final Map<UUID, Scoreboard> scoreboardStorage;

    private int runTicks = 0;

    private List<String> teamNames;
    private List<String> objectiveNames;

    public ScoreboardManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
        this.scoreboardStorage = new HashMap<>();
        this.teamNames = new ArrayList<>();
        this.objectiveNames = new ArrayList<>();
        this.runTaskTimer(skyblock, 20, 40);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {

        if (runTicks++ == 0) {
            IslandManager islandManager = skyblock.getIslandManager();
            FileManager fileManager = skyblock.getFileManager();

            if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable")) {
                Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));

                for (Player all : Bukkit.getOnlinePlayers()) {
                    Scoreboard scoreboard = new Scoreboard(skyblock, all);
                    Island island = islandManager.getIsland(all);

                    if (island == null) {
                        scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
                        scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
                    } else {
                        if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
                            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));

                            if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                                scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
                            } else {
                                scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
                            }
                        } else {
                            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));

                            if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                                scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
                            } else {
                                scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
                            }

                            Map<String, String> displayVariables = new HashMap<>();
                            displayVariables.put("%owner", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
                            displayVariables.put("%operator", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Operator"));
                            displayVariables.put("%member", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));

                            scoreboard.setDisplayVariables(displayVariables);
                        }
                    }

                    scoreboard.run();
                    storeScoreboard(all, scoreboard);
                }
            }
            return;
        }

        final org.bukkit.scoreboard.Scoreboard primary = Bukkit.getScoreboardManager().getMainScoreboard();
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final Set<Objective> objectives = primary.getObjectives();
        final Set<Team> teams = primary.getTeams();

        for (Player player : players) {

            /*
             * Unregister all teams or objectives that are no longer present in the main
             * scoreboard.
             */

            final org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();

            for (String name : objectiveNames) {

                if (primary.getObjective(name) != null) continue;

                final Objective objective = board.getObjective(name);

                if (objective != null) objective.unregister();

            }

            for (String name : teamNames) {

                if (primary.getTeam(name) != null) continue;

                final Team team = board.getTeam(name);

                if (team != null) team.unregister();
            }

        }

        /*
         * Update the objective/team names.
         */

        objectiveNames.clear();
        teamNames.clear();

        objectives.forEach(objective -> {
            if (primary.getObjective(objective.getName()) != null) objectiveNames.add(objective.getName());
        });
        teams.forEach(team -> {
            if (primary.getTeam(team.getName()) != null) teamNames.add(team.getName());
        });

        /*
         * Update or add any missing information to the player's scoreboard.
         */

        for (Player player : players) {

            final org.bukkit.scoreboard.Scoreboard playerBoard = player.getScoreboard();

            for (Objective primaryObjective : objectives) {

                Objective obj = playerBoard.getObjective(primaryObjective.getName());

                if (obj == null) obj = playerBoard.registerNewObjective(primaryObjective.getName(), primaryObjective.getCriteria());

                obj.setDisplayName(primaryObjective.getDisplayName());
                obj.setDisplaySlot(primaryObjective.getDisplaySlot());
                obj.setRenderType(primaryObjective.getRenderType());
            }

            for (Team primaryTeam : teams) {

                Team obj = playerBoard.getTeam(primaryTeam.getName());

                if (obj == null) obj = playerBoard.registerNewTeam(primaryTeam.getName());

                obj.setAllowFriendlyFire(primaryTeam.allowFriendlyFire());
                obj.setCanSeeFriendlyInvisibles(primaryTeam.canSeeFriendlyInvisibles());
                if (VERSION > 11) obj.setColor(primaryTeam.getColor());
                obj.setDisplayName(primaryTeam.getDisplayName());
                obj.setNameTagVisibility(primaryTeam.getNameTagVisibility());
                obj.setPrefix(primaryTeam.getPrefix());
                obj.setSuffix(primaryTeam.getSuffix());

                for (String primaryEntry : primaryTeam.getEntries()) {
                    obj.addEntry(primaryEntry);
                }

                if (VERSION > 8) {
                    for (Option option : Option.values()) {
                        obj.setOption(option, primaryTeam.getOption(option));
                    }
                }

            }

        }

    }

    public void resendScoreboard() {
        IslandManager islandManager = skyblock.getIslandManager();
        FileManager fileManager = skyblock.getFileManager();

        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable")) return;
        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!hasScoreboard(all)) continue;

            Scoreboard scoreboard = getScoreboard(all);

            Island island = islandManager.getIsland(all);

            if (island == null) {
                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
                scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
            } else {
                if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
                    scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));

                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                        scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
                    } else {
                        scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
                    }
                } else {
                    scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));

                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                        scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
                    } else {
                        scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
                    }

                    Map<String, String> displayVariables = new HashMap<>();
                    displayVariables.put("%owner", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
                    displayVariables.put("%operator", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Operator"));
                    displayVariables.put("%member", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));

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
