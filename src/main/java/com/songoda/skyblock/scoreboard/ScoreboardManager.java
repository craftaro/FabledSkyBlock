package com.songoda.skyblock.scoreboard;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager {
    
    private final SkyBlock plugin;
    private final Map<UUID, Scoreboard> scoreboardStorage = new ConcurrentHashMap<>();
    
    private final PlayerDataManager playerDataManager;

    private final List<String> teamNames = new ArrayList<>();
    private final List<String> objectiveNames = new ArrayList<>();

    public ScoreboardManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
        Bukkit.getScheduler().runTask(plugin, () -> reloadScoreboards(true));
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateScoreboards,  20L, 40L);
    }

    private synchronized void updateScoreboards() {
        final org.bukkit.scoreboard.Scoreboard primary = Bukkit.getScoreboardManager().getMainScoreboard();
        final Set<Objective> objectives = primary.getObjectives();
        final Set<Team> teams = primary.getTeams();

        /*
         * Unregister all teams or objectives that are no longer present in the main
         * scoreboard.
         */
        for (UUID uuid : scoreboardStorage.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
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
        }

        /*
         * Update the objective/team names.
         */

        objectiveNames.clear();
        teamNames.clear();

        for(Objective objective : objectives) {
            if (primary.getObjective(objective.getName()) != null) {
                objectiveNames.add(objective.getName());
            }
        }

        for(Team team : teams) {
            if (primary.getTeam(team.getName()) != null) {
                teamNames.add(team.getName());
            }
        }

        /*
         * Update or add any missing information to the player's scoreboard.
         */

        for (UUID uuid : scoreboardStorage.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                PlayerData pd = playerDataManager.getPlayerData(player);
                if(pd != null && pd.isScoreboard()){
                    final org.bukkit.scoreboard.Scoreboard playerBoard = player.getScoreboard();
    
                    for (Objective primaryObjective : objectives) {
        
                        Objective obj = playerBoard.getObjective(primaryObjective.getName());
        
                        if (obj == null)
                            obj = playerBoard.registerNewObjective(primaryObjective.getName(), primaryObjective.getCriteria());
        
                        obj.setDisplayName(primaryObjective.getDisplayName());
                        obj.setDisplaySlot(primaryObjective.getDisplaySlot());
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) obj.setRenderType(primaryObjective.getRenderType());
                    }
    
                    for (Team primaryTeam : teams) {
        
                        Team obj = playerBoard.getTeam(primaryTeam.getName());
        
                        if (obj == null) obj = playerBoard.registerNewTeam(primaryTeam.getName());
        
                        obj.setAllowFriendlyFire(primaryTeam.allowFriendlyFire());
                        obj.setCanSeeFriendlyInvisibles(primaryTeam.canSeeFriendlyInvisibles());
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) obj.setColor(primaryTeam.getColor());
                        obj.setDisplayName(primaryTeam.getDisplayName());
                        obj.setNameTagVisibility(primaryTeam.getNameTagVisibility());
                        obj.setPrefix(primaryTeam.getPrefix());
                        obj.setSuffix(primaryTeam.getSuffix());
        
                        for (String primaryEntry : primaryTeam.getEntries()) {
                            obj.addEntry(primaryEntry);
                        }
        
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                            for (Option option : Option.values()) {
                                obj.setOption(option, primaryTeam.getOption(option));
                            }
                        }
                    }
                }
            }
        }
    }

    public synchronized void reloadScoreboards(boolean createNew) {

        FileManager fileManager = plugin.getFileManager();

        if (!fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable"))
            return;

        for (Player all : Bukkit.getOnlinePlayers()) {

            boolean store = false;

            Scoreboard scoreboard = null;
            if (hasScoreboard(all))
                scoreboard = getScoreboard(all);
            else {
                if (createNew) {
                    scoreboard = new Scoreboard(plugin, all);
                    store = true;
                }
            }

            if (scoreboard == null) continue;

            IslandManager islandManager = plugin.getIslandManager();
            Config language = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
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

    public synchronized void storeScoreboard(Player player, Scoreboard scoreboard) {
        scoreboardStorage.put(player.getUniqueId(), scoreboard);
    }

    public synchronized Scoreboard getScoreboard(Player player) {
        if (scoreboardStorage.containsKey(player.getUniqueId())) {
            return scoreboardStorage.get(player.getUniqueId());
        }

        return null;
    }

    public synchronized boolean hasScoreboard(Player player) {
        return scoreboardStorage.containsKey(player.getUniqueId());
    }
    
    public synchronized Map<UUID, Scoreboard> getScoreboardStorage() {
        return this.scoreboardStorage;
    }
    
    public synchronized void addPlayer(Player player){
        CooldownManager cooldownManager = plugin.getCooldownManager();
        FileManager fileManager = plugin.getFileManager();
        IslandManager islandManager = plugin.getIslandManager();
        
        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        Scoreboard scoreboard = new Scoreboard(plugin, player);
        Island island = islandManager.getIsland(player);
    
        if (island != null) {
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
        
            cooldownManager.addCooldownPlayer(CooldownType.Levelling, cooldownManager.loadCooldownPlayer(CooldownType.Levelling, offlinePlayer));
            cooldownManager.addCooldownPlayer(CooldownType.Ownership, cooldownManager.loadCooldownPlayer(CooldownType.Ownership, offlinePlayer));
        
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
            
            }
        } else {
            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
            scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
        }
    
        scoreboard.run();
        this.storeScoreboard(player, scoreboard);
    }
    
    public synchronized void removePlayer(Player player){
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        this.scoreboardStorage.remove(player.getUniqueId());
    }
}