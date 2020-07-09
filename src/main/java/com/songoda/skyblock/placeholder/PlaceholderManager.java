package com.songoda.skyblock.placeholder;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.upgrade.Upgrade.Type;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.visit.VisitManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlaceholderManager {

    private final SkyBlock plugin;

    private boolean PlaceholderAPI = false;
    private boolean MVdWPlaceholderAPI = false;

    public PlaceholderManager(SkyBlock plugin) {
        this.plugin = plugin;

        PluginManager pluginManager = plugin.getServer().getPluginManager();

        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI = true;
        }

        if (pluginManager.getPlugin("MVdWPlaceholderAPI") != null) {
            MVdWPlaceholderAPI = true;
        }
    }

    public void registerPlaceholders() {
        if (PlaceholderAPI) {
            new EZPlaceholder(plugin).register();
        }

        if (MVdWPlaceholderAPI) {
            new MVdWPlaceholder(plugin).register();
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        return PlaceholderAPI;
    }

    public boolean isMVdWPlaceholderAPIEnabled() {
        return MVdWPlaceholderAPI;
    }

    public String getPlaceholder(Player player, String placeholder) {
        IslandManager islandManager = plugin.getIslandManager();
        VisitManager visitManager = plugin.getVisitManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();

        Island island = islandManager.getIsland(player);

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (placeholder.equalsIgnoreCase("fabledskyblock_island_exists")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_exists.Not-exists.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_exists.Exists.Message"));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_isopen")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_isopen.Empty.Message"));
            } else {
                if (island.getStatus().equals(IslandStatus.OPEN)) { // TODO Update to Status
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_isopen.Open.Message"));
                } else {
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_isopen.Closed.Message"));
                }
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_size")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_size.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_size.Non-empty.Message")
                                .replace("%placeholder", "" + island.getSize()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_radius")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_radius.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_radius.Non-empty.Message")
                                .replace("%placeholder", "" + island.getRadius()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_level")) {
            return island == null || island.getLevel() == null ? "0": Long.toString(island.getLevel().getLevel());
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_level_formatted")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_level_formatted.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_level_formatted.Non-empty.Message").replace(
                                "%placeholder", "" + NumberUtil.formatNumberBySuffix(island.getLevel().getLevel())));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_points")) {
            return island == null ? "0": Long.toString(island.getLevel().getPoints());
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_votes")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_votes.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_votes.Non-empty.Message")
                                .replace("%placeholder", "" + visitManager.getIslands().get(player.getUniqueId()).getVoters().size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_role")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_role.Empty.Message"));
            } else {
                for (IslandRole roleList : IslandRole.values()) {
                    if (island.hasRole(roleList, player.getUniqueId())) {
                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_island_role.Non-empty.Message")
                                        .replace("%placeholder", plugin.getLocalizationManager().getLocalizationFor(IslandRole.class).getLocale(roleList)));
                    }
                }
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_owner")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_owner.Empty.Message"));
            } else {
                UUID islandOwnerUUID = island.getOwnerUUID();
                Player targetPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);

                if (targetPlayer == null) {
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_owner.Non-empty.Other.Message").replace(
                                    "%placeholder", Bukkit.getServer().getOfflinePlayer(islandOwnerUUID).getName()));
                } else {
                    if (targetPlayer.getName().equals(player.getName())) {
                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_island_owner.Non-empty.Yourself.Message")
                                        .replace("%placeholder", targetPlayer.getName()));
                    } else {
                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_island_owner.Non-empty.Other.Message")
                                        .replace("%placeholder", targetPlayer.getName()));
                    }
                }
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_biome")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_biome.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_biome.Non-empty.Message")
                                .replace("%placeholder", island.getBiomeName()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_time")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_time.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_time.Non-empty.Message")
                                .replace("%placeholder", "" + island.getTime()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_weather")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_weather.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_weather.Non-empty.Message")
                                .replace("%placeholder", "" + island.getWeatherName()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_bans")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_bans.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_bans.Non-empty.Message")
                                .replace("%placeholder", "" + island.getBan().getBans().size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_members_total")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_members_total.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_members_total.Non-empty.Message")
                                .replace("%placeholder", "" + (island.getRole(IslandRole.Member).size()
                                        + island.getRole(IslandRole.Operator).size() + 1)));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_members")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_members.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_members.Non-empty.Message")
                                .replace("%placeholder", "" + island.getRole(IslandRole.Member).size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_maxmembers")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_maxmembers.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_maxmembers.Non-empty.Message")
                                .replace("%placeholder", "" + island.getMaxMembers()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_operators")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_operators.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_operators.Non-empty.Message")
                                .replace("%placeholder", "" + island.getRole(IslandRole.Operator).size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_coops")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_coops.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_coops.Non-empty.Message")
                                .replace("%placeholder", "" + islandManager.getCoopPlayersAtIsland(island).size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_coops_total")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_coops_total.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_coops_total.Non-empty.Message")
                                .replace("%placeholder", "" + island.getCoopPlayers().size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_visitors")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_visitors.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_visitors.Non-empty.Message")
                                .replace("%placeholder", "" + islandManager.getVisitorsAtIsland(island).size()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_invites")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_invites.Empty.Message"));
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

                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_invites.Non-empty.Message")
                                .replace("%placeholder", "" + invitedPlayers));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_bank_balance")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_bank_balance.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_bank_balance.Non-empty.Message"))
                        .replace("%placeholder", "" + NumberUtil.formatNumberByDecimal(island.getBankBalance()));
            }
        } else if (placeholder.equalsIgnoreCase("fabledskyblock_island_bank_balance_formatted")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_bank_balance_formatted.Empty.Message"));
            } else {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_bank_balance_formatted.Non-empty.Message"))
                        .replace("%placeholder", "" + NumberUtil.formatNumberBySuffix((long) island.getBankBalance()));
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_level_rank")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_leaderboard_level_rank.Empty.Message"));
            } else {
                LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
                int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.Level);
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_leaderboard_level_rank.Non-empty.Message")
                                .replace("%placeholder", "" + rank));
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_bank_rank")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_leaderboard_bank_rank.Empty.Message"));
            } else {
                LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
                int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.Bank);
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_leaderboard_bank_rank.Non-empty.Message")
                                .replace("%placeholder", "" + rank));
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_votes_rank")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_leaderboard_votes_rank.Empty.Message"));
            } else {
                LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
                int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.Votes);
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_leaderboard_votes_rank.Non-empty.Message")
                                .replace("%placeholder", "" + rank));
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_level_block_count_")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_level_block_count.Empty.Message"));
            } else {
                String materialName = placeholder.replace("fabledskyblock_island_level_block_count_", "").toUpperCase();
                CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);
                if (materials == null) {
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_level_block_count.Invalid.Message"));
                } else {
                    long blockCount = island.getLevel().getMaterialAmount(materials.name());
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_level_block_count.Non-empty.Message")
                                    .replace("%placeholder", NumberUtil.formatNumberByDecimal(blockCount)));
                }
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_level_block_points_")) {
            if (island == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_island_level_block_points.Empty.Message"));
            } else {
                String materialName = placeholder.replace("fabledskyblock_island_level_block_points_", "").toUpperCase();
                CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);
                if (materials == null) {
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_level_block_points.Invalid.Message"));
                } else {
                    long blockPoints = island.getLevel().getMaterialPoints(materials.name());
                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_island_level_block_points.Non-empty.Message")
                                    .replace("%placeholder", NumberUtil.formatNumberByDecimal(blockPoints)));
                }
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_level_block_value_")) {
            String materialName = placeholder.replace("fabledskyblock_level_block_value_", "").toUpperCase();
            CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);
            if (materials == null) {
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_level_block_value.Invalid.Message"));
            } else {
                long blockValue = levellingManager.getWorth(materials);
                return ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Placeholder.fabledskyblock_level_block_value.Non-empty.Message")
                                .replace("%placeholder", NumberUtil.formatNumberByDecimal(blockValue)));
            }
        } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_has_upgrade_")) {
            Type type;

            final String lower = placeholder.replace("fabledskyblock_island_has_upgrade_", "").toLowerCase();

            if (lower.isEmpty()) return "";

            final String toParse = lower.substring(0, 1).toUpperCase() + lower.substring(1);

            try {
                type = Upgrade.Type.valueOf(toParse);
            } catch (IllegalArgumentException ignored) {
                type = null;
            }

            if (type == null) return "Invalid type '" + toParse + "'";

            return Boolean.toString(island.hasUpgrade(type));
        }

        return "";
    }

    public List<String> getPlaceholders() {
        List<String> placeholders = new ArrayList<>(25);
        placeholders.add("fabledskyblock_island_exists");
        placeholders.add("fabledskyblock_island_isopen");
        placeholders.add("fabledskyblock_island_size");
        placeholders.add("fabledskyblock_island_radius");
        placeholders.add("fabledskyblock_island_level");
        placeholders.add("fabledskyblock_island_level_formatted");
        placeholders.add("fabledskyblock_island_points");
        placeholders.add("fabledskyblock_island_votes");
        placeholders.add("fabledskyblock_island_role");
        placeholders.add("fabledskyblock_island_owner");
        placeholders.add("fabledskyblock_island_biome");
        placeholders.add("fabledskyblock_island_time");
        placeholders.add("fabledskyblock_island_weather");
        placeholders.add("fabledskyblock_island_bans");
        placeholders.add("fabledskyblock_island_members_total");
        placeholders.add("fabledskyblock_island_members");
        placeholders.add("fabledskyblock_island_maxmembers");
        placeholders.add("fabledskyblock_island_operators");
        placeholders.add("fabledskyblock_island_coops");
        placeholders.add("fabledskyblock_island_coops_total");
        placeholders.add("fabledskyblock_island_visitors");
        placeholders.add("fabledskyblock_island_invites");
        placeholders.add("fabledskyblock_island_bank_balance");
        placeholders.add("fabledskyblock_island_leaderboard_level_rank");
        placeholders.add("fabledskyblock_island_leaderboard_bank_rank");
        placeholders.add("fabledskyblock_island_leaderboard_votes_rank");
        //placeholders.add("fabledskyblock_island_level_block_count_");
        //placeholders.add("fabledskyblock_island_level_block_points_");
        //placeholders.add("fabledskyblock_level_block_value_");

        return placeholders;
    }
}
