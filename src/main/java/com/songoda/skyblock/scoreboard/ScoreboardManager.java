package com.songoda.skyblock.scoreboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;

import java.io.File;
import java.util.*;

public class ScoreboardManager extends BukkitRunnable {

    private final static int VERSION = NMSUtil.getVersionNumber();
    private final SkyBlock skyblock;
    private final Map<UUID, Scoreboard> scoreboardStorage = new HashMap<>();

    private int runTicks = 0;

    private final List<String> teamNames = new ArrayList<>();
    private final List<String> objectiveNames = new ArrayList<>();

    public ScoreboardManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
        this.runTaskTimer(skyblock, 20, 40);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {

        if (runTicks++ == 0) {
            updateScoreboards(true);
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

                if (obj == null)
                    obj = playerBoard.registerNewObjective(primaryObjective.getName(), primaryObjective.getCriteria());

                obj.setDisplayName(primaryObjective.getDisplayName());
                obj.setDisplaySlot(primaryObjective.getDisplaySlot());
                if (VERSION > 12) obj.setRenderType(primaryObjective.getRenderType());
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

    public void updateScoreboards(boolean createNew) {

        FileManager fileManager = skyblock.getFileManager();

        if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable"))
            return;

        for (Player all : Bukkit.getOnlinePlayers()) {

            boolean store = false;

            Scoreboard scoreboard = null;
            if (hasScoreboard(all))
                scoreboard = getScoreboard(all);
            else {
                if (createNew) {
                    scoreboard = new Scoreboard(skyblock, all);
                    store = true;
                }
            }

            if (scoreboard == null) continue;

            IslandManager islandManager = skyblock.getIslandManager();
            Config language = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
            Island island = islandManager.getIsland(all);

            if (island == null) {
                scoreboard.setDisplayName(color(language.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
                scoreboard.setDisplayList(language.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
            } else {
                if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
                    scoreboard.setDisplayName(color(language.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));

                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                        scoreboard.setDisplayList(language.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
                    } else {
                        scoreboard.setDisplayList(language.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
                    }
                } else {
                    scoreboard.setDisplayName(color(language.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));

                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                        scoreboard.setDisplayList(language.getFileConfiguration().getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
                    } else {
                        scoreboard.setDisplayList(language.getFileConfiguration().getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
                    }
                }
            }

            scoreboard.run();
            if (store) storeScoreboard(all, scoreboard);
        }
    }

    private String color(String str) {
        return str != null ? ChatColor.translateAlternateColorCodes('&', str) : null;
    }

    public void storeScoreboard(Player player, Scoreboard scoreboard) {
        scoreboardStorage.put(player.getUniqueId(), scoreboard);
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