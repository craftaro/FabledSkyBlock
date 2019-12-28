package com.songoda.skyblock.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.localization.type.Localization;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.NMSUtil;

public class Scoreboard {

    private final SkyBlock plugin;
    private final Player player;

    private String displayName;
    private List<String> displayList;

    private BukkitTask scheduler;

    public Scoreboard(SkyBlock plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        displayList = new ArrayList<>();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayList(List<String> displayList) {
        this.displayList = displayList;
    }

    public void run() {
        if (scheduler != null) scheduler.cancel();

        new BukkitRunnable() {
            @Override
            public void run() {
                final org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

                new BukkitRunnable() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void run() {
                        String ranStr = UUID.randomUUID().toString().split("-")[0];
                        Objective obj;

                        if (scoreboard.getObjective(player.getName()) != null) {
                            obj = scoreboard.getObjective(player.getName());
                        } else {
                            obj = scoreboard.registerNewObjective(player.getName(), "dummy");
                        }

                        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                        String formattedDisplayName = ChatColor.translateAlternateColorCodes('&', replaceDisplayName(displayName));
                        int max = NMSUtil.getVersionNumber() > 8 ? 32 : 16;
                        if (formattedDisplayName.length() > max) {
                            obj.setDisplayName(ChatColor.RED + "Too long...");
                        } else {
                            obj.setDisplayName(formattedDisplayName);
                        }

                        for (int i = 0; i < ChatColor.values().length; i++) {
                            if (i == displayList.size()) {
                                break;
                            }

                            ChatColor chatColor = ChatColor.values()[i];
                            Team team = scoreboard.registerNewTeam(ranStr + i);
                            team.addEntry(chatColor.toString());
                            obj.getScore(chatColor.toString()).setScore(i);
                        }

                        scheduler = new BukkitRunnable() {
                            int i1 = displayList.size();

                            @Override
                            public void run() {
                                if (!player.isOnline()) cancel();

                                try {
                                    String formattedDisplayName = ChatColor.translateAlternateColorCodes('&', replaceDisplayName(displayName));

                                    if (formattedDisplayName.length() > max) {
                                        obj.setDisplayName(ChatColor.RED + "Too long...");
                                    } else {
                                        obj.setDisplayName(formattedDisplayName);
                                    }

                                    for (String displayLine : displayList) {
                                        i1--;

                                        displayLine = replaceDisplayLine(displayLine);

                                        if (displayLine.length() > 32) {
                                            displayLine = "&cLine too long...";
                                        }

                                        if (displayLine.length() >= 16) {
                                            String prefixLine = displayLine.substring(0, Math.min(displayLine.length(), 16));
                                            String suffixLine = displayLine.substring(16, Math.min(displayLine.length(), displayLine.length()));

                                            if (prefixLine.substring(prefixLine.length() - 1).equals("&")) {
                                                prefixLine = ChatColor.translateAlternateColorCodes('&', prefixLine.substring(0, prefixLine.length() - 1));
                                                suffixLine = ChatColor.translateAlternateColorCodes('&', "&" + suffixLine);
                                            } else {
                                                String lastColorCodes;

                                                if (prefixLine.contains("&")) {
                                                    String[] colorCodes = prefixLine.split("&");
                                                    String lastColorCodeText = colorCodes[colorCodes.length - 1];
                                                    lastColorCodes = "&" + lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1));

                                                    if ((colorCodes.length >= 2) && (lastColorCodes.equals("&l") || lastColorCodes.equals("&m") || lastColorCodes.equals("&n") || lastColorCodes.equals("&o"))) {
                                                        lastColorCodeText = colorCodes[colorCodes.length - 2];
                                                        lastColorCodes = "&" + lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1)) + lastColorCodes;
                                                    }
                                                } else {
                                                    lastColorCodes = "&f";
                                                }

                                                prefixLine = ChatColor.translateAlternateColorCodes('&', prefixLine);
                                                suffixLine = ChatColor.translateAlternateColorCodes('&', lastColorCodes + suffixLine);
                                            }

                                            scoreboard.getTeam(ranStr + i1).setPrefix(prefixLine);
                                            scoreboard.getTeam(ranStr + i1).setSuffix(suffixLine);
                                        } else {
                                            scoreboard.getTeam(ranStr + i1).setPrefix(ChatColor.translateAlternateColorCodes('&', displayLine));
                                        }
                                    }

                                    i1 = displayList.size();
                                } catch (Exception e) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(plugin, 0L, 20L);

                        player.setScoreboard(scoreboard);
                    }
                }.runTaskAsynchronously(plugin);
            }
        }.runTask(plugin);
    }

    private String replaceDisplayName(String displayName) {
        displayName = displayName.replace("%players_online", "" + Bukkit.getServer().getOnlinePlayers().size()).replace("%players_max", "" + Bukkit.getServer().getMaxPlayers());

        return displayName;
    }

    private String replaceDisplayLine(String displayLine) {
        SkyBlock skyblock = SkyBlock.getInstance();

        IslandManager islandManager = skyblock.getIslandManager();

        displayLine = displayLine.replace("%players_online", "" + Bukkit.getServer().getOnlinePlayers().size()).replace("%players_max", "" + Bukkit.getServer().getMaxPlayers());

        Island island = islandManager.getIsland(player);

        if (island != null) {
            IslandLevel level = island.getLevel();

            if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
                displayLine = displayLine.replace("%island_level", "" + NumberUtil.formatNumberByDecimal(level.getLevel())).replace("%island_members", ChatColor.RED + "0").replace("%island_role", ChatColor.RED + "null")
                        .replace("%island_visitors", "" + islandManager.getVisitorsAtIsland(island).size()).replace("%island_size", "" + island.getSize()).replace("%island_radius", "" + island.getRadius());
            } else {
                int islandMembers = 1 + island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size();
                String islandRole = "";

                
                Localization<IslandRole> locale = skyblock.getLocalizationManager().getLocalizationFor(IslandRole.class);
                
                if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                    islandRole = locale.getLocale(IslandRole.Owner);
                } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
                    islandRole = locale.getLocale(IslandRole.Operator);
                } else if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
                    islandRole = locale.getLocale(IslandRole.Member);;
                }

                displayLine = displayLine.replace("%island_points", "" + NumberUtil.formatNumberByDecimal(level.getPoints())).replace("%island_level", "" + NumberUtil.formatNumberByDecimal(level.getLevel()))
                        .replace("%island_members", "" + islandMembers).replace("%island_role", islandRole).replace("%island_visitors", "" + islandManager.getVisitorsAtIsland(island).size())
                        .replace("%island_size", "" + island.getSize()).replace("%island_radius", "" + island.getRadius());
            }
        } else {
            displayLine = displayLine.replace("%island_points", ChatColor.RED + "0").replace("%island_level", ChatColor.RED + "0").replace("%island_members", ChatColor.RED + "0").replace("%island_role", ChatColor.RED + "null")
                    .replace("%island_size", ChatColor.RED + "0").replace("%island_radius", ChatColor.RED + "0");
        }

        PlaceholderManager placeholderManager = skyblock.getPlaceholderManager();

        if (placeholderManager.isPlaceholderAPIEnabled()) {
            displayLine = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, displayLine.replace("&", "clr")).replace("clr", "&");
        }

        return displayLine;
    }
}
