package com.songoda.skyblock.placeholder;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.visit.VisitManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class PlaceholderProcessor {
    
    public String processPlaceholder(Player player, String placeholder) {
        if(player == null || placeholder == null) {
            return "";
        }
        
        SkyBlock plugin = SkyBlock.getInstance();
        IslandManager islandManager = plugin.getIslandManager();
        VisitManager visitManager = plugin.getVisitManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();

        FileManager fileManager = plugin.getFileManager();
        FileConfiguration placeholdersLoad = fileManager.getConfig(
                new File(plugin.getDataFolder(), "placeholders.yml")).getFileConfiguration();

        if(placeholdersLoad == null) {
            return "Error";
        }

        Island island = islandManager.getIsland(player);
    
        String returnValue = null;
        
        switch (placeholder.toLowerCase()) {
            case "fabledskyblock_island_exists":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_exists.Not-exists.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_exists.Exists.Message"));
                }
                break;
            case "fabledskyblock_island_isopen":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_isopen.Empty.Message"));
                } else {
                    if (island.getStatus().equals(IslandStatus.OPEN)) { // TODO Update to Status
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_isopen.Open.Message"));
                    } else {
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_isopen.Closed.Message"));
                    }
                }
                break;
            case "fabledskyblock_island_size":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_size.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_size.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getSize()));
                }
                break;
            case "fabledskyblock_island_radius":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_radius.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_radius.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getRadius()));
                }
                break;
            case "fabledskyblock_island_level":
                returnValue = island == null || island.getLevel() == null ? "0" : Long.toString(island.getLevel().getLevel());
                break;
            case "fabledskyblock_island_level_formatted":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_formatted.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_formatted.Non-empty.Message").replace(
                                    "%placeholder", "" + NumberUtil.formatNumberBySuffix(island.getLevel().getLevel())));
                }
                break;
            case "fabledskyblock_island_points":
                returnValue = island == null ? "0" : Long.toString(island.getLevel().getPoints());
                break;
            case "fabledskyblock_island_votes":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_votes.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_votes.Non-empty.Message")
                                    .replace("%placeholder", "" + visitManager.getIslands().get(player.getUniqueId()).getVoters().size()));
                }
                break;
            case "fabledskyblock_island_role":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_role.Empty.Message"));
                } else {
                    for (IslandRole roleList : IslandRole.values()) {
                        if (island.hasRole(roleList, player.getUniqueId())) {
                            returnValue = ChatColor.translateAlternateColorCodes('&',
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_role.Non-empty.Message")
                                            .replace("%placeholder", plugin.getLocalizationManager().getLocalizationFor(IslandRole.class).getLocale(roleList)));
                        }
                    }
                }
                break;
            case "fabledskyblock_island_owner":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Empty.Message"));
                } else {
                    UUID islandOwnerUUID = island.getOwnerUUID();
                    Player targetPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);
    
                    if (targetPlayer == null) {
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Non-empty.Other.Message").replace(
                                        "%placeholder", Bukkit.getServer().getOfflinePlayer(islandOwnerUUID).getName()));
                    } else {
                        if (targetPlayer.getName().equals(player.getName())) {
                            returnValue = ChatColor.translateAlternateColorCodes('&',
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Non-empty.Yourself.Message")
                                            .replace("%placeholder", targetPlayer.getName()));
                        } else {
                            returnValue = ChatColor.translateAlternateColorCodes('&',
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Non-empty.Other.Message")
                                            .replace("%placeholder", targetPlayer.getName()));
                        }
                    }
                }
                break;
            case "fabledskyblock_island_biome":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_biome.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_biome.Non-empty.Message")
                                    .replace("%placeholder", island.getBiomeName()));
                }
                break;
            case "fabledskyblock_island_time":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_time.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_time.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getTime()));
                }
                break;
            case "fabledskyblock_island_weather":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_weather.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_weather.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getWeatherName()));
                }
                break;
            case "fabledskyblock_island_bans":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bans.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bans.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getBan().getBans().size()));
                }
                break;
            case "fabledskyblock_island_members_total":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members_total.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members_total.Non-empty.Message")
                                    .replace("%placeholder", "" + (island.getRole(IslandRole.Member).size()
                                            + island.getRole(IslandRole.Operator).size() + 1)));
                }
                break;
            case "fabledskyblock_island_members":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_members.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getRole(IslandRole.Member).size()));
                }
                break;
            case "fabledskyblock_island_maxmembers":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_maxmembers.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_maxmembers.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getMaxMembers()));
                }
                break;
            case "fabledskyblock_island_operators":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_operators.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_operators.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getRole(IslandRole.Operator).size()));
                }
                break;
            case "fabledskyblock_island_coops":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops.Non-empty.Message")
                                    .replace("%placeholder", "" + islandManager.getCoopPlayersAtIsland(island).size()));
                }
                break;
            case "fabledskyblock_island_coops_total":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops_total.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops_total.Non-empty.Message")
                                    .replace("%placeholder", "" + island.getCoopPlayers().size()));
                }
                break;
            case "fabledskyblock_island_visitors":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_visitors.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_visitors.Non-empty.Message")
                                    .replace("%placeholder", "" + islandManager.getVisitorsAtIsland(island).size()));
                }
                break;
            case "fabledskyblock_island_invites":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_invites.Empty.Message"));
                } else {
                    Map<UUID, Invite> invites = plugin.getInviteManager().getInvites();
                    int invitedPlayers = 0;
    
                    for (int i = 0; i < invites.size(); i++) {
                        UUID uuid = (UUID) invites.keySet().toArray()[i];
                        Invite invite = invites.get(uuid);
        
                        if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
                            invitedPlayers++;
                        }
                    }
    
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_invites.Non-empty.Message")
                                    .replace("%placeholder", "" + invitedPlayers));
                }
                break;
            case "fabledskyblock_island_bank_balance":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance.Non-empty.Message"))
                            .replace("%placeholder", "" + NumberUtil.formatNumberByDecimal(island.getBankBalance()));
                }
                break;
            case "fabledskyblock_island_bank_balance_formatted":
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance_formatted.Empty.Message"));
                } else {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance_formatted.Non-empty.Message"))
                            .replace("%placeholder", "" + NumberUtil.formatNumberBySuffix((long) island.getBankBalance()));
                }
                break;
        }
            
        if(returnValue == null) {
            if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_level_rank")) {
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_leaderboard_level_rank.Empty.Message"));
                } else {
                    LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
                    int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.Level);
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_leaderboard_level_rank.Non-empty.Message")
                                    .replace("%placeholder", "" + rank));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_bank_rank")) {
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_leaderboard_bank_rank.Empty.Message"));
                } else {
                    LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
                    int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.Bank);
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_leaderboard_bank_rank.Non-empty.Message")
                                    .replace("%placeholder", "" + rank));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_votes_rank")) {
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_leaderboard_votes_rank.Empty.Message"));
                } else {
                    LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
                    int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.Votes);
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_leaderboard_votes_rank.Non-empty.Message")
                                    .replace("%placeholder", "" + rank));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_level_block_count_")) {
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_level_block_count.Empty.Message"));
                } else {
                    String materialName = placeholder.replace("fabledskyblock_island_level_block_count_", "").toUpperCase();
                    CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);
                    if (materials == null) {
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholder.fabledskyblock_island_level_block_count.Invalid.Message"));
                    } else {
                        long blockCount = island.getLevel().getMaterialAmount(materials.name());
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholder.fabledskyblock_island_level_block_count.Non-empty.Message")
                                        .replace("%placeholder", NumberUtil.formatNumberByDecimal(blockCount)));
                    }
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_level_block_points_")) {
                if (island == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_island_level_block_points.Empty.Message"));
                } else {
                    String materialName = placeholder.replace("fabledskyblock_island_level_block_points_", "").toUpperCase();
                    CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);
                    if (materials == null) {
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholder.fabledskyblock_island_level_block_points.Invalid.Message"));
                    } else {
                        long blockPoints = island.getLevel().getMaterialPoints(materials.name());
                        returnValue = ChatColor.translateAlternateColorCodes('&',
                                placeholdersLoad.getString("Placeholder.fabledskyblock_island_level_block_points.Non-empty.Message")
                                        .replace("%placeholder", NumberUtil.formatNumberByDecimal(blockPoints)));
                    }
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_level_block_value_")) {
                String materialName = placeholder.replace("fabledskyblock_level_block_value_", "").toUpperCase();
                CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);
                if (materials == null) {
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_level_block_value.Invalid.Message"));
                } else {
                    double blockValue = levellingManager.getWorth(materials);
                    returnValue = ChatColor.translateAlternateColorCodes('&',
                            placeholdersLoad.getString("Placeholder.fabledskyblock_level_block_value.Non-empty.Message")
                                    .replace("%placeholder", NumberUtil.formatNumberByDecimal(blockValue)));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_has_upgrade_")) {
                Upgrade.Type type;
        
                final String lower = placeholder.replace("fabledskyblock_island_has_upgrade_", "").toLowerCase();
    
                if (!lower.isEmpty()) {
                    final String toParse = lower.substring(0, 1).toUpperCase() + lower.substring(1);
    
                    try {
                        type = Upgrade.Type.valueOf(toParse);
                        returnValue = Boolean.toString(island.hasUpgrade(type));
                    } catch (IllegalArgumentException ignored) {
                        returnValue = "Invalid type '" + toParse + "'";
                    }
                } else {
                    returnValue = "";
                }
            }
        }
        
        return returnValue;
    }
}
