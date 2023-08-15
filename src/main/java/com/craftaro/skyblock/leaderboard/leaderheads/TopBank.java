package com.craftaro.skyblock.leaderboard.leaderheads;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.leaderboard.Leaderboard;
import com.craftaro.skyblock.leaderboard.Leaderboard.Type;
import com.craftaro.skyblock.visit.Visit;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TopBank extends DataCollector {
    private final SkyBlock plugin;

    public TopBank(SkyBlock plugin) {
        super("topbank", plugin.getDescription().getName(), BoardType.DEFAULT, "&bTop Bank", "topbank",
                Arrays.asList(ChatColor.DARK_GRAY + "-=+=-", ChatColor.AQUA + "{name}",
                        ChatColor.WHITE + "Bal: ${amount}", ChatColor.DARK_GRAY + "-=+=-"),
                true, UUID.class);

        this.plugin = plugin;
    }

    @Override
    public List<Entry<?, Double>> requestAll() {
        List<Leaderboard> leaderboards = this.plugin.getLeaderboardManager().getLeaderboard(Type.BANK);
        Map<UUID, Double> topLevels = new HashMap<>(leaderboards.size());

        for (Leaderboard leaderboard : leaderboards) {
            Visit visit = leaderboard.getVisit();
            topLevels.put(visit.getOwnerUUID(), visit.getBankBalance());
        }

        return LeaderHeadsAPI.sortMap(topLevels);
    }
}
