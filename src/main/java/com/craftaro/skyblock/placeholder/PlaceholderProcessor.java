package com.craftaro.skyblock.placeholder;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.invite.Invite;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandLevel;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.leaderboard.Leaderboard;
import com.craftaro.skyblock.leaderboard.LeaderboardManager;
import com.craftaro.skyblock.levelling.IslandLevelManager;
import com.craftaro.skyblock.upgrade.Upgrade;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.visit.Visit;
import com.craftaro.skyblock.visit.VisitManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlaceholderProcessor {
    public String processPlaceholder(Player player, String placeholder) {
        if (player == null || placeholder == null) {
            return "";
        }

        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);
        IslandManager islandManager = plugin.getIslandManager();
        VisitManager visitManager = plugin.getVisitManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();
        LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();

        FileManager fileManager = plugin.getFileManager();
        FileConfiguration placeholdersLoad = fileManager.getConfig(
                new File(plugin.getDataFolder(), "placeholders.yml")).getFileConfiguration();

        if (placeholdersLoad == null) {
            return "Error";
        }

        Island island = islandManager.getIsland(player);

        String returnValue = null;

        switch (placeholder.toLowerCase()) {
            case "fabledskyblock_island_exists":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_exists.Not-exists"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_exists.Exists"));
                }
                break;
            case "fabledskyblock_island_isopen": //Deprecated
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_isopen.Empty"));
                } else {
                    if (island.getStatus().equals(IslandStatus.OPEN)) {
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_isopen.Open"));
                    } else {
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_isopen.Closed"));
                    }
                }
                break;
            case "fabledskyblock_island_status":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_status.Empty"));
                } else {
                    switch (island.getStatus()) {
                        case OPEN:
                            returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_status.Open"));
                            break;
                        case CLOSED:
                            returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_status.Closed"));
                            break;
                        case WHITELISTED:
                            returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_status.Whitelisted"));
                            break;
                    }
                }
                break;
            case "fabledskyblock_island_size":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_size.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_size.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getSize()));
                }
                break;
            case "fabledskyblock_island_radius":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_radius.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_radius.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getRadius()));
                }
                break;
            case "fabledskyblock_island_level":
                returnValue = island == null || island.getLevel() == null ? "0" : Long.toString(island.getLevel().getLevel());
                break;
            case "fabledskyblock_island_level_formatted":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_formatted.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_formatted.Non-empty").replace(
                                    "{PLACEHOLDER}", NumberUtils.formatWithSuffix(island.getLevel().getLevel())));
                }
                break;
            case "fabledskyblock_island_points":
                returnValue = island == null ? "0" : Double.toString(island.getLevel().getPoints());
                break;
            case "fabledskyblock_island_votes":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_votes.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_votes.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + visitManager.getIslands().get(island.getOwnerUUID()).getVoters().size()));
                }
                break;
            case "fabledskyblock_island_role":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_role.Empty"));
                } else {
                    for (IslandRole roleList : IslandRole.values()) {
                        if (island.hasRole(roleList, player.getUniqueId())) {
                            returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_role.Non-empty")
                                            .replace("{PLACEHOLDER}", plugin.getLocalizationManager().getLocalizationFor(IslandRole.class).getLocale(roleList)));
                        }
                    }
                }
                break;
            case "fabledskyblock_island_owner":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Empty"));
                } else {
                    UUID islandOwnerUUID = island.getOwnerUUID();
                    Player targetPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);

                    if (targetPlayer == null) {
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Non-empty.Other").replace(
                                        "{PLACEHOLDER}", Bukkit.getServer().getOfflinePlayer(islandOwnerUUID).getName()));
                    } else {
                        if (targetPlayer.getName().equals(player.getName())) {
                            returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Non-empty.Yourself")
                                            .replace("{PLACEHOLDER}", targetPlayer.getName()));
                        } else {
                            returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_owner.Non-empty.Other")
                                            .replace("{PLACEHOLDER}", targetPlayer.getName()));
                        }
                    }
                }
                break;
            case "fabledskyblock_island_biome":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_biome.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_biome.Non-empty")
                                    .replace("{PLACEHOLDER}", island.getBiomeName()));
                }
                break;
            case "fabledskyblock_island_time":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_time.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_time.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getTime()));
                }
                break;
            case "fabledskyblock_island_weather":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_weather.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_weather.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getWeatherName()));
                }
                break;
            case "fabledskyblock_island_bans":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bans.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bans.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getBan().getBans().size()));
                }
                break;
            case "fabledskyblock_island_members_total":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members_total.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members_total.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + (island.getRole(IslandRole.MEMBER).size()
                                            + island.getRole(IslandRole.OPERATOR).size() + 1)));
                }
                break;
            case "fabledskyblock_island_members":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_members.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getRole(IslandRole.MEMBER).size()));
                }
                break;
            case "fabledskyblock_island_maxmembers":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_maxmembers.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_maxmembers.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getMaxMembers(player)));
                }
                break;
            case "fabledskyblock_island_operators":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_operators.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_operators.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getRole(IslandRole.OPERATOR).size()));
                }
                break;
            case "fabledskyblock_island_coops":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + islandManager.getCoopPlayersAtIsland(island).size()));
                }
                break;
            case "fabledskyblock_island_coops_total":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops_total.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_coops_total.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + island.getCoopPlayers().size()));
                }
                break;
            case "fabledskyblock_island_visitors":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_visitors.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_visitors.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + islandManager.getVisitorsAtIsland(island).size()));
                }
                break;
            case "fabledskyblock_island_invites":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_invites.Empty"));
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

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_invites.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + invitedPlayers));
                }
                break;
            case "fabledskyblock_island_bank_balance":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance.Non-empty"))
                            .replace("{PLACEHOLDER}", "" + NumberUtils.formatNumber(island.getBankBalance()));
                }
                break;
            case "fabledskyblock_island_bank_balance_formatted":
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance_formatted.Empty"));
                } else {
                    returnValue = TextUtils.formatText(
                                    placeholdersLoad.getString("Placeholders.fabledskyblock_island_bank_balance_formatted.Non-empty"))
                            .replace("{PLACEHOLDER}", "" + NumberUtils.formatWithSuffix((long) island.getBankBalance()));
                }
                break;
        }

        if (returnValue == null) {
            if (placeholder.toLowerCase().startsWith("fabledskyblock_leaderboard_votes_")) {
                List<Leaderboard> leaderboardVotesPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.VOTES);

                String[] values = placeholder.split("_");
                int value;
                try {
                    value = Integer.parseInt(values[values.length - 1]);
                } catch (NumberFormatException ignored) {
                    value = 1;
                }

                if (value > 0 && value < leaderboardVotesPlayers.size()) {
                    Leaderboard leaderboard = leaderboardVotesPlayers.get(value);
                    Visit visit = leaderboard.getVisit();

                    Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                    String islandOwnerName;

                    if (targetPlayer == null) {
                        islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                    } else {
                        islandOwnerName = targetPlayer.getName();
                    }

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_leaderboard_votes.Non-empty")
                                    .replace("{POSITION}", "" + (value))
                                    .replace("{PLAYER}", islandOwnerName)
                                    .replace("{VOTES}", NumberUtils.formatNumber(visit.getVoters().size())));
                } else {

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_leaderboard_votes.Empty"));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_leaderboard_bank_")) {
                List<Leaderboard> leaderboardBankPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.BANK);

                String[] values = placeholder.split("_");
                int value;
                try {
                    value = Integer.parseInt(values[values.length - 1]);
                } catch (NumberFormatException ignored) {
                    value = 1;
                }

                if (value > 0 && value < leaderboardBankPlayers.size()) {
                    Leaderboard leaderboard = leaderboardBankPlayers.get(value);
                    Visit visit = leaderboard.getVisit();

                    Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                    String islandOwnerName;

                    if (targetPlayer == null) {
                        islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                    } else {
                        islandOwnerName = targetPlayer.getName();
                    }

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_leaderboard_bank.Non-empty")
                                    .replace("{POSITION}", "" + (value))
                                    .replace("{PLAYER}", islandOwnerName)
                                    .replace("{BALANCE}", NumberUtils.formatNumber(visit.getBankBalance())));
                } else {

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_leaderboard_bank.Empty"));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_leaderboard_level_")) {
                List<Leaderboard> leaderboardLevelPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.LEVEL);


                String[] values = placeholder.split("_");
                int value;
                try {
                    value = Integer.parseInt(values[values.length - 1]);
                } catch (NumberFormatException ignored) {
                    value = 1;
                }

                if (value > 0 && value - 1 < leaderboardLevelPlayers.size()) {
                    value--;
                    Leaderboard leaderboard = leaderboardLevelPlayers.get(value);
                    Visit visit = leaderboard.getVisit();
                    IslandLevel level = visit.getLevel();

                    Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                    String islandOwnerName;

                    if (targetPlayer == null) {
                        islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                    } else {
                        islandOwnerName = targetPlayer.getName();
                    }

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_leaderboard_level.Non-empty")
                                    .replace("{POSITION}", "" + (value + 1))
                                    .replace("{PLAYER}", islandOwnerName)
                                    .replace("{LEVEL}", NumberUtils.formatNumber(level.getLevel()))
                                    .replace("{POINTS}", NumberUtils.formatNumber(level.getPoints())));
                } else {

                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_leaderboard_level.Empty"));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_level_rank")) {
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_leaderboard_level_rank.Empty"));
                } else {
                    int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.LEVEL);
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_leaderboard_level_rank.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + rank));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_bank_rank")) {
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_leaderboard_bank_rank.Empty"));
                } else {
                    int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.BANK);
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_leaderboard_bank_rank.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + rank));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_leaderboard_votes_rank")) {
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_leaderboard_votes_rank.Empty"));
                } else {
                    int rank = leaderboardManager.getPlayerIslandLeaderboardPosition(player, Leaderboard.Type.VOTES);
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_leaderboard_votes_rank.Non-empty")
                                    .replace("{PLACEHOLDER}", "" + rank));
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_level_block_count_")) {
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_block_count.Empty"));
                } else {
                    String materialName = placeholder.replace("fabledskyblock_island_level_block_count_", "").toUpperCase();
                    Optional<XMaterial> materials = CompatibleMaterial.getMaterial(materialName);
                    if (!materials.isPresent()) {
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_block_count.Invalid"));
                    } else {
                        long blockCount = island.getLevel().getMaterialAmount(materials.get().name());
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_block_count.Non-empty")
                                        .replace("{PLACEHOLDER}", NumberUtils.formatNumber(blockCount)));
                    }
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_island_level_block_points_")) {
                if (island == null) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_block_points.Empty"));
                } else {
                    String materialName = placeholder.replace("fabledskyblock_island_level_block_points_", "").toUpperCase();
                    Optional<XMaterial> materials = CompatibleMaterial.getMaterial(materialName);
                    if (!materials.isPresent()) {
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_block_points.Invalid"));
                    } else {
                        long blockPoints = island.getLevel().getMaterialPoints(materials.get().name());
                        returnValue = TextUtils.formatText(
                                placeholdersLoad.getString("Placeholders.fabledskyblock_island_level_block_points.Non-empty")
                                        .replace("{PLACEHOLDER}", NumberUtils.formatNumber(blockPoints)));
                    }
                }
            } else if (placeholder.toLowerCase().startsWith("fabledskyblock_level_block_value_")) {
                String materialName = placeholder.replace("fabledskyblock_level_block_value_", "").toUpperCase();
                Optional<XMaterial> materials = CompatibleMaterial.getMaterial(materialName);
                if (!materials.isPresent()) {
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_level_block_value.Invalid"));
                } else {
                    double blockValue = levellingManager.getWorth(materials.get());
                    returnValue = TextUtils.formatText(
                            placeholdersLoad.getString("Placeholders.fabledskyblock_level_block_value.Non-empty")
                                    .replace("{PLACEHOLDER}", NumberUtils.formatNumber(blockValue)));
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
